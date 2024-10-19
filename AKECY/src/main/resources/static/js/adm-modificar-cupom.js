var linhaEditando = null;
var dadosTemporarios = {};

function editarLinha(link) {
    if (linhaEditando) {
        return; // Impede edição se já houver uma linha em edição
    }

    var row = link.parentNode.parentNode;
    linhaEditando = row;
    var cells = row.querySelectorAll('.cell');
    var idCupom = row.querySelector('.status-cell')?.getAttribute('data-id');

    if (!idCupom) {
        console.error("Erro: status-cell ou idCupom não encontrados.");
        return;
    }

    // Verifica se os dados já existem antes de inicializar
    if (!dadosTemporarios[idCupom]) {
        dadosTemporarios[idCupom] = {};
    }

    for (var i = 0; i < 5; i++) {
        var cell = cells[i];
        var originalText = cell.innerText;
        var field = cell.getAttribute('data-field');
        cell.innerHTML = '<input type="text" id="' + field + '" class="' + field + '" value="' + originalText + '" name="' + field + '" maxlength="100">';
    }

    link.innerText = 'Salvar';
    link.onclick = function() { salvarLinha(this, idCupom); };
}

function salvarLinha(link, idCupom) {
    var row = link.parentNode.parentNode;
    var inputs = row.querySelectorAll('input');
    var data = dadosTemporarios[idCupom] || {};

    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        data[input.name] = input.value; // Armazena os dados
    }

    fetch('/AKECY/ADM/salvar-cupom/' + idCupom, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (response.ok) {
            // Atualiza a linha com os novos valores
            inputs.forEach(input => {
                var field = input.getAttribute('name');
                var cell = row.querySelector('.cell[data-field="' + field + '"]');
                if (cell) {
                    cell.innerHTML = data[field];
                }
            });

            // Reverte o link para "Editar"
            link.innerText = 'Editar';
            link.onclick = function() { editarLinha(this); };

            linhaEditando = null;
            delete dadosTemporarios[idCupom];

            // Recarrega a página
            location.reload(); // Recarrega a página para atualizar a lista
        } else {
            console.error('Erro ao salvar os dados.');
        }
    })
    .catch(error => {
        console.error('Erro na requisição:', error);
    });
}
