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

	// Loop para tornar os campos editáveis
	for (var i = 0; i < 4; i++) { // Modificado para 4, pois o status é um botão
		var cell = cells[i];
		var originalText = cell.innerText;
		var field = cell.getAttribute('data-field');
		cell.innerHTML = '<input type="text" id="' + field + '" class="' + field + '" value="' + originalText + '" name="' + field + '" maxlength="100">';
	}

	// Status do Cupom
	var statusCell = row.querySelector('.status-cell');
	var statusAtual = statusCell.innerText === 'ATIVO' ? 'ATIVO' : 'INATIVO';
	statusCell.innerHTML = '<button class="status-btn" data-status="' + statusAtual + '" data-id="' + idCupom + '" onclick="toggleStatus(this)">' + (statusAtual === 'ATIVO' ? 'ATIVO' : 'INATIVO') + '</button>';

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

	// Obter o status do cupom do botão
	var statusAtual = row.querySelector('.status-cell .status-btn').getAttribute('data-status');
	data.statusCupom = statusAtual === 'ATIVO' ? 'ATIVO' : 'INATIVO';

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

				// Atualiza o status do cupom
				var statusCell = row.querySelector('.status-cell');
				statusCell.innerHTML = data.statusCupom;

				// Reverte o link para "Editar"
				link.innerText = 'Editar';
				link.onclick = function() { editarLinha(this); };

				linhaEditando = null;
				delete dadosTemporarios[idCupom];

				// Recarrega a página
				location.reload();
			} else {
				// Trata o erro retornado pelo servidor
				response.json().then(data => {
					if (data.error) {
						alert(data.error); // Exibe o alerta com a mensagem de erro
					} else {
						console.error('Erro ao salvar os dados.');
					}
				});
			}
		})
		.catch(error => {
			console.error('Erro na requisição:', error);
		});
}

function toggleStatus(button) {
	var idCupom = button.getAttribute('data-id');
	var statusAtual = button.getAttribute('data-status');
	var novoStatus = statusAtual === 'ATIVO' ? 'INATIVO' : 'ATIVO';

	button.setAttribute('data-status', novoStatus);
	button.innerHTML = novoStatus === 'ATIVO' ? 'ATIVO' : 'INATIVO';

	// Atualizando o valor do status no objeto temporário
	dadosTemporarios[idCupom].statusCupom = novoStatus === 'ATIVO' ? 'ATIVO' : 'INATIVO';
}