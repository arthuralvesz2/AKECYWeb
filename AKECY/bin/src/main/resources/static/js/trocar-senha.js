const senhaInput = document.getElementById('novaSenha');


senhaInput.addEventListener('input', () => {
	if (senhaInput.validity.tooShort) {
		senhaInput.setCustomValidity('A senha deve ter no mínimo 7 caracteres.');
	} else if (senhaInput.validity.patternMismatch) {
		if (!/[a-z]/.test(senhaInput.value)) {
			senhaInput.setCustomValidity('A senha deve conter pelo menos uma letra minúscula.');
		} else if (!/[A-Z]/.test(senhaInput.value)) {
			senhaInput.setCustomValidity('A senha deve conter pelo menos uma letra maiúscula.');
		} else if (!/\d/.test(senhaInput.value)) {
			senhaInput.setCustomValidity('A senha deve conter pelo menos um número.');
		} else if (!/[@$!%*?&]/.test(senhaInput.value)) {
			senhaInput.setCustomValidity('A senha deve conter pelo menos um caractere especial.');
		} else {
			senhaInput.setCustomValidity('');
		}
	} else {
		senhaInput.setCustomValidity('');
	}
});

function verificarSenhas() {
	const senha1 = document.getElementById('novaSenha').value;
	const senha2 = document.getElementById('novaSenhaConfirmacao').value;
	const mensagemSenha = document.getElementById('mensagemSenha');

	if (senha1 === senha2) {
		mensagemSenha.textContent = '';
		return true;
	} else {
		const mensagem = document.getElementById('mensagem');
		mensagem.textContent = 'As senhas não coincidem. Por favor, verifique.';
		return false;
	}
}