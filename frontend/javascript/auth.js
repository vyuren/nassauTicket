const API_BASE_URL = `http://${window.location.hostname || "localhost"}:8080`;
const loginPages = {
    CLIENTE: "../html/loginCliente.html",
    ATENDENTE: "../html/loginAtendente.html"
};
const panelPages = {
    CLIENTE: "../html/painelCliente.html",
    ATENDENTE: "../html/painelAtendente.html"
};

function showMessage(message) {
    const element = document.getElementById("auth-message");
    if (element) {
        element.textContent = message;
    }
}

function navigateTo(relativePath) {
    window.location.replace(new URL(relativePath, document.baseURI));
}

function navigateToPanel(profile) {
    const path = panelPages[profile];
    if (!path) {
        throw new Error("Perfil de usuário inválido.");
    }
    navigateTo(path);
}

async function requestApi(path, options = {}) {
    let response;
    try {
        response = await fetch(`${API_BASE_URL}${path}`, {
            ...options,
            credentials: "include",
            headers: {
                ...(options.body ? { "Content-Type": "application/json" } : {}),
                ...options.headers
            }
        });
    } catch (error) {
        throw new Error("Não foi possível conectar ao servidor. Verifique se o backend está ativo.");
    }

    if (!response.ok) {
        let message = "Não foi possível concluir a operação.";
        try {
            const body = await response.json();
            message = body.detail || body.message || message;
        } catch {
            // Use the generic message when the server does not return JSON.
        }
        throw new Error(message);
    }

    if (response.status === 204) {
        return null;
    }
    return response.json();
}

async function getCurrentSession() {
    const response = await fetch(`${API_BASE_URL}/auth/sessao`, {
        credentials: "include"
    });
    if (response.status === 401) {
        return null;
    }
    if (!response.ok) {
        throw new Error("Não foi possível verificar sua sessão.");
    }
    return response.json();
}

async function checkSessionOnPage() {
    const page = document.body.dataset.authPage;
    const expectedProfile = document.body.dataset.profile;
    try {
        const user = await getCurrentSession();

        if (page === "home" && user) {
            navigateToPanel(user.perfil);
            return;
        }

        if ((page === "login" || page === "register") && user) {
            navigateToPanel(user.perfil);
            return;
        }

        if (page === "panel") {
            if (!user) {
                navigateTo(loginPages[expectedProfile]);
                return;
            }
            if (user.perfil !== expectedProfile) {
                navigateToPanel(user.perfil);
                return;
            }
            const name = document.getElementById("displayNome");
            const id = document.getElementById("displayId");
            if (name) {
                name.textContent = `Olá, ${user.nome}`;
            }
            if (id) {
                id.textContent = `ID: ${user.id}`;
            }
            setupTicketPanel(user);
        }
    } catch (error) {
        showMessage(error.message);
    }
}

const ticketTypeLabels = {
    PRIORITARIA: "Prioritário",
    GERAL: "Geral",
    EXAME: "Exame"
};
const ticketStatusLabels = {
    ABERTA: "Aberto",
    EM_ATENDIMENTO: "Em atendimento",
    FECHADA: "Fechado"
};

function showTicketMessage(message) {
    const element = document.getElementById("ticket-message");
    if (element) {
        element.textContent = message;
    }
}

function setupTicketPanel(user) {
    const board = document.getElementById("ticket-board");
    if (!board) {
        return;
    }

    document.getElementById("refresh-tickets")?.addEventListener("click", () => {
        loadLatestTickets(user);
    });

    const form = document.getElementById("ticket-form");
    form?.addEventListener("submit", async event => {
        event.preventDefault();
        const submit = document.getElementById("ticket-submit");
        const tipo = form.elements.tipo.value;
        if (submit) {
            submit.disabled = true;
        }
        showTicketMessage("Abrindo chamado...");
        try {
            const result = await requestApi(`/senha/${encodeURIComponent(tipo)}`, { method: "POST" });
            showTicketMessage(`Chamado aberto. Sua senha é ${result.codigoSenha}.`);
            await loadLatestTickets(user);
        } catch (error) {
            showTicketMessage(error.message);
        } finally {
            if (submit) {
                submit.disabled = false;
            }
        }
    });

    loadLatestTickets(user);
}

