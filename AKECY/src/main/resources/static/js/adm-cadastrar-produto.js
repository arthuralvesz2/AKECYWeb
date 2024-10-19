$(document).ready(function() {
  $('file').change(function(){
    var label = $(this).parent().find('span'); 
    if(typeof(this.files) != 'undefined'){ 
      if(this.files.length == 0){
        label.removeClass('withFile').text(label.data('default'));
      } else {
        var file = this.files[0]; 
        var name = file.name;
        var size = (file.size / 1048576).toFixed(3);   

        label.addClass('withFile').text(name);
      }
    } else {
      var name = this.value.split("\\");
      label.addClass('withFile').text(name[name.length-1]);
    }
    return false;
  });  
});

const input = document.getElementById('tamanhos');
input.addEventListener('input', function() {
    this.value = this.value.replace(/ {2}/g, " - ");
});


$(window).on("load", () => {
  $(() => {
    const inputsFiles = ['#input-foto1', '#input-foto2', '#input-foto3', '#input-foto4', '#input-foto5'];
    const imgs = ['#foto1', '#foto2', '#foto3', '#foto4', '#foto5'];

    inputsFiles.forEach((inputSelector, index) => {
      const image = $(imgs[index]);
      const inputFile = $(inputSelector);

      inputFile.change((e) => addImage(e, image));

      const addImage = (e, image) => {
        const file = e.target.files[0];
        if (!file.type.match(/image.*/)) {
          cleanImage(image);
          return;
        }

        const reader = new FileReader();
        reader.onload = (event) => fileOnload(event, image);
        reader.readAsDataURL(file);
      };

      const cleanImage = (image) => {
        image.hide();
        image.attr("src", "");
      };

      const fileOnload = (e, image) => {
        const result = e.target.result;
        image.show();
        image.attr("src", result);
      };
    });
  });
});

function atualizarIdCategoria() {
  var input = document.getElementById("categoriaInput");
  var datalist = document.getElementById("categoriaSelecionar");
  var idCategoriaInput = document.getElementById("idCategoria");

  for (var i = 0; i < datalist.options.length; i++) {
    if (datalist.options[i].value === input.value) {
      idCategoriaInput.value = datalist.options[i].getAttribute('data-id');
      break;
    }
  }
}

$(document).ready(function() {
    $('#input-foto1').change(function(e) {
        handleFileChange(e, '#foto1', '#foto2-container');
    });

    $('#input-foto2').change(function(e) {
        handleFileChange(e, '#foto2', '#foto3-container');
    });

    $('#input-foto3').change(function(e) {
        handleFileChange(e, '#foto3', '#foto4-container');
    });
	
	$('#input-foto4').change(function(e) {
	    handleFileChange(e, '#foto4', '#foto5-container');
	});

    // Função para lidar com a mudança de arquivo
    function handleFileChange(event, imgSelector, nextContainerSelector) {
        const file = event.target.files[0];
        const image = $(imgSelector);
        const container = $(nextContainerSelector);

        if (file && file.type.match(/image.*/)) {
            const reader = new FileReader();
            reader.onload = function(e) {
                image.attr('src', e.target.result).show();
                container.show(); // Mostra o próximo container
            };
            reader.readAsDataURL(file);
        } else {
            image.attr('src', '').hide();
            container.hide(); // Esconde o próximo container se não houver imagem
        }
    }
});


function formatarMoeda(input) {
    let valor = input.value.replace(/\D/g, ""); // Remove tudo que não é dígito
    valor = valor.replace(/^0+/, ""); // Remove zeros à esquerda
    valor = valor.slice(0, 8); // Limita o valor a 8 dígitos

    if (valor === "") {
        input.value = "";
        document.getElementById('preco').value = ""; // Limpa o campo oculto
        return;
    }

    // Formata o valor como moeda para visualização
    let valorFormatado = (valor / 100).toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
    input.value = valorFormatado.replace("R$", "R$ "); // Exibe o valor formatado

    // Salva o valor original no campo oculto para o envio
    document.getElementById('preco').value = (valor / 100).toFixed(2).replace('.', ',');
}