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

function formatarCPF(event) {
	const input = document.getElementById('cpf');
    let value = input.value.replace(/\D/g, ''); // Remove todos os caracteres não numéricos

    value = value.replace(/^(\d{3})(\d)/, '$1.$2');
    value = value.replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3');
    value = value.replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');

    input.value = value;
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

    if (value.length === 10) {
        const [dia, mes, ano] = value.split('/');
        const hoje = new Date();
        const dataNasc = new Date(ano, mes - 1, dia);
        const idade = hoje.getFullYear() - dataNasc.getFullYear();
        const mesDiff = hoje.getMonth() - dataNasc.getMonth();
        if (mesDiff < 0 || (mesDiff === 0 && hoje.getDate() < dataNasc.getDate())) {
            idade--;
        }

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
        } else if (ano < 1900 || ano > hoje.getFullYear()) {
            input.setCustomValidity('Ano inválido. Insira um valor entre 1900 e o ano atual.');
        } else if (idade < 18) {
            input.setCustomValidity('Você deve ter pelo menos 18 anos.');
        } else {
            input.setCustomValidity('');
        }
    } else {
        input.setCustomValidity('');
    }
}
