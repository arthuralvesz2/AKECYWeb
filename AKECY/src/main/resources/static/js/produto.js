    window.onload = function() {
        const productId = getIdFromUrl(); 

        fetch('/produto/' + productId)
            .then(response => response.json())
            .then(produto => {
                document.getElementById('item-display').src = 'data:image/jpeg;base64,' + produto.base64Image;
                document.querySelector('.product-title').textContent = produto.nome;
                document.querySelector('.product-desc').textContent = produto.descricao;
                document.querySelector('.product-price').textContent = 'R$ ' + produto.preco;
                document.querySelector('.product-sizes').textContent = produto.tamanhos_disponiveis;
                document.querySelector('.product-info section').textContent = produto.descricao_completa;

                const fotos = [produto.foto2, produto.foto3, produto.foto4, produto.foto5];
                for (let i = 0; i < fotos.length; i++) {
                    if (fotos[i]) {
                        const base64Image = Base64.getEncoder().encodeToString(fotos[i]);
                        document.getElementById('item-' + (i + 1)).src = 'data:image/jpeg;base64,' + base64Image;
                    }
                }
            })
            .catch(error => console.error('Erro ao buscar o produto:', error));
    };

    function getIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('id'); 
    }