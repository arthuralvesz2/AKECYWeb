document.addEventListener('DOMContentLoaded', function() {
    // Obtém a mensagem de erro do atributo do modelo Thymeleaf
    var mensagemErro = /*[[${error}]]*/ null; 

    // Verifica se há uma mensagem de erro
    if (mensagemErro) {
        document.getElementById('erro').textContent = mensagemErro;
    }

    // Adiciona o event listener para o submit do formulário
    document.getElementById('cupomForm').addEventListener('submit', function(event) {
        event.preventDefault(); 

        var form = this;
        var formData = new FormData(form);

        fetch(form.action, {
            method: form.method,
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(data => { // Analisa a resposta como JSON
                    throw new Error(data.error); // Lança um erro com a mensagem do backend
                });
            }
            return response.json(); // Analisa a resposta como JSON em caso de sucesso
        })
		.then(data => {
		    if (data.message) {
		        form.reset(); 
		        // Exibe um alert em vez de exibir a mensagem na página
		        alert(data.message); 
		        // Redireciona para a página modificar-cupons
		        window.location.href = '/AKECY/ADM/modificar-cupons'; 
		    }
		})
        .catch(error => {
            console.log(error); // Log do erro no console
            document.getElementById('erro').textContent = error.message; // Exibe a mensagem de erro
        });
    });
});