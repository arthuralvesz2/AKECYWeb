package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	        RedirectAttributes redirectAttributes) { // Alteração aqui

	    Produto produto = new Produto();
	    produto.setNome(nome);
	    produto.setDescricao(descricao);
	    produto.setDescricao_completa(descricaoCompleta);
	    produto.setTamanhos_disponiveis(tamanhosDisponiveis);
	    produto.setPreco(preco); 

	    Categoria categoria = categoriaService.findById(idCategoria).orElse(null);
	    produto.setCategoria(categoria);

	    produtoService.saveNew(foto1, foto2, foto3, foto4, foto5, produto);

	    redirectAttributes.addFlashAttribute("successMessage", "Produto cadastrado com sucesso!");
	    return "redirect:/AKECY/ADM/modificar-produtos";
	}

	
	@GetMapping("/modificar-produtos")
	public String modificarProdutos(Model model) {
	    List<Produto> produtos = produtoService.findAllProdutos();
	    for (Produto produto : produtos) {
	        encodeProductImages(produto);
	    }
	    model.addAttribute("produtos", produtos); 
	    return "adm-modificar-produtos";
	}
	
	@GetMapping("/produto/{id}")
	public String editarProduto(@PathVariable("id") Long id, Model model) {
	    Optional<Produto> optionalProduto = produtoService.findById(id);
	    if (optionalProduto.isPresent()) {
	        Produto produto = optionalProduto.get();
	        model.addAttribute("produto", produto);
	        List<Categoria> categorias = categoriaService.findAll();
	        model.addAttribute("categorias", categorias);
	        return "adm-modificar-produto"; // View para editar o produto
	    } else {
	        // Lidar com o caso em que o produto não é encontrado
	        return "redirect:/AKECY/ADM/modificar-produtos"; 
	    }
	}
	
	@PostMapping("/modificar-produtos")
	public String atualizarProduto(
	        @RequestParam(value = "foto1", required = false) MultipartFile foto1,
	        @RequestParam(value = "foto2", required = false) MultipartFile foto2,
	        @RequestParam(value = "foto3", required = false) MultipartFile foto3,
	        @RequestParam(value = "foto4", required = false) MultipartFile foto4,
	        @RequestParam(value = "foto5", required = false) MultipartFile foto5,
	        @RequestParam("idProduto") Long idProduto, 
	        @RequestParam("nome") String nome,
	        @RequestParam("descricao") String descricao,
	        @RequestParam("descricao_completa") String descricaoCompleta,
	        @RequestParam("tamanhos_disponiveis") String tamanhosDisponiveis,
	        @RequestParam("preco") String preco,
	        @RequestParam("categoria.idCategoria") Long idCategoria,
	        RedirectAttributes redirectAttributes) {

	    Optional<Produto> optionalProduto = produtoService.findById(idProduto);
	    if (optionalProduto.isPresent()) {
	        Produto produto = optionalProduto.get();
	        produto.setNome(nome);
	        produto.setDescricao(descricao);
	        produto.setDescricao_completa(descricaoCompleta);
	        produto.setTamanhos_disponiveis(tamanhosDisponiveis);
	        produto.setPreco(preco);

	        Categoria categoria = categoriaService.findById(idCategoria).orElse(null);
	        produto.setCategoria(categoria);

	        produtoService.update(foto1, foto2, foto3, foto4, foto5, produto); // Método para atualizar o produto

	        redirectAttributes.addFlashAttribute("successMessageEdit", "Produto modificado com sucesso!");
	        return "redirect:/AKECY/ADM/modificar-produtos";
	    } else {
	        // Lidar com o caso em que o produto não é encontrado
	        return "redirect:/AKECY/ADM/modificar-produtos";
	    }
	}
	
	@PostMapping("/mudar-status-produto/{idProduto}")
	public ResponseEntity<?> mudarStatusProduto(@PathVariable Long idProduto, @RequestBody Produto produto) {
	    try {
	        Produto produtoExistente = produtoService.findById(idProduto).orElseThrow(); 
	        produtoExistente.setStatusProd(produto.getStatusProd());
	        produtoService.save(produtoExistente); 
	        return ResponseEntity.ok(produtoExistente);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
	    }
	}

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

	// Cupom

	@GetMapping("/cadastrar-cupom")
	public String cadastrarCupom(Model model) {
		model.addAttribute("cupom", new Cupom());
		return "adm-cadastrar-cupom";
	}

	@PostMapping("/cadastrar-cupom")
	public String salvarCupom(@ModelAttribute("cupom") Cupom cupom, Model model, RedirectAttributes redirectAttributes) {
	    try {
	        cupomService.create(cupom); 
	        return "redirect:/AKECY/ADM/modificar-cupons";  
	    } catch (DataIntegrityViolationException e) {
	        model.addAttribute("serverError", "Código já cadastrado, por favor, tente outro.");
	        return "adm-cadastrar-cupom";
	    } catch (Exception e) {
	        model.addAttribute("serverError", "Ocorreu um erro ao cadastrar o cupom.");
	        return "adm-cadastrar-cupom"; 
	    }
	}

	@PostMapping("/salvar-cupom/{idCupom}")
	public ResponseEntity<?> salvarCupom(@PathVariable int idCupom, @RequestBody Cupom cupom) {
	    Cupom cupomExistente = cupomService.findById(idCupom);
	    if (cupomExistente != null) {
	        if (!cupomExistente.getCodigo().equals(cupom.getCodigo())) {
	            Cupom cupomComMesmoCodigo = cupomService.findByCodigo(cupom.getCodigo());
	            if (cupomComMesmoCodigo != null && cupomComMesmoCodigo.getIdCupom() != idCupom) {
	                Map<String, String> response = new HashMap<>();
	                response.put("error", "Código já cadastrado, por favor, tente outro.");
	                return ResponseEntity.badRequest().body(response); 
	            }
	        }

	        cupomExistente.setDesconto(cupom.getDesconto());
	        cupomExistente.setCashback(cupom.getCashback());
	        cupomExistente.setDescricao(cupom.getDescricao());
	        cupomExistente.setCodigo(cupom.getCodigo());
	        cupomExistente.setStatusCupom(cupom.getStatusCupom());

	        cupomService.save(cupomExistente); 
	        return ResponseEntity.ok(cupomExistente);
	    }
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cupom não encontrado");
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
	
	@PostMapping("/verificar-duplicatas")
	public ResponseEntity<?> verificarDuplicatas(@RequestBody Map<String, String> request, Model model) {
	    String email = request.get("email");
	    String telefone = request.get("telefone");
	    String cpf = request.get("cpf");
	    int idUsuario = Integer.parseInt(request.get("idUsuario")); 

	    Map<String, Boolean> duplicatas = new HashMap<>();
	    duplicatas.put("email", usuarioService.existsByEmailAndDifferentId(email, idUsuario));
	    duplicatas.put("telefone", usuarioService.existsByTelefoneAndDifferentId(telefone, idUsuario));
	    duplicatas.put("cpf", usuarioService.existsByCpfAndDifferentId(cpf, idUsuario));

	    return ResponseEntity.ok(Map.of("duplicatas", duplicatas));
	}

	// Mensagens

	@GetMapping("/mensagens")
	public String mensagens(Model model) {
		List<Mensagem> mensagens = mensagemRepository.findAll();
		model.addAttribute("mensagens", mensagens);
		return "adm-mensagens";
	}
	
	@PutMapping("/mensagens/{id}")
	public ResponseEntity<?> atualizarStatusMensagem(@PathVariable Long id, @RequestBody Mensagem mensagem) {
	    // Busca a mensagem pelo ID
	    Optional<Mensagem> mensagemOptional = mensagemRepository.findById(id); 

	    // Verifica se a mensagem existe
	    if (mensagemOptional.isPresent()) { 
	        Mensagem mensagemExistente = mensagemOptional.get();
	        mensagemExistente.setStatusMensagem(mensagem.getStatusMensagem());
	        mensagemRepository.save(mensagemExistente);
	        return ResponseEntity.ok(mensagemExistente);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mensagem não encontrada");
	    }
	}

	// ADM

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loggedInUser");
		return "redirect:/AKECY/index";
	}
}