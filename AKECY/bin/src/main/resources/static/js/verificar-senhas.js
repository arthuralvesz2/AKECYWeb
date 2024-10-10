function verificarSenhas() {
	const senha1 = document.getElementById('senha1').value;
	const senha2 = document.getElementById('senha2').value;
	const mensagemSenha = document.getElementById('mensagemSenha');

	if (senha1 === senha2) {
		mensagemSenha.textContent = '';
		return true;
	} else {
		mensagemSenha.textContent = 'As senhas não coincidem. Por favor, verifique.';
		return false;
	}
}
