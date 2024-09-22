function verificarSenhas() {
    const senha1 = document.getElementById('senha').value;
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

function formatarCPF(cpf) {
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
    const telefoneInput = document.getElementById('tele');
    let telefone = telefoneInput.value.replace(/\D/g, '');
    
    if (telefone.length >= 2) {
        telefone = `(${telefone.slice(0, 2)}) ${telefone.slice(2)}`;
    }
    if (telefone.length >= 10) {
        telefone = telefone.slice(0, 10) + '-' + telefone.slice(10);
    }


    telefoneInput.value = telefone;
}