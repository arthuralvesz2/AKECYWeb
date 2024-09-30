package br.itb.projeto.AKECY.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.service.ProdutoService;

@Controller
@RequestMapping("/AKECY/")
public class AKECYController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/index")
    public String index(Model model) {
        List<Produto> produtosEmDestaque = produtoService.getProdutosEmDestaque();

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        for (Produto produto : produtosEmDestaque) {
            String url = baseUrl + "/AKECY/produto/fonte/" + produto.getIdProduto();
            ResponseEntity<Produto> response = restTemplate.getForEntity(url, Produto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Produto produtoCompleto = response.getBody();
                produto.setBase64Image(produtoCompleto.getBase64Image());
            }
        }

        model.addAttribute("produtosEmDestaque", produtosEmDestaque);

        List<Produto> produtosRecentes = produtoService.getProdutosRecentes();

        for (Produto produto : produtosRecentes) {
            String url = baseUrl + "/AKECY/produto/fonte/" + produto.getIdProduto();
            ResponseEntity<Produto> response = restTemplate.getForEntity(url, Produto.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Produto produtoCompleto = response.getBody();
                produto.setBase64Image(produtoCompleto.getBase64Image());
            }
        }

        model.addAttribute("produtosRecentes", produtosRecentes);

        return "index";
    }
    
}