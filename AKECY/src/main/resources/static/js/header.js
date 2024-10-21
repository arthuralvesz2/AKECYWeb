// Event listener para alternar entre os temas
document.querySelector("#theme-checkbox").addEventListener("change", () => {
    document.body.classList.toggle("dark");

    if (document.body.classList.contains("dark")) {
        localStorage.setItem("theme", "dark");
    } else {
        localStorage.setItem("theme", "light");
    }
});

// Carrega o tema salvo do localStorage
window.addEventListener("load", () => {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark");
        document.querySelector("#theme-checkbox").checked = true;
    } else {
        // Modo claro por padrão
        document.body.classList.remove("dark");
        document.querySelector("#theme-checkbox").checked = false;
    }
});
