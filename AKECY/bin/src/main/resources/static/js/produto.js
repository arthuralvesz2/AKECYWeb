document.addEventListener('DOMContentLoaded', function() { 

  const mainImage = document.getElementById('main-product-image'); 
  mainImage.src = document.getElementById('foto-1').src;

  document.getElementById('foto-1').addEventListener('click', function() {
    mainImage.src = this.src;
  });


  document.getElementById('foto-2').addEventListener('click', function() {
    mainImage.src = this.src;
  });

  document.getElementById('foto-3').addEventListener('click', function() {
    mainImage.src = this.src;
  });

  document.getElementById('foto-4').addEventListener('click', function() {
    mainImage.src = this.src;
  });

  document.getElementById('foto-5').addEventListener('click', function() {
    mainImage.src = this.src;
  });
});

function favoritarProduto(idProduto) {
  fetch('/AKECY/favorito/alterar?idProduto=' + idProduto, { method: 'POST' })
    .then(response => {
      if (response.ok) {
        return response.text(); 
      } else if (response.status === 401) {
        window.location.href = '/AKECY/usuario/login'; 
      } else {
        console.error('Erro ao favoritar/desfavoritar:', response.status);
      }
    })
    .then(data => {
      console.log(data); 
      const icone = document.getElementById('favorito-icon');
      icone.classList.toggle('fa-regular'); 
      icone.classList.toggle('fa-solid'); 
      icone.style.color = icone.style.color === 'orange' ? '' : 'orange'; 
    })
    .catch(error => {
      console.error('Erro na requisição:', error);
    });
}