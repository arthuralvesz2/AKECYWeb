document.addEventListener('DOMContentLoaded', function() { 
  const mainImage = document.getElementById('main-product-image'); 
  
  const firstImage = document.getElementById('foto-1');
  if (firstImage) {
    mainImage.src = firstImage.src;
  }

  for (let i = 1; i <= 5; i++) {
    const img = document.getElementById('foto-' + i);
    if (img) {
      img.addEventListener('click', function() {
        mainImage.src = this.src;
      });
    }
  }

  const favoritoIcon = document.getElementById('favorito-icon');
  if (favoritoIcon) {
    const isFavorite = favoritoIcon.classList.contains('fa-solid');
    if (isFavorite) {
      favoritoIcon.style.color = 'orange';
    }
  }
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