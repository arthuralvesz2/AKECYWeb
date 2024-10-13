function editarLinha(link) {
    var row = link.parentNode.parentNode;
    var cells = row.querySelectorAll('.cell');
    var idUsuario = row.querySelector('.status-cell').getAttribute('data-id');

    // Transformar células em inputs com classes (exceto a célula do sexo)
    for (var i = 0; i < 4; i++) { // Alterado de 6 para 4
        var cell = cells[i];
        var originalText = cell.innerText;
        var field = cell.getAttribute('data-field');

        // Adiciona a classe com o nome do field
        cell.innerHTML = '<input type="text" class="' + field + '" value="' + originalText + '" name="' + field + '">';
    }

    var sexoCell = row.querySelector('.sexo-cell');
    var sexoAtual = sexoCell.querySelector('span').innerText;
    sexoCell.innerHTML = '<button class="sexo-btn" data-sexo="' + sexoAtual + '" data-id="' + idUsuario + '" onclick="alternarSexo(this)">' + sexoAtual + '</button>';

    // Mudar o link para "Salvar"
    link.innerText = 'Salvar';
    link.onclick = function () { salvarLinha(this, idUsuario); };

    var statusCell = row.querySelector('.status-cell');
    var nivelCell = row.querySelector('.nivel-cell');

    var statusAtual = statusCell.innerText === 'Ativo' ? 'Ativo' : 'Inativo';
    statusCell.innerHTML = '<button class="status-btn" data-status="' + statusAtual + '" data-id="' + idUsuario + '" onclick="toggleStatus(this)">' + statusAtual + '</button>';

    var nivelAtual = nivelCell.innerText === 'Usuário' ? 'USER' : 'ADMIN';
    nivelCell.innerHTML = '<button class="nivel-btn" data-nivel="' + nivelAtual + '" data-id="' + idUsuario + '" onclick="mudarNivel(this)">' + (nivelAtual === 'USER' ? 'Usuário' : 'Admin') + '</button>';
}

function salvarLinha(link, idUsuario) {
    var row = link.parentNode.parentNode;
    var inputs = row.querySelectorAll('input');
    var data = {};
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        data[input.name] = input.value;
    }

    // Enviar dados para o servidor via AJAX
    fetch('/AKECY/ADM/salvar-usuario/' + idUsuario, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                // Atualizar a linha com os novos valores
                for (var i = 0; i < 6; i++) {
                    var cell = row.cells[i];
                    cell.innerHTML = data[cell.getAttribute('data-field')];
                }
                link.innerText = 'Editar';
                link.onclick = function () { editarLinha(this); };
            } else {
                console.error('Erro ao salvar os dados.');
            }
        });
}

function toggleStatus(button) {
    var idUsuario = button.getAttribute('data-id');
    var statusAtual = button.getAttribute('data-status');
    var novoStatus = statusAtual === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    var url = '/AKECY/ADM/' + (novoStatus === 'ATIVO' ? 'enable' : 'disable') + '/' + idUsuario;

    fetch(url, { method: 'POST' })
        .then(response => {
            if (response.ok) {
                button.setAttribute('data-status', novoStatus);
                button.innerHTML = novoStatus === 'ATIVO' ? 'Ativo' : 'Inativo';
            } else {
                console.error('Erro ao alterar o status.');
            }
        });
}

function mudarNivel(button) {
    var idUsuario = button.getAttribute('data-id');
    var nivelAtual = button.getAttribute('data-nivel');
    var novoNivel = nivelAtual === 'USER' ? 'ADMIN' : 'USER';
    var url = '/AKECY/ADM/mudar-nivel/' + idUsuario;

    fetch(url, { method: 'POST' })
        .then(response => {
            if (response.ok) {
                button.setAttribute('data-nivel', novoNivel);
                button.innerHTML = novoNivel === 'USER' ? 'Usuário' : 'Admin';
            } else {
                console.error('Erro ao mudar o nível de acesso.');
            }
        });
}

function alternarSexo(button) {
    var idUsuario = button.getAttribute('data-id');
    var sexoAtual = button.getAttribute('data-sexo');
    var novoSexo;

    switch (sexoAtual) {
        case 'Masculino':
            novoSexo = 'Feminino';
            break;
        case 'Feminino':
            novoSexo = 'Prefiro não informar';
            break;
        case 'Prefiro não informar':
            novoSexo = 'Masculino';
            break;
        default:
            novoSexo = 'Masculino'; // Valor padrão caso o sexoAtual seja inválido
    }

    var url = '/AKECY/ADM/mudar-sexo/' + idUsuario + '/' + novoSexo;

    fetch(url, { method: 'POST' })
        .then(response => {
            if (response.ok) {
                button.setAttribute('data-sexo', novoSexo);
                button.innerHTML = novoSexo;
            } else {
                console.error('Erro ao alterar o sexo.');
            }
        });
}