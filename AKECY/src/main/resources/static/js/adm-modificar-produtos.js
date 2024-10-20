function toggleStatusProduto(button) {
    var idProduto = button.getAttribute('data-id');
    var statusAtual = button.getAttribute('data-status');
    var novoStatus = statusAtual === 'ATIVO' ? 'INATIVO' : 'ATIVO';

    // Requisição para o backend para atualizar o status do produto
    fetch('/AKECY/ADM/mudar-status-produto/' + idProduto, { 
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ statusProd: novoStatus }) 
    })
    .then(response => {
        if (response.ok) {
            // Atualiza o botão com o novo status
            button.setAttribute('data-status', novoStatus);
            button.innerText = novoStatus;
        } else {
            console.error('Erro ao atualizar o status do produto.');
        }
    })
    .catch(error => {
        console.error('Erro na requisição:', error);
    });
}