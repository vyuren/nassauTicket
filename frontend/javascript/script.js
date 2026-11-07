const API_BASE_URL = `http://${window.location.hostname || "localhost"}:8080`;

fetch(`${API_BASE_URL}/auth/sessao`, { credentials: "include" })
    .then(response => {
        if (response.status === 401) {
            return null;
        }
        if (!response.ok) {
            throw new Error("Não foi possível verificar sua sessão.");
        }
        return response.json();
    })
    .then(user => {
        if (user) {
            const page = user.perfil === "CLIENTE"
                ? "html/painelCliente.html"
                : "html/painelAtendente.html";
            window.location.replace(page);
        }
    })
    .catch(() => {
        const message = document.getElementById("auth-message");
        if (message) {
            message.textContent = "Não foi possível verificar sua sessão. Tente novamente.";
        }
    });