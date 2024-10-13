package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.FavoritoRepository;
import br.itb.projeto.AKECY.service.ProdutoService;
import br.itb.projeto.AKECY.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/produto/")
public class ProdutoController {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private FavoritoRepository favoritoRepository;

	@Autowired
	private UsuarioService usuarioService;

	// Constructor injection for better testability
	public ProdutoController(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	@GetMapping("findAll")
	public ResponseEntity<List<Produto>> findAll() {
		List<Produto> produtos = produtoService.findAll();
		return ResponseEntity.ok(produtos);
	}

	@PutMapping("inativar/{id}")
	public ResponseEntity<Produto> inativar(@PathVariable long id) {
		Produto _produto = produtoService.inativar(id);
		return ResponseEntity.ok(_produto);
	}

	@PutMapping("reativar/{id}")
	public ResponseEntity<Produto> reativar(@PathVariable long id) {
		Produto _produto = produtoService.reativar(id);
		return ResponseEntity.ok(_produto);
	}

	@GetMapping("fonte/{id}")
	public ResponseEntity<Produto> findByIdForApi(@PathVariable Long id) {
		Optional<Produto> optionalProduto = produtoService.findById(id);
		if (optionalProduto.isPresent()) {
			Produto produto = optionalProduto.get();
			String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
			produto.setBase64Image(base64Image);
			return ResponseEntity.ok(produto);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}")
	public String findById(@PathVariable Long id, Model model, HttpSession session) {
	    Optional<Produto> optionalProduto = produtoService.findById(id);
	    String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

	    if (optionalProduto.isPresent()) {
	        Produto produto = optionalProduto.get();
	        encodeProductImages(produto);

	        boolean isFavorite = false;
	        if (loggedInUserEmail != null) {
	            Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
	            if (usuario != null) {
	                isFavorite = favoritoRepository.existsByUsuarioIdUsuarioAndProdutoIdProduto(usuario.getIdUsuario(), id);
	            }
	        }

	        model.addAttribute("produto", produto);
	        model.addAttribute("isFavorite", isFavorite);
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
			String base64Image = getBase64ImageByIndex(produto, foto);
			if (base64Image != null) {
				return ResponseEntity.ok(base64Image);
			}
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("buscar")
	public String buscarProdutos(@RequestParam("q") String query, Model model) {
		List<Produto> produtos = produtoService.buscarProdutosPorPalavraChave(query);
		encodeProductImagesList(produtos); // Helper method to encode images for each product
		model.addAttribute("produtos", produtos);
		model.addAttribute("termoPesquisa", query);
		return "buscar";
	}

	// Helper method to encode images for a single product
	private void encodeProductImages(Produto produto) {
		if (produto.getFoto1() != null) {
			produto.setBase64Image(Base64.getEncoder().encodeToString(produto.getFoto1()));
		}
		if (produto.getFoto2() != null) {
			produto.setBase64Image2(Base64.getEncoder().encodeToString(produto.getFoto2()));
		}
		if (produto.getFoto3() != null) {
			produto.setBase64Image3(Base64.getEncoder().encodeToString(produto.getFoto3()));
		}
		if (produto.getFoto4() != null) {
			produto.setBase64Image4(Base64.getEncoder().encodeToString(produto.getFoto4()));
		}
		if (produto.getFoto5() != null) {
			produto.setBase64Image5(Base64.getEncoder().encodeToString(produto.getFoto5()));
		}
	}

	// Helper method to encode images for a list of products
	private void encodeProductImagesList(List<Produto> produtos) {
		for (Produto produto : produtos) {
			encodeProductImages(produto);
		}
	}

	// Helper method to retrieve the base64 image string based on index
	private String getBase64ImageByIndex(Produto produto, int index) {
		switch (index) {
		case 1:
			return produto.getBase64Image();
		case 2:
			return produto.getBase64Image2();
		case 3:
			return produto.getBase64Image3();
		case 4:
			return produto.getBase64Image4();
		case 5:
			return produto.getBase64Image5();
		default:
			return null;
		}
	}
}
