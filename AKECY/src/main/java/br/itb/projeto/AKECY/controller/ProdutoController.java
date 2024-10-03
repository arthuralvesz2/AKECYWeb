package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
import br.itb.projeto.AKECY.service.ProdutoService;

@Controller
@RequestMapping("/AKECY/produto/")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        super();
        this.produtoService = produtoService;
    }

    @GetMapping("findAll")
    public ResponseEntity<List<Produto>> findAll() {
        List<Produto> produtos = produtoService.findAll();
        return new ResponseEntity<List<Produto>>(produtos, HttpStatus.OK);
    }

    @GetMapping("create")
    public ResponseEntity<?> create(@RequestBody Produto produto) {
        Produto _produto = produtoService.create(produto);

        if (_produto == null) {
            return ResponseEntity.badRequest().body(new MessageResponse("Produto Já Cadastrado!"));
        }
        return ResponseEntity.ok().body(new MessageResponse("Produto cadastrado com sucesso!"));
    }

    @PutMapping("inativar/{id}")
    public ResponseEntity<Produto> inativar(@PathVariable long id) {
        Produto _produto = produtoService.inativar(id);
        return new ResponseEntity<Produto>(_produto, HttpStatus.OK);
    }

    @PutMapping("reativar/{id}")
    public ResponseEntity<Produto> reativar(@PathVariable long id) {
        Produto _produto = produtoService.reativar(id);
        return new ResponseEntity<Produto>(_produto, HttpStatus.OK);
    }

    @GetMapping("fonte/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        Produto produto = produtoService.findById(id).get();

        String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
        produto.setBase64Image(base64Image);

        return new ResponseEntity<Produto>(produto, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model) {
        Optional<Produto> optionalProduto = produtoService.findById(id);

        if (optionalProduto.isPresent()) {
            Produto produto = optionalProduto.get();

            if (produto.getFoto1() != null) {
                String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
                produto.setBase64Image(base64Image);
            }

            if (produto.getFoto2() != null) {
                String base64Image2 = Base64.getEncoder().encodeToString(produto.getFoto2());
                produto.setBase64Image2(base64Image2);
            }

            if (produto.getFoto3() != null) {
                String base64Image3 = Base64.getEncoder().encodeToString(produto.getFoto3());
                produto.setBase64Image3(base64Image3);
            }

            if (produto.getFoto4() != null) {
                String base64Image4 = Base64.getEncoder().encodeToString(produto.getFoto4());
                produto.setBase64Image4(base64Image4);
            }

            if (produto.getFoto5() != null) {
                String base64Image5 = Base64.getEncoder().encodeToString(produto.getFoto5());
                produto.setBase64Image5(base64Image5);
            }

            model.addAttribute("produto", produto);
            return "produto"; 
        } else {
            return "error"; 
        }
    }
    
    @GetMapping("imagem/{id}/{foto}")
    public ResponseEntity<String> getImagemProduto(@PathVariable Long id, @PathVariable int foto) {
        Optional<Produto> optionalProduto = produtoService.findById(id);
        if (optionalProduto.isPresent()) {
            Produto produto = optionalProduto.get();
            String base64Image = null;
            switch (foto) {
                case 1:
                    base64Image = produto.getBase64Image();
                    break;
                case 2:
                    base64Image = produto.getBase64Image2();
                    break;
                case 3:
                    base64Image = produto.getBase64Image3();
                    break;
                case 4:
                    base64Image = produto.getBase64Image4();
                    break;
                case 5:
                    base64Image = produto.getBase64Image5();
                    break;
            }
            if (base64Image != null) {
                return new ResponseEntity<>(base64Image, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
    @GetMapping("buscar")
    public String buscarProdutos(@RequestParam("q") String query, Model model) {
        List<Produto> produtos = produtoService.findByNomeContainingIgnoreCase(query);

        for (Produto produto : produtos) {
            if (produto.getFoto1() != null) {
                String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
                produto.setBase64Image(base64Image);
            }
        }

        model.addAttribute("produtos", produtos);
        return "buscar";
    }
    
}