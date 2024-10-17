package br.itb.projeto.AKECY.controller;

import java.util.Base64;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.entity.Mensagem;
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

	@Autowired
	private br.itb.projeto.AKECY.service.CategoriaService categoriaService;
	
	@Autowired
	private br.itb.projeto.AKECY.model.repository.MensagemRepository mensagemRepository;

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
	
	private static final Logger logger = LoggerFactory.getLogger(ProdutoController.class);

    @GetMapping("/cadastrar-produto")
    public String mostrarFormularioCadastroProduto(Model model) {
        logger.info("Acessando o formulário de cadastro de produto.");
        model.addAttribute("produto", new Produto());
        List<Categoria> categorias = categoriaService.findAll();
        model.addAttribute("categorias", categorias);
        return "adm-cadastrar-produto";
    }

    @PostMapping("/cadastrar-produto")
    public String cadastrarProduto(
            @RequestParam("foto1") MultipartFile foto1,
            @RequestParam("foto2") MultipartFile foto2,
            @RequestParam("foto3") MultipartFile foto3,
            @RequestParam("foto4") MultipartFile foto4,
            @RequestParam("foto5") MultipartFile foto5,
            @ModelAttribute Produto produto,
            BindingResult result,
            RedirectAttributes attributes) {

        logger.info("Tentando cadastrar o produto: {}", produto.getNome());

        if (result.hasErrors()) {
            logger.error("Erros encontrados ao cadastrar o produto: {}", result.getAllErrors());
            attributes.addFlashAttribute("erro", "Erro ao cadastrar o produto. Verifique os campos."); // Mensagem de erro mais amigável
            return "redirect:/AKECY/ADM/cadastrar-produto"; // Redireciona para o formulário com a mensagem de erro
        }

        try {
            if (!foto1.isEmpty()) {
                produto.setFoto1(foto1.getBytes());
                logger.info("Foto 1 processada.");
            }
            if (!foto2.isEmpty()) {
                produto.setFoto2(foto2.getBytes());
                logger.info("Foto 2 processada.");
            }
            if (!foto3.isEmpty()) {
                produto.setFoto3(foto3.getBytes());
                logger.info("Foto 3 processada.");
            }
            if (!foto4.isEmpty()) {
                produto.setFoto4(foto4.getBytes());
                logger.info("Foto 4 processada.");
            }
            if (!foto5.isEmpty()) {
                produto.setFoto5(foto5.getBytes());
                logger.info("Foto 5 processada.");
            }
        } catch (IOException e) {
            logger.error("Erro ao processar as imagens: {}", e.getMessage());
            attributes.addFlashAttribute("erro", "Erro ao processar as imagens.");
            return "redirect:/AKECY/ADM/cadastrar-produto";
        }

        produto.setStatusProd("ATIVO");

        produtoService.create(produto);
        logger.info("Produto cadastrado com sucesso: {}", produto.getNome());
        attributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso!");
        return "redirect:/AKECY/ADM/cadastrar-produto";
    }


	@GetMapping("/modificar-produtos")
	public String modificarProdutos(Model model) {

		return "adm-modificar-produtos";
	}
	
	// Cupom


	@GetMapping("/cadastrar-cupom")
	public String cadatrarCupom(Model model) {

		return "adm-cadastrar-cupom";
	}

	
	@GetMapping("/modificar-cupom")
	public String modificarCupom(Model model) {

		return "adm-modificar-cupom";
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