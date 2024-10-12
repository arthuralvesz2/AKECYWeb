package br.itb.projeto.AKECY.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.itb.projeto.AKECY.model.entity.Mensagem;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.service.MensagemService;
import br.itb.projeto.AKECY.service.ProdutoService;

@Controller
@RequestMapping("/AKECY/")
public class AKECYController {

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MensagemService mensagemService;

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

	@GetMapping("/fale-conosco")
	public String faleConosco(Model model) {
		model.addAttribute("mensagem", new Mensagem());
		return "fale-conosco";
	}

	@PostMapping("/fale-conosco")
	public String salvarMensagem(@ModelAttribute Mensagem mensagem, 
	                             BindingResult result, RedirectAttributes attributes) {
	    if (result.hasErrors()) {
	        if (result.hasFieldErrors("nome")) {
	            attributes.addFlashAttribute("erroNome", 
	                                         "Por favor, insira um nome válido (apenas letras).");
	        }
	        if (result.hasFieldErrors("email")) {
	            attributes.addFlashAttribute("erroEmail", 
	                                         "Por favor, insira um email válido.");
	        }
	        if (result.hasFieldErrors("telefone")) {
	            attributes.addFlashAttribute("erroTelefone", 
	                                         "Por favor, insira um telefone válido.");
	        }
	        return "fale-conosco"; 
	    }

	    mensagem.setDataMensagem(LocalDateTime.now());
	    mensagem.setStatusMensagem("PENDENTE"); 
	    mensagemService.salvarMensagem(mensagem);

	    attributes.addFlashAttribute("sucesso", "Mensagem enviada com sucesso!");
	    return "redirect:/AKECY/fale-conosco";
	}
}
