function formatarCashback(input) {
    // Remove tudo que não é dígito ou vírgula
    let valor = input.value.replace(/[^0-9,]/g, "");

    // Adiciona o símbolo de porcentagem
    if (valor) {
        valor = valor.replace(',', '.'); // Troca vírgula por ponto para manipulação numérica
        let valorNumerico = parseFloat(valor);
        
        // Formata o valor com duas casas decimais
        if (!isNaN(valorNumerico)) {
            let cashbackFormatado = valorNumerico.toLocaleString("pt-BR", { minimumFractionDigits: 1, maximumFractionDigits: 1 }) + "%";
            input.value = cashbackFormatado;
            
            // Salva o valor original no campo oculto para o envio
            document.getElementById('cashback').value = `+${valorNumerico.toFixed(1).replace('.', ',')} de cashback`;
        }
    } else {
        input.value = "";
        document.getElementById('cashback').value = ""; // Limpa o campo oculto
    }
}
