function formatarMoeda(input) {
    let valor = input.value.replace(/\D/g, "");
    valor = valor.replace(/^0+/, "");

    valor = valor.slice(0, 3);

    if (valor === "") {
        input.value = "";
        document.getElementById('desconto').value = "";
        return;
    }

    // Adiciona "R$ " ao valor
    let valorFormatado = "R$ " + valor;
    input.value = valorFormatado;

    // Salva o valor formatado no campo oculto para o envio
    document.getElementById('desconto').value = `R$${valor} OFF`;
}

function formatarCashback(input) {
    let valor = input.value.replace(/\D/g, ""); // Remove tudo que não for dígito
    valor = valor.replace(/^0+/, ""); // Remove zeros à esquerda

    // Limita o valor a 2 dígitos antes da vírgula e 1 depois
    if (valor.length > 3) {
        valor = valor.slice(0, 3);
    }

    if (valor.length > 1) {
        valor = valor.slice(0, 1) + "," + valor.slice(1);
    }

    input.value = valor; // Atualiza o valor no input

    // Salva o valor formatado no campo oculto para o envio
    document.getElementById('cashback').value = valor ? `+ ${valor}% de cashback` : "";
}

function formatarCodigo(input) {
    input.value = input.value.replace(/\s+/g, '').toUpperCase();
}