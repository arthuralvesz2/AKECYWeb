package br.itb.projeto.AKECY.controller;

import java.util.Base64;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.itb.projeto.AKECY.service.FavoritoService;
import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.entity.Usuario.NivelAcesso;
import br.itb.projeto.AKECY.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/usuario/")
public class UsuarioController {

	private final UsuarioService usuarioService;

	@Autowired
	private FavoritoService favoritoService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/cadastro")
	public String showFormCadastroUsuario(Usuario usuario, Model model, HttpSession session) {
		model.addAttribute("usuario", usuario);
		model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
		session.removeAttribute("serverMessage");
		return "cadastro";
	}

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute("usuario") Usuario usuario, HttpSession session) {
		Usuario existingUser = usuarioService.findByEmail(usuario.getEmail());
		if (existingUser == null) {
			existingUser = usuarioService.findByCpf(usuario.getCpf());
		}
		if (existingUser == null) {
			existingUser = usuarioService.findByTelefone(usuario.getTelefone());
		}

		if (existingUser == null) {
			if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()) {
				session.setAttribute("serverMessage", "Dados Incompletos!");
			} else {
				usuario.setNivelAcesso(Usuario.NivelAcesso.USER);
				usuarioService.create(usuario);
				session.setAttribute("serverMessage", "Usuário cadastrado com sucesso!");
			}
		} else {
			// Construir uma mensagem de erro mais específica
			String errorMessage = "Usuário já cadastrado no sistema. ";
			if (existingUser.getEmail().equals(usuario.getEmail())) {
				errorMessage += "O email informado já está em uso.";
			} else if (existingUser.getCpf().equals(usuario.getCpf())) {
				errorMessage += "O CPF informado já está em uso.";
			} else {
				errorMessage += "O telefone informado já está em uso.";
			}
			session.setAttribute("serverError", errorMessage);
		}

		return "redirect:/AKECY/usuario/login";
	}

	@GetMapping("/login")
	public String showFormLogin(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", session.getAttribute("serverMessage"));

		Integer refreshCounter = (Integer) session.getAttribute("refreshCounter");
		if (refreshCounter == null) {
			refreshCounter = 1;
		} else {
			refreshCounter++;
		}
		session.setAttribute("refreshCounter", refreshCounter);

		if (refreshCounter >= 2) {
			session.removeAttribute("serverError");
			session.removeAttribute("refreshCounter");
		}

		session.removeAttribute("serverMessage");
		return "login";
	}

	@PostMapping("/verificar-login")
	public String verificarLogin(@ModelAttribute("usuario") Usuario usuario, Model model, HttpSession session) {
		Usuario _usuario = usuarioService.acessar(usuario.getEmail(), usuario.getSenha());

		if (_usuario != null) {
			if ("INATIVO".equals(_usuario.getStatusUsuario())) {
				session.setAttribute("serverError", "Seu usuário está inativo.");
				return "redirect:/AKECY/usuario/login";
			}

			String primeiroNome = _usuario.getNome().split(" ")[0];
			session.setAttribute("loggedInUser", primeiroNome); // Armazena o nome na sessão
			session.setAttribute("loggedInUserEmail", _usuario.getEmail()); // Armazena o email na sessão
			System.out.println("Nome do usuário armazenado na sessão: " + session.getAttribute("loggedInUserName"));
			System.out.println("Email do usuário armazenado na sessão: " + session.getAttribute("loggedInUserEmail"));

			if (_usuario.getNivelAcesso() == NivelAcesso.ADMIN) {
				return "redirect:/AKECY/ADM/cadastrar";
			} else {
				return "redirect:/AKECY/index";
			}
		} else {
			session.setAttribute("serverError", "Email ou senha incorretos.");
			return "redirect:/AKECY/usuario/login";
		}
	}

	@GetMapping("/recuperar-senha")
	public String showFormRecuperarSenha(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
		model.addAttribute("serverError", session.getAttribute("serverError"));
		model.addAttribute("serverErrorCode", session.getAttribute("serverErrorCode"));
		model.addAttribute("codigoEnviado", session.getAttribute("codigoEnviado"));
		session.removeAttribute("serverMessage");
		session.removeAttribute("serverError");
		session.removeAttribute("serverErrorCode");
		session.removeAttribute("codigoEnviado");
		return "recuperar-senha";
	}

	@PostMapping("/enviar-codigo")
	public String enviarCodigo(@RequestParam(value = "email", required = false) String email, HttpSession session) {
		if (email == null || email.isEmpty()) {
			session.setAttribute("serverMessage", "Informe o e-mail.");
			session.setAttribute("codigoEnviado", false);
			return "redirect:/AKECY/usuario/recuperar-senha";
		}

		Usuario usuario = usuarioService.solicitarTrocaSenha(email);
		if (usuario == null) {
			session.setAttribute("serverError", "E-mail não encontrado.");
			session.setAttribute("codigoEnviado", false);
		} else if ("INATIVO".equals(usuario.getStatusUsuario())) {
			session.setAttribute("serverError", "E-mail está inativado.");
			session.setAttribute("codigoEnviado", false);
		} else {
			session.setAttribute("emailRecuperado", email);
			session.setAttribute("serverMessage", "Código enviado para o e-mail informado.");
			session.setAttribute("codigoEnviado", true);
		}

		return "redirect:/AKECY/usuario/recuperar-senha";
	}

	@PostMapping("/verificar-codigo")
	public String verificarCodigo(@RequestParam("codigo") String codigo, HttpSession session) {
		if ("000000".equals(codigo)) {
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		} else {
			session.setAttribute("serverErrorCode", "Código inválido, tente novamente.");
			return "redirect:/AKECY/usuario/recuperar-senha";
		}
	}

	@GetMapping("/recuperar-senha-confirmar")
	public String showRecuperarSenhaConfirmar(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverError",
				session.getAttribute("serverError") != null ? session.getAttribute("serverError") : "");
		session.removeAttribute("serverError");
		return "recuperar-senha-confirmar";
	}

	@PostMapping("/recuperar-senha-confirmar")
	public String recuperarSenhaConfirmar(@RequestParam("senha") String novaSenha,
			@RequestParam("novaSenhaConfirmacao") String novaSenhaConfirmacao, HttpSession session) {
		System.out.println("Nova Senha: " + novaSenha);
		System.out.println("Confirmação de Senha: " + novaSenhaConfirmacao);
		String emailRecuperado = (String) session.getAttribute("emailRecuperado");

		if (emailRecuperado == null || emailRecuperado.isEmpty()) {
			System.out.println("Email não encontrado.");
			session.setAttribute("serverError", "Erro ao alterar a senha. Tente novamente.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}

		if (novaSenha == null || novaSenha.isEmpty() || novaSenhaConfirmacao == null
				|| novaSenhaConfirmacao.isEmpty()) {
			System.out.println("Senha ou confirmação de senha estão vazias.");
			session.setAttribute("serverError", "Por favor, insira e confirme sua nova senha.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}

		if (!novaSenha.equals(novaSenhaConfirmacao)) {
			System.out.println("As senhas não coincidem.");
			session.setAttribute("serverError", "As senhas não coincidem. Por favor, verifique.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}

		Usuario usuario = usuarioService.findByEmail(emailRecuperado);
		System.out.println("Usuário encontrado: " + usuario);

		if (usuario != null && "TROCAR_SENHA".equals(usuario.getStatusUsuario())) {
			usuario.setSenha(novaSenha);
			usuario.setStatusUsuario("ATIVO");

			Usuario usuarioAtualizado = usuarioService.update(usuario);
			System.out.println("Usuário atualizado: " + usuarioAtualizado);

			if (usuarioAtualizado != null) {
				session.setAttribute("serverMessage", "Senha alterada com sucesso!");
				return "redirect:/AKECY/usuario/login";
			} else {
				System.out.println("Erro ao atualizar o usuário.");
				session.setAttribute("serverError", "Erro ao atualizar a senha.");
				return "redirect:/AKECY/usuario/login";
			}
		} else {
			System.out.println("Usuário não encontrado ou não autorizado.");
			session.setAttribute("serverError", "Usuário não encontrado ou não está autorizado a trocar a senha.");
			return "redirect:/AKECY/usuario/login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loggedInUser"); // Remove o atributo da sessão
		session.removeAttribute("loggedInUserEmail");
		return "redirect:/AKECY/index"; // Redireciona para a página inicial
	}

	@PostMapping("/minha-conta")
	public String updateDadosPessoais(@ModelAttribute("usuario") Usuario usuarioAtualizado, HttpSession session, RedirectAttributes redirectAttributes) {
	    String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

	    if (loggedInUserEmail == null) {
	        return "redirect:/AKECY/usuario/login";
	    }

	    // Buscar o usuário logado pelo email
	    Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
	    if (usuario == null) {
	        session.setAttribute("serverError", "Usuário não encontrado");
	        return "redirect:/AKECY/usuario/minha-conta";
	    }

	    // Verificar duplicidade antes de atualizar
	    Usuario existingUserByEmail = usuarioService.findByEmail(usuarioAtualizado.getEmail());
	    Usuario existingUserByCpf = usuarioService.findByCpf(usuarioAtualizado.getCpf());
	    Usuario existingUserByTelefone = usuarioService.findByTelefone(usuarioAtualizado.getTelefone());

	    if (existingUserByEmail != null && !existingUserByEmail.getEmail().equals(loggedInUserEmail)) {
	        redirectAttributes.addFlashAttribute("serverError", "Email já cadastrado. Tente outro.");
	        return "redirect:/AKECY/usuario/minha-conta";
	    }

	    if (existingUserByCpf != null && !existingUserByCpf.getCpf().equals(usuario.getCpf())) {
	        redirectAttributes.addFlashAttribute("serverError", "CPF já cadastrado. Tente outro.");
	        return "redirect:/AKECY/usuario/minha-conta";
	    }

	    if (existingUserByTelefone != null && !existingUserByTelefone.getTelefone().equals(usuario.getTelefone())) {
	        redirectAttributes.addFlashAttribute("serverError", "Telefone já cadastrado. Tente outro.");
	        return "redirect:/AKECY/usuario/minha-conta";
	    }

	    // Atualizar os dados do usuário existente
	    usuario.setNome(usuarioAtualizado.getNome());
	    usuario.setDataNasc(usuarioAtualizado.getDataNasc());
	    usuario.setCpf(usuarioAtualizado.getCpf());
	    usuario.setSexo(usuarioAtualizado.getSexo());
	    usuario.setTelefone(usuarioAtualizado.getTelefone());

	    usuarioService.updateMinhaConta(usuario);

	    // Adiciona mensagem de sucesso
	    redirectAttributes.addFlashAttribute("serverMessage", "Dados alterados com sucesso.");
	    
	    return "redirect:/AKECY/usuario/minha-conta";
	}

	@GetMapping("/minha-conta")
	public String showDadosPessoais(Model model, HttpSession session) {
	    String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

	    model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
	    session.removeAttribute("serverMessage"); // Remove a mensagem da sessão após exibir

	    if (loggedInUserEmail == null) {
	        return "redirect:/AKECY/usuario/login";
	    }

	    Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);

	    if (usuario == null) {
	        model.addAttribute("errorMessage", "Usuário não encontrado");
	        usuario = new Usuario();
	    }

	    model.addAttribute("usuario", usuario);
	    return "minha-conta";
	}


	@GetMapping("/trocar-senha")
	public String exibirFormularioTrocarSenha(Model model, HttpSession session) {
		String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

		if (loggedInUserEmail == null) {
			return "redirect:/AKECY/usuario/login";
		}

		Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);

		if (usuario == null) {
			// Lidar com o caso em que o usuário não é encontrado
			model.addAttribute("errorMessage", "Usuário não encontrado");
			usuario = new Usuario(); // Cria um novo objeto Usuario para evitar erros no Thymeleaf

		}

		model.addAttribute("usuario", usuario);
		return "trocar-senha"; // Nome da sua view de troca de senha (ajuste se necessário)
	}

	@PostMapping("/trocar-senha")
	public String trocarSenha(@RequestParam String senhaAtual, @RequestParam String novaSenha,
			@RequestParam String novaSenhaConfirmacao, HttpSession session, Model model) {

		String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

		if (loggedInUserEmail == null) {
			return "redirect:/AKECY/usuario/login";
		}

		if (novaSenha == null || novaSenha.isEmpty() || novaSenhaConfirmacao == null || novaSenhaConfirmacao.isEmpty()
				|| senhaAtual == null || senhaAtual.isEmpty()) {

			model.addAttribute("serverMessage", "Por favor, preencha todos os campos.");
			return "trocar-senha";
		}

		if (!novaSenha.equals(novaSenhaConfirmacao)) {
			model.addAttribute("serverMessage", "As senhas não coincidem. Por favor, verifique.");
			return "trocar-senha";
		}

		Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);

		if (usuario != null) {
			String senhaAtualCodificada = Base64.getEncoder().encodeToString(senhaAtual.getBytes());
			if (!usuario.getSenha().equals(senhaAtualCodificada)) {
				model.addAttribute("serverMessage", "Senha atual incorreta.");
				return "trocar-senha";
			}

			// Codifica a nova senha em Base64
			String novaSenhaCodificada = Base64.getEncoder().encodeToString(novaSenha.getBytes());

			usuario.setSenha(novaSenhaCodificada); // Salva a senha codificada
			usuarioService.updateMinhaConta(usuario);

	        session.setAttribute("serverMessage", "Senha alterada com sucesso!"); // Adiciona a mensagem na sessão
	        return "redirect:/AKECY/usuario/minha-conta"; // Redireciona para minha-conta
	    } else {
			model.addAttribute("serverError", "Erro ao alterar a senha. Tente novamente.");
		}

		return "trocar-senha";
	}

	@PostMapping("/verificar-senha-atual")
	@ResponseBody
	public String verificarSenhaAtual(@RequestParam String senhaAtual, HttpSession session) {
		String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");
		if (loggedInUserEmail == null) {
			return "false";
		}

		Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
		if (usuario != null) {
			String senhaAtualCodificada = Base64.getEncoder().encodeToString(senhaAtual.getBytes());
			if (usuario.getSenha().equals(senhaAtualCodificada)) {
				return "true";
			}
		}
		return "false";
	}

	@GetMapping("/favoritos")
	public String listarFavoritos(Model model, HttpSession session) {
		String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

		if (loggedInUserEmail != null) {
			Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
			List<Favorito> favoritos = favoritoService.findByUsuario(usuario);

			List<Produto> produtos = favoritos.stream().map(Favorito::getProduto).toList();

			for (Produto produto : produtos) {
				if (produto.getFoto1() != null) {
					String base64Image = Base64.getEncoder().encodeToString(produto.getFoto1());
					produto.setBase64Image(base64Image);
				}
			}

			model.addAttribute("produtos", produtos);
		}
		return "favoritos";
	}

}
