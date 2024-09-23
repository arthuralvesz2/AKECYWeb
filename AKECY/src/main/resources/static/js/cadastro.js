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

const VerificarSenha = document.getElementById('senha1');

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

function formatarCPF() {
	const input = document.getElementById('cpf');
	let value = input.value.replace(/\D/g, '');

	if (value.length >= 3) {
		value = value.slice(0, 3) + '.' + value.slice(3);
	}
	if (value.length >= 7) {
		value = value.slice(0, 7) + '.' + value.slice(7);
	}
	if (value.length >= 11) {
		value = value.slice(0, 11) + '-' + value.slice(11);
	}

	input.value = value;
}

function formatarTelefone() {
	const telefoneInput = document.getElementById('telefone');
	let telefone = telefoneInput.value.replace(/\D/g, '');

	if (telefone.length >= 2) {
		telefone = `(${telefone.slice(0, 2)}) ${telefone.slice(2)}`;
	}
	if (telefone.length >= 6) {
		telefone = telefone.slice(0, 6) + ' ' + telefone.slice(6);
	}
	if (telefone.length >= 11) {
		telefone = telefone.slice(0, 11) + '-' + telefone.slice(11);
	}

	telefoneInput.value = telefone;
}

function formatarData() {
	const input = document.getElementById('dataNasc');
	let value = input.value.replace(/\D/g, '');

	if (value.length > 2) {
		value = value.slice(0, 2) + '/' + value.slice(2);
	}
	if (value.length > 5) {
		value = value.slice(0, 5) + '/' + value.slice(5, 9);
	}

	input.value = value;

	if (value.length === 10) {
		const [dia, mes, ano] = value.split('/');

		if (dia < 1 || dia > 31) {
			input.setCustomValidity('Dia inválido. Insira um valor entre 1 e 31.');
		} else if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia > 30) {
			input.setCustomValidity('Dia inválido para o mês selecionado.');
		} else if (mes == 2 && dia > 29) {
			input.setCustomValidity('Dia inválido para fevereiro.');
		} else if (mes == 2 && dia > 28 && (ano % 4 != 0 || (ano % 100 == 0 && ano % 400 != 0))) {
			input.setCustomValidity('Dia inválido para fevereiro em ano não bissexto.');

		} else if (mes < 1 || mes > 12) {
			input.setCustomValidity('Mês inválido. Insira um valor entre 1 e 12.');

		} else {
			const hoje = new Date();
			const dataNasc = new Date(ano, mes - 1, dia);
			const idade = hoje.getFullYear() - dataNasc.getFullYear();
			const mesDiff = hoje.getMonth() - dataNasc.getMonth();
			if (mesDiff < 0 || (mesDiff === 0 && hoje.getDate() < dataNasc.getDate())) {
				idade--;
			}
			if (ano < 1900 || ano > hoje.getFullYear()) {
				input.setCustomValidity('Ano inválido. Insira um valor entre 1900 e o ano atual.');
			} else if (idade < 18) {
				input.setCustomValidity('Você deve ter pelo menos 18 anos.');
			} else {
				input.setCustomValidity('');
			}
		}
	} else {
		input.setCustomValidity('');
	}
}