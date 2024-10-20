function toggleStatusMensagem(button) {
	var idMensagem = button.getAttribute('data-id');
	var statusAtual = button.getAttribute('data-status');
	var novoStatus = statusAtual === 'RESPONDIDO' ? 'PENDENTE' : 'RESPONDIDO';

	button.setAttribute('data-status', novoStatus);
	button.innerHTML = novoStatus;

	fetch('/AKECY/ADM/mensagens/' + idMensagem, {
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ statusMensagem: novoStatus })
	})
		.then(response => {
			if (!response.ok) {
				console.error('Erro ao atualizar status da mensagem.');
			}
		})
		.catch(error => {
			console.error('Erro na requisição:', error);
		});

	setTimeout(function() {
		location.reload();
	}, 1000); // 2000 milissegundos = 2 segundos

}

document.addEventListener('DOMContentLoaded', (event) => {
	const statusCells = document.querySelectorAll('.status-cell');
	statusCells.forEach(cell => {
		const idMensagem = cell.getAttribute('data-id');
		const statusAtual = cell.innerText;
		cell.innerHTML = `<button class="status-btn" data-status="${statusAtual}" data-id="${idMensagem}" onclick="toggleStatusMensagem(this)">${statusAtual}</button>`;
	});
});