async function loadLatestTickets(user) {
    const board = document.getElementById("ticket-board");
    if (!board) {
        return;
    }

    board.replaceChildren();
    const loading = document.createElement("li");
    loading.textContent = "Carregando chamados...";
    board.append(loading);

    try {
        const tickets = await requestApi("/senha/ultimos");
        board.replaceChildren();
        if (tickets.length === 0) {
            const empty = document.createElement("li");
            empty.textContent = "Ainda não há chamados.";
            board.append(empty);
            return;
        }
        tickets.forEach(ticket => board.append(createTicketCard(ticket, user)));
    } catch (error) {
        board.replaceChildren();
        const failure = document.createElement("li");
        failure.textContent = error.message;
        board.append(failure);
    }
}

function createTicketCard(ticket, user) {
    const card = document.createElement("li");
    card.className = "ticket-card";

    const title = document.createElement("h3");
    title.textContent = ticket.codigoSenha;
    card.append(title);

    const details = [
        `Tipo: ${ticketTypeLabels[ticket.tipo] || ticket.tipo}`,
        `Status: ${ticketStatusLabels[ticket.status] || ticket.status}`,
        `Cliente: ${ticket.clienteNome || "Não informado"}`,
        `Data: ${ticket.dataEmissao.split("-").reverse().join("/")}`
    ];
    if (ticket.atendenteNome) {
        details.push(`Atendente: ${ticket.atendenteNome}`);
    }
    details.forEach(detail => {
        const paragraph = document.createElement("p");
        paragraph.textContent = detail;
        card.append(paragraph);
    });

    if (user.perfil === "ATENDENTE") {
        const isAssignedAttendant = ticket.atendenteId === user.id;
        const action = ticket.status === "ABERTA"
            ? { label: "Atender chamado", path: `/senha/${ticket.id}/atender` }
            : ticket.status === "EM_ATENDIMENTO" && isAssignedAttendant
                ? { label: "Fechar chamado", path: `/senha/${ticket.id}/fechar` }
                : null;

        if (action) {
            const button = document.createElement("button");
            button.className = "ticket-action";
            button.type = "button";
            button.textContent = action.label;
            button.addEventListener("click", async () => {
                button.disabled = true;
                showTicketMessage(`${action.label}...`);
                try {
                    await requestApi(action.path, { method: "POST" });
                    showTicketMessage(ticket.status === "ABERTA"
                        ? "Chamado atribuído a você."
                        : "Chamado fechado.");
                    await loadLatestTickets(user);
                } catch (error) {
                    showTicketMessage(error.message);
                    button.disabled = false;
                }
            });
            card.append(button);
        }
    }

    return card;
}

function setupAuthenticationForm() {
    const page = document.body.dataset.authPage;
    const profile = document.body.dataset.profile;
    const form = document.getElementById(page === "login" ? "login" : "cadastro");
    if (!form) {
        return;
    }

    form.addEventListener("submit", async event => {
        event.preventDefault();
        const isLogin = page === "login";
        const payload = isLogin
            ? {
                email: form.elements.email.value.trim(),
                senha: form.elements.senha.value
            }
            : {
                nome: form.elements.nome.value.trim(),
                nascimento: form.elements.nascimento.value,
                cpf: form.elements.cpf.value.trim(),
                email: form.elements.email.value.trim(),
                senha: form.elements.senha.value
            };
        const route = profile === "CLIENTE" ? "/clientes" : "/atendentes";

        try {
            const user = await requestApi(`${route}/${isLogin ? "logar" : "criar"}`, {
                method: "POST",
                body: JSON.stringify(payload)
            });
            navigateToPanel(user.perfil);
        } catch (error) {
            showMessage(error.message);
        }
    });
}

function setupLogout() {
    const button = document.getElementById("logout");
    if (!button) {
        return;
    }

    button.addEventListener("click", async () => {
        try {
            await requestApi("/auth/sair", { method: "POST" });
            navigateTo(loginPages[document.body.dataset.profile]);
        } catch (error) {
            showMessage(error.message);
        }
    });
}

checkSessionOnPage();
setupAuthenticationForm();
setupLogout();
