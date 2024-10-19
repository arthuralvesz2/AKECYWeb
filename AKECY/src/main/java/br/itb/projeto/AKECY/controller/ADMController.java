package br.itb.projeto.AKECY.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.entity.Cupom;
import br.itb.projeto.AKECY.model.entity.Mensagem;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.MensagemRepository;
import br.itb.projeto.AKECY.service.CategoriaService;
import br.itb.projeto.AKECY.service.CupomService;
import br.itb.projeto.AKECY.service.ProdutoService;
import br.itb.projeto.AKECY.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/ADM/")
public class ADMController {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private CupomService cupomService;

	@Autowired
	private ProdutoService produtoService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private MensagemRepository mensagemRepository;

	// Base

	@GetMapping("/cadastrar")
	public String cadastrar(Model model) {
		return "adm-cadastrar";
	}

	@GetMapping("/modificar")
	public String modificar(Model model) {
		return "adm-modificar";
	}

	// Produto
	
	@GetMapping("/cadastrar-produto")
	public String CadastrarProduto(Model model) {
		model.addAttribute("produto", new Produto());
		List<Categoria> categorias = categoriaService.findAll();
		model.addAttribute("categorias", categorias);
		return "adm-cadastrar-produto";
	}

	@PostMapping("/cadastrar-produto")
	public String salvarProduto(
	        @RequestParam(value = "foto1", required = false) MultipartFile foto1,
	        @RequestParam(value = "foto2", required = false) MultipartFile foto2,
	        @RequestParam(value = "foto3", required = false) MultipartFile foto3,
	        @RequestParam(value = "foto4", required = false) MultipartFile foto4,
	        @RequestParam(value = "foto5", required = false) MultipartFile foto5,
	        @RequestParam("nome") String nome,
	        @RequestParam("descricao") String descricao,
	        @RequestParam("descricao_completa") String descricaoCompleta,
	        @RequestParam("tamanhos_disponiveis") String tamanhosDisponiveis,
	        @RequestParam("preco") String preco,
	        @RequestParam("categoria.idCategoria") Long idCategoria,
	        Model model) {

	    // Crie uma instância de Produto e preencha seus atributos
	    Produto produto = new Produto();
	    produto.setNome(nome);
	    produto.setDescricao(descricao);
	    produto.setDescricao_completa(descricaoCompleta);
	    produto.setTamanhos_disponiveis(tamanhosDisponiveis);
	    produto.setPreco(preco); 

	    // Recupere a categoria pelo ID
	    Categoria categoria = categoriaService.findById(idCategoria).orElse(null);
	    produto.setCategoria(categoria);

	    Produto p = produtoService.saveNew(foto1, foto2, foto3, foto4, foto5, produto);

	    return "redirect:/AKECY/ADM/cadastrar-produto";
	}

	// Cupom

	@GetMapping("/cadastrar-cupom")
	public String cadastrarCupom(Model model) {
		model.addAttribute("cupom", new Cupom());
		return "adm-cadastrar-cupom";
	}

	@PostMapping("/cadastrar-cupom")
	public ResponseEntity<Map<String, String>> salvarCupom(@ModelAttribute("cupom") Cupom cupom) {
	    Map<String, String> response = new HashMap<>();
	    try {
	        cupomService.create(cupom); 
	        response.put("message", "Cupom cadastrado com sucesso!");
	        return ResponseEntity.ok(response);
	    } catch (DataIntegrityViolationException e) {
	        response.put("error", "Código já cadastrado, por favor, tente outro.");
	        return ResponseEntity.badRequest().body(response);
	    } catch (Exception e) {
	        response.put("error", "Ocorreu um erro ao cadastrar o cupom.");
	        return ResponseEntity.status(500).body(response);
	    }
	}
	
	@GetMapping("/cupons/{id}")
	public ResponseEntity<Cupom> obterCupom(@PathVariable int id) {
	    Cupom cupom = cupomService.findById(id);
	    if (cupom != null) {
	        return ResponseEntity.ok(cupom);
	    }
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	}


	@GetMapping("/modificar-cupons")
	public String modificarCupons(Model model) {
	    List<Cupom> cupons = cupomService.findAll(); // Obtém todos os cupons do serviço
	    model.addAttribute("cupons", cupons); // Adiciona a lista de cupons ao modelo
	    return "adm-modificar-cupons"; // Retorna a view
	}

	// Usuário

	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		List<Usuario> usuarios = usuarioService.findAllOrderByDataCadastroDesc();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (Usuario usuario : usuarios) {
			String dataCadastroFormatada = usuario.getDataCadastro().format(formatter);
			usuario.setDataCadastroFormatada(dataCadastroFormatada);

			String dataNascFormatada = usuario.getDataNasc().format(dateFormatter);
			usuario.setDataNascFormatada(dataNascFormatada);
		}
		model.addAttribute("usuarios", usuarios);
		return "adm-usuarios";
	}

	@PostMapping("/disable/{idUsuario}")
	public String disableUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.disable(idUsuario);
		return "redirect:/AKECY/ADM/usuarios";
	}

	@PostMapping("/enable/{idUsuario}")
	public String enableUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.enable(idUsuario);
		return "redirect:/AKECY/ADM/usuarios";
	}

	@PostMapping("/mudar-nivel/{idUsuario}")
	public String mudarNivelUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.mudarNivelAcesso(idUsuario);
		return "redirect:/AKECY/ADM/usuarios";
	}

	@PostMapping("/salvar-usuario/{idUsuario}")
	public ResponseEntity<?> salvarUsuario(@PathVariable int idUsuario, @RequestBody Usuario usuario) {
		Usuario usuarioExistente = usuarioService.findById(idUsuario);
		if (usuarioExistente != null) {
			usuarioExistente.setNome(usuario.getNome());
			usuarioExistente.setEmail(usuario.getEmail());
			usuarioExistente.setTelefone(usuario.getTelefone());
			usuarioExistente.setCpf(usuario.getCpf());
			usuarioExistente.setSexo(usuario.getSexo());
			usuarioExistente.setDataNasc(usuario.getDataNasc());
			usuarioExistente.setStatusUsuario(usuario.getStatusUsuario());
			usuarioExistente.setNivelAcesso(usuario.getNivelAcesso());

			usuarioService.save(usuarioExistente);
			return ResponseEntity.ok(usuarioExistente);
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
	}

	@PostMapping("/mudar-sexo/{idUsuario}/{novoSexo}")
	public String mudarSexoUsuario(@PathVariable int idUsuario, @PathVariable String novoSexo, Model model) {
		Usuario usuario = usuarioService.findById(idUsuario);
		if (usuario != null) {
			usuario.setSexo(novoSexo);
			usuarioService.save(usuario);
		}
		return "redirect:/AKECY/ADM/usuarios";
	}

	// Mensagens

	@GetMapping("/mensagens")
	public String mensagens(Model model) {
		List<Mensagem> mensagens = mensagemRepository.findAll();
		model.addAttribute("mensagens", mensagens);
		return "adm-mensagens";
	}

	// ADM

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loggedInUser");
		return "redirect:/AKECY/index";
	}
}