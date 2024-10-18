function formatarCashback(input) {
    let valor = input.value.replace(/\D/g, ""); // Remove tudo que não é dígito
    valor = valor.replace(/^0+/, ""); // Remove zeros à esquerda

    if (valor === "") {
        input.value = "";
        document.getElementById('cashback').value = ""; // Limpa o campo oculto
        return;
    }

    // Limita o valor a 3 dígitos (para porcentagens)
    valor = valor.slice(0, 3); 

    // Formata o valor como porcentagem
    let valorFormatado = (valor / 100).toLocaleString("pt-BR", { style: "percent", minimumFractionDigits: 1 });
    input.value = valorFormatado.replace(".", ","); // Exibe o valor formatado

    // Salva o valor original no campo oculto para o envio
    document.getElementById('cashback').value = `+ ${valor / 100},0% de cashback`;
}
