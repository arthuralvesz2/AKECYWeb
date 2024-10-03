package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.List;

import org.apache.commons.text.WordUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.service.CategoriaService;

@Controller
@RequestMapping("/AKECY/categoria/")
public class CategoriaController {

	private CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		super();
		this.categoriaService = categoriaService;
	}

	@GetMapping("/{nomeCategoria}")
	public String getCategoria(@PathVariable String nomeCategoria, Model model) {
		List<Produto> produtos = categoriaService.findProdutosByNomeCategoria(nomeCategoria);

		for (Produto produto : produtos) {
			if (produto.getFoto1() != null) {
				String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
				produto.setBase64Image(base64Image);
			}
		}

        String nomeCategoriaCapitalizado = WordUtils.capitalize(nomeCategoria);
        model.addAttribute("nomeCategoria", nomeCategoriaCapitalizado); 
        model.addAttribute("produtos", produtos);
        return "categoria";
	}
}