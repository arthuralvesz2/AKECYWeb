var linhaEditando = null;
var dadosTemporarios = {};

function editarLinha(link) {
    if (linhaEditando) {
        return;
    }

    var row = link.parentNode.parentNode;
    linhaEditando = row;
    var cells = row.querySelectorAll('.cell');
    var idUsuario = row.querySelector('.status-cell')?.getAttribute('data-id');

    if (!idUsuario) {
        console.error("Erro: status-cell ou idUsuario não encontrados.");
        return;
    }

    // Verifica se os dados já existem antes de inicializar
    if (!dadosTemporarios[idUsuario]) {
        dadosTemporarios[idUsuario] = {};
    }

    for (var i = 0; i < 5; i++) {
        var cell = cells[i];
        var originalText = cell.innerText;
        var field = cell.getAttribute('data-field');
        cell.innerHTML = '<input type="text" id="' + field + '" class="' + field + '" value="' + originalText + '" name="' + field + '" maxlength="100">';
    }
	
    // Aplicando formatações no CPF, Telefone e Data de Nascimento
    var cpfInput = row.querySelector('.cell[data-field="cpf"] input');
    cpfInput.oninput = formatarCPF;
    cpfInput.maxLength = 14;

    var telefoneInput = row.querySelector('.cell[data-field="telefone"] input');
    telefoneInput.maxLength = 16;
    telefoneInput.oninput = formatarTelefone;

    row.querySelector('.cell[data-field="dataNasc"] input').oninput = formatarData;

    // Sexo, Status e Nível
    var sexoCell = row.querySelector('.sexo-cell');
    var sexoAtual = sexoCell?.querySelector('span')?.innerText || 'Não definido';
    sexoCell.innerHTML = '<button class="sexo-btn" data-sexo="' + sexoAtual + '" data-id="' + idUsuario + '" onclick="alternarSexo(this)">' + sexoAtual + '</button>';

    var statusCell = row.querySelector('.status-cell');
    var statusAtual = statusCell.innerText === 'ATIVO' ? 'ATIVO' : 'INATIVO';
    statusCell.innerHTML = '<button class="status-btn" data-status="' + statusAtual + '" data-id="' + idUsuario + '" onclick="toggleStatus(this)">' + (statusAtual === 'ATIVO' ? 'ATIVO' : 'INATIVO') + '</button>';

    var nivelCell = row.querySelector('.nivel-cell');
    var nivelAtual = nivelCell.innerText === 'USUÁRIO' ? 'USER' : 'ADMIN';
    nivelCell.innerHTML = '<button class="nivel-btn" data-nivel="' + nivelAtual + '" data-id="' + idUsuario + '" onclick="mudarNivel(this)">' + (nivelAtual === 'USER' ? 'USUÁRIO' : 'ADMIN') + '</button>';

    link.innerText = 'Salvar';
    link.onclick = function() { salvarLinha(this, idUsuario); };
}

function salvarLinha(link, idUsuario) {
    var row = link.parentNode.parentNode;
    var inputs = row.querySelectorAll('input');
    var data = dadosTemporarios[idUsuario] || {};
    var camposObrigatorios = ['nome', 'cpf', 'dataNasc'];

    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];

        // Validação de campos obrigatórios
        if (camposObrigatorios.includes(input.name) && input.value.trim() === '') {
            alert(input.name.charAt(0).toUpperCase() + input.name.slice(1) + " é um campo obrigatório.");
            return;
        }

        // Validação de CPF
        if (input.name === "cpf" && input.value.length !== 14) {
            alert("O CPF deve ter exatamente 14 caracteres no formato '###.###.###-##'.");
            return;
        }

        // Validação de telefone
        if (input.name === "telefone" && input.value.replace(/\D/g, '').length < 11) {
            alert("Insira todos os dígitos do telefone.");
            return;
        }

        // Validação de Email
        if (input.name === "email" && (!input.value.includes("@") || input.value.split("@")[1].length === 0)) {
            alert("Por favor, insira um e-mail válido contendo '@' e algo depois.");
            return;
        }

        // Validação de Data de Nascimento
        if (input.name === "dataNasc") {
            if (input.value.length < 10) {
                alert("Por favor, insira uma data completa no formato DD/MM/AAAA.");
                return;
            }

            const [dia, mes, ano] = input.value.split('/').map(Number);

            if (dia < 1 || dia > 31) {
                alert("Dia inválido. Insira um valor entre 1 e 31.");
                return;
            }
            if (mes < 1 || mes > 12) {
                alert("Mês inválido. Insira um valor entre 1 e 12.");
                return;
            }
            if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
                alert("Dia inválido para o mês selecionado.");
                return;
            }
            if (mes == 2 && dia > 29) {
                alert("Dia inválido para fevereiro.");
                return;
            }
            if (mes == 2 && dia > 28 && !(ano % 4 === 0 && (ano % 100 !== 0 || ano % 400 === 0))) {
                alert("Ano não bissexto, 29 de fevereiro não é válido.");
                return;
            }

            const dataNascimento = new Date(ano, mes - 1, dia);
            const idade = new Date().getFullYear() - dataNascimento.getFullYear();

            if (idade < 18 || (idade === 18 && new Date() < new Date(new Date().getFullYear(), mes - 1, dia))) {
                alert("O usuário deve ter pelo menos 18 anos.");
                return;
            }
        }

        data[input.name] = input.value;
    }

    var sexoCell = row.querySelector('.sexo-cell');
    data.sexo = sexoCell.querySelector('button').getAttribute('data-sexo');
    
    // Atualiza status e nível
    var statusAtual = row.querySelector('.status-cell .status-btn').getAttribute('data-status');
    data.statusUsuario = statusAtual === 'ATIVO' ? 'ATIVO' : 'INATIVO'; // Certifique-se que a lógica aqui está correta

    var nivelAtual = row.querySelector('.nivel-cell .nivel-btn').getAttribute('data-nivel');
    data.nivelAcesso = nivelAtual;

    fetch('/AKECY/ADM/salvar-usuario/' + idUsuario, {
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
                var cell = row.querySelector(`.cell[data-field="${field}"]`);
                if (cell) {
                    cell.innerHTML = data[field];
                }
            });

            // Atualiza Sexo, Status e Nível
            var sexoCell = row.querySelector('.sexo-cell');
            sexoCell.innerHTML = data.sexo || sexoCell.querySelector('button').getAttribute('data-sexo') || 'Não definido';

            var statusCell = row.querySelector('.status-cell');
            statusCell.innerHTML = data.statusUsuario || 'INATIVO';

            var nivelCell = row.querySelector('.nivel-cell');
            nivelCell.innerHTML = data.nivelAcesso === 'USER' ? 'USUÁRIO' : 'ADMIN'; // Alterado aqui

            // Reverte o link para "Editar"
            link.innerText = 'Editar';
            link.onclick = function() { editarLinha(this); };

            linhaEditando = null;
            delete dadosTemporarios[idUsuario];

            // Recarrega a página
            location.reload(); // Adiciona esta linha para recarregar a página
        } else {
            console.error('Erro ao salvar os dados.');
        }
    })
    .catch(error => {
        console.error('Erro na requisição:', error);
    });
}


