const API_BASE_URL = "http://localhost:8080";
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
        }
    } catch (error) {
        showMessage(error.message);
    }
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
