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