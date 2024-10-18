function formatarMoeda(input) {
    let valor = input.value.replace(/\D/g, ""); // Remove tudo que não é dígito
    valor = valor.replace(/^0+/, ""); // Remove zeros à esquerda
    valor = valor.slice(0, 8); // Limita o valor a 8 dígitos

    if (valor === "") {
        input.value = "";
        document.getElementById('preco').value = ""; // Limpa o campo oculto
        return;
    }

    // Formata o valor como moeda para visualização
    let valorFormatado = (valor / 100).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
    input.value = valorFormatado.replace("R$", "R$ "); // Exibe o valor formatado

    // Salva o valor original no campo oculto para o envio
    document.getElementById('preco').value = (valor / 100).toFixed(2).replace('.', ',');
}