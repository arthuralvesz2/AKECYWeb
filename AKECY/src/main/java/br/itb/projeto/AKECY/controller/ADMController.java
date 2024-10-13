package br.itb.projeto.AKECY.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.entity.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/ADM/")
public class ADMController {

	@Autowired
	private br.itb.projeto.AKECY.service.UsuarioService usuarioService;
	
	@Autowired
	private br.itb.projeto.AKECY.service.ProdutoService produtoService;

	@GetMapping("/cadastrar-produto")
	public String mostrarFormularioCadastroProduto(Model model) {
	    // Adiciona um novo objeto Produto ao modelo, se necessário
	    model.addAttribute("produto", new Produto());
	    
	    return "adm-cadastrar-produto"; // Retorna a view que contém o formulário de cadastro
	}

	
	@PostMapping("/ADM/cadastrar-produto")
	public String cadastrarProduto(@ModelAttribute Produto produto, 
	                               @RequestParam("foto1") MultipartFile foto1,
	                               @RequestParam("foto2") MultipartFile foto2,
	                               @RequestParam("foto3") MultipartFile foto3,
	                               @RequestParam("foto4") MultipartFile foto4,
	                               @RequestParam("foto5") MultipartFile foto5,
	                               BindingResult result, 
	                               RedirectAttributes attributes) {
	    if (result.hasErrors()) {
	        return "adm-cadastrar-produto"; // Retorna à página de cadastro em caso de erro
	    }

	    // Converte as imagens de MultipartFile para byte[]
	    try {
	        if (!foto1.isEmpty()) {
	            produto.setFoto1(foto1.getBytes());
	        }
	        if (!foto2.isEmpty()) {
	            produto.setFoto2(foto2.getBytes());
	        }
	        if (!foto3.isEmpty()) {
	            produto.setFoto3(foto3.getBytes());
	        }
	        if (!foto4.isEmpty()) {
	            produto.setFoto4(foto4.getBytes());
	        }
	        if (!foto5.isEmpty()) {
	            produto.setFoto5(foto5.getBytes());
	        }
	    } catch (IOException e) {
	        attributes.addFlashAttribute("erro", "Erro ao processar as imagens.");
	        return "adm-cadastrar-produto"; // Retorna à página de cadastro em caso de erro
	    }

	    // Use o método create (ou save) para persistir o produto
	    produtoService.create(produto); // ou produtoService.save(produto);
	    
	    attributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
	    return "redirect:/AKECY/ADM/cadastrar-produto"; // Redirecionar após sucesso
	}



	@GetMapping("/editar-produtos")
	public String editarProdutos(Model model) {

		return "adm-editar-produtos";
	}

	@GetMapping("/modificar-usuarios")
	public String ModificarUsuarios(Model model) {
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
		return "adm-modificar-usuarios";
	}

	@PostMapping("/disable/{idUsuario}")
	public String disableUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.disable(idUsuario);
		return "redirect:/AKECY/ADM/modificar-usuarios";
	}

	@PostMapping("/enable/{idUsuario}")
	public String enableUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.enable(idUsuario);
		return "redirect:/AKECY/ADM/modificar-usuarios";
	}
	

	@PostMapping("/mudar-nivel/{idUsuario}")
	public String mudarNivelUsuario(@PathVariable int idUsuario, Model model) {
		usuarioService.mudarNivelAcesso(idUsuario);
		return "redirect:/AKECY/ADM/modificar-usuarios";
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
	    return "redirect:/AKECY/ADM/modificar-usuarios";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loggedInUser"); // Remove o atributo da sessão
		return "redirect:/AKECY/index"; // Redireciona para a página inicial
	}

}