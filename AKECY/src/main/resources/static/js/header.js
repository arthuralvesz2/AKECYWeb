document.querySelector("#theme-checkbox").addEventListener("change", () => {
    document.body.classList.toggle("dark");

    if (document.body.classList.contains("dark")) {
        localStorage.setItem("theme", "dark");
    } else {
        localStorage.setItem("theme", "light");
    }
});

window.addEventListener("load", () => {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme === "dark") {
        document.body.classList.add("dark");
        document.querySelector("#theme-checkbox").checked = true;
    }
});

function performSearch(event) {
  event.preventDefault();
  const searchTerm = document.getElementById('searchInput').value;
  window.location.href = `/AKECY/produto/buscar?q=${searchTerm}`;
}