document.getElementById('cadastrarCupomBtn').addEventListener('click', function(event) {

    // Validação do desconto
    var descontoInput = document.getElementById('valor');
    if (descontoInput.value.trim() === "") {
        alert("Preencha o desconto do cupom");
        return;
    }

    // Validação da descrição
    var descricaoInput = document.getElementById('descricao');
    if (descricaoInput.value.trim() === "") {
        alert("Preencha a descrição do cupom");
        return;
    }

    // Validação do código
    var codigoInput = document.getElementById('codigo');
    if (codigoInput.value.trim() === "") {
        alert("Preencha o código do cupom");
        return;
    }

    // Se passou pelas validações, exibe o alerta de sucesso e redireciona
    alert("Cupom cadastrado com sucesso!");
    window.location.href = "/AKECY/ADM/modificar-cupons"; // Redireciona para a página de modificar cupons
});