function alternarSexo(button) {
    var idUsuario = button.getAttribute('data-id');
    var sexoAtual = button.getAttribute('data-sexo');
    var novoSexo = sexoAtual === 'Masculino' ? 'Feminino' : (sexoAtual === 'Feminino' ? 'Prefiro não informar' : 'Masculino');
    
    button.setAttribute('data-sexo', novoSexo);
    button.innerHTML = novoSexo;

    // Atualiza o valor no objeto temporário
    dadosTemporarios[idUsuario].sexo = novoSexo; // Certifique-se de que esta linha esteja presente
}


function toggleStatus(button) {
    var idUsuario = button.getAttribute('data-id');
    var statusAtual = button.getAttribute('data-status');
    var novoStatus = statusAtual === 'ATIVO' ? 'INATIVO' : 'ATIVO';
    
    button.setAttribute('data-status', novoStatus);
    button.innerHTML = novoStatus === 'ATIVO' ? 'ATIVO' : 'INATIVO';
    
    // Atualizando o valor do status no objeto temporário
    dadosTemporarios[idUsuario].statusUsuario = novoStatus === 'ATIVO' ? 'ATIVO' : 'INATIVO';
}

function mudarNivel(button) {
    var idUsuario = button.getAttribute('data-id');
    var nivelAtual = button.getAttribute('data-nivel');
    var novoNivel = nivelAtual === 'USER' ? 'ADMIN' : 'USER';
    
    button.setAttribute('data-nivel', novoNivel);
    button.innerHTML = novoNivel === 'USER' ? 'USUÁRIO' : 'ADMIN';
    
    // Atualizando o nível no objeto temporário
    dadosTemporarios[idUsuario].nivelAcesso = novoNivel;
}


// Funções para formatar CPF, Telefone e Data de Nascimento
function formatarCPF(event) {
    var input = event.target;
    var value = input.value.replace(/\D/g, '');
    if (value.length > 11) value = value.slice(0, 11);
    if (value.length <= 3) {
        input.value = value;
    } else if (value.length <= 6) {
        input.value = value.replace(/(\d{3})(\d+)/, '$1.$2');
    } else if (value.length <= 9) { 
        input.value = value.replace(/(\d{3})(\d{3})(\d+)/, '$1.$2-$3');
    } else {
        input.value = value.replace(/(\d{3})(\d{3})(\d{3})(\d+)/, '$1.$2.$3-$4');
    }
}

function formatarTelefone(event) {
    var input = event.target;
    var value = input.value.replace(/\D/g, ''); // Remove todos os caracteres não numéricos
    if (value.length > 11) value = value.slice(0, 11); // Limita o número de dígitos a 11
    
    if (value.length <= 2) {
        input.value = value.replace(/(\d{2})/, '($1'); // Adiciona o parêntese no DDD
    } else if (value.length <= 7) {
        input.value = value.replace(/(\d{2})(\d{0,5})/, '($1) $2'); // Formato para até o quinto dígito
    } else {
        input.value = value.replace(/(\d{2})(\d{1})(\d{4})(\d{0,4})/, '($1) $2 $3-$4'); // Formato completo
    }
}

function formatarData(event) {
    var input = event.target;
    var value = input.value.replace(/\D/g, '');
    if (value.length > 8) value = value.slice(0, 8);
    if (value.length <= 2) {
        input.value = value;
    } else if (value.length <= 4) {
        input.value = value.replace(/(\d{2})(\d+)/, '$1/$2');
    } else {
        input.value = value.replace(/(\d{2})(\d{2})(\d+)/, '$1/$2/$3');
    }
}
