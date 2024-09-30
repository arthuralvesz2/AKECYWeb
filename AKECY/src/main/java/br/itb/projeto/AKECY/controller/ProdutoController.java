package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
import br.itb.projeto.AKECY.service.ProdutoService;

@RestController
@RequestMapping("/produto/")
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

    @GetMapping("produto/{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        Produto produto = produtoService.findById(id).get();

        String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
        produto.setBase64Image(base64Image);

        return new ResponseEntity<Produto>(produto, HttpStatus.OK);
    }
}