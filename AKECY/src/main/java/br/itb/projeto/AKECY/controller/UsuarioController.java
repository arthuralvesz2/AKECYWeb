package br.itb.projeto.AKECY.controller;

import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.itb.projeto.AKECY.model.entity.Usuario; 

import br.itb.projeto.AKECY.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/usuario/")
public class UsuarioController {

	private final UsuarioService usuarioService;

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
		Usuario _usuario = usuarioService.findByEmail(usuario.getEmail());

		if (_usuario == null) {
			if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()) {
				session.setAttribute("serverMessage", "Dados Incompletos!!!");
			} else {
				usuario.setNivelAcesso("USER");
				usuarioService.create(usuario);
				session.setAttribute("serverMessage", "Usuário cadastrado com sucesso!!!");
			}
		} else {
			session.setAttribute("serverMessage", "Usuário já cadastrado no sistema!");
		}

		return "redirect:/AKECY/usuario/login";
	}

	@GetMapping("/login")
	public String showFormLogin(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
		session.removeAttribute("serverMessage");
		return "login";
	}

	@PostMapping("/verificar-login")
	public String verificarLogin(@ModelAttribute("usuario") Usuario usuario, Model model, HttpSession session) {
		Usuario _usuario = usuarioService.acessar(usuario.getEmail(), usuario.getSenha());

		if (_usuario != null) {
			if ("INATIVO".equals(_usuario.getStatusUsuario())) {
				session.setAttribute("serverMessage", "Seu usuário está inativo.");
				return "redirect:/AKECY/usuario/login";
			}

			String primeiroNome = _usuario.getNome().split(" ")[0];
			session.setAttribute("loggedInUser", primeiroNome); // Armazena o nome na sessão
			session.setAttribute("loggedInUserEmail", _usuario.getEmail()); // Armazena o email na sessão
			System.out.println("Nome do usuário armazenado na sessão: " + session.getAttribute("loggedInUserName"));
			System.out.println("Email do usuário armazenado na sessão: " + session.getAttribute("loggedInUserEmail"));

			if ("ADMIN".equals(_usuario.getNivelAcesso())) {
				return "redirect:/AKECY/index-adm";
			} else {
				return "redirect:/AKECY/index";
			}
		} else {
			session.setAttribute("serverMessage", "Email ou senha incorretos.");
			return "redirect:/AKECY/usuario/login";
		}
	}

	@GetMapping("/recuperar-senha")
	public String showFormRecuperarSenha(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
		model.addAttribute("codigoEnviado", session.getAttribute("codigoEnviado"));
		session.removeAttribute("serverMessage");
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
			session.setAttribute("serverMessage", "Usuário não encontrado.");
			session.setAttribute("codigoEnviado", false);
		} else if ("INATIVO".equals(usuario.getStatusUsuario())) {
			session.setAttribute("serverMessage", "Usuário inativo.");
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
			session.setAttribute("serverMessage", "Código inválido.");
			return "redirect:/AKECY/usuario/recuperar-senha";
		}
	}

	@GetMapping("/recuperar-senha-confirmar")
	public String showRecuperarSenhaConfirmar(Model model, HttpSession session) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("serverMessage",
				session.getAttribute("serverMessage") != null ? session.getAttribute("serverMessage") : "");
		session.removeAttribute("serverMessage");
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
			session.setAttribute("serverMessage", "Erro ao alterar a senha. Tente novamente.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}

		if (novaSenha == null || novaSenha.isEmpty() || novaSenhaConfirmacao == null
				|| novaSenhaConfirmacao.isEmpty()) {
			System.out.println("Senha ou confirmação de senha estão vazias.");
			session.setAttribute("serverMessage", "Por favor, insira e confirme sua nova senha.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}

		if (!novaSenha.equals(novaSenhaConfirmacao)) {
			System.out.println("As senhas não coincidem.");
			session.setAttribute("serverMessage", "As senhas não coincidem. Por favor, verifique.");
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
				session.setAttribute("serverMessage", "Erro ao atualizar a senha.");
				return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
			}
		} else {
			System.out.println("Usuário não encontrado ou não autorizado.");
			session.setAttribute("serverMessage", "Usuário não encontrado ou não está autorizado a trocar a senha.");
			return "redirect:/AKECY/usuario/recuperar-senha-confirmar";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("loggedInUser"); // Remove o atributo da sessão
		return "redirect:/AKECY/index"; // Redireciona para a página inicial
	}

	@GetMapping("/minha-conta")
	public String showDadosPessoais(Model model, HttpSession session) {
		String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail"); // Recupera o email da sessão

		if (loggedInUserEmail == null) {
			return "redirect:/AKECY/usuario/login";
		}

		Usuario usuario = usuarioService.findByEmail(loggedInUserEmail); // Busca pelo email completo

		if (usuario == null) {
			// Lidar com o caso em que o usuário não é encontrado (exibir uma mensagem de
			// erro)
			model.addAttribute("errorMessage", "Usuário não encontrado");
			usuario = new Usuario(); // Cria um novo objeto Usuario para evitar erros no Thymeleaf
		}

		model.addAttribute("usuario", usuario);
		return "minha-conta";
	}

	@PostMapping("/minha-conta")
	public String updateDadosPessoais(Usuario usuarioAtualizado, HttpSession session) {
	    String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

	    if (loggedInUserEmail == null) {
	        return "redirect:/AKECY/usuario/login";
	    }

	    // Buscar o usuário logado pelo email
	    Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
	    if (usuario != null) {
	        // Atualizar os dados do usuário existente
	        usuario.setNome(usuarioAtualizado.getNome());
	        
	        // Verificar se o email foi alterado
	        if (!usuarioAtualizado.getEmail().equals(loggedInUserEmail)) {
	            usuario.setEmail(usuarioAtualizado.getEmail());
	            usuario.setDataNasc(usuarioAtualizado.getDataNasc());
	            usuario.setCpf(usuarioAtualizado.getCpf());
	            usuario.setSexo(usuarioAtualizado.getSexo());
	            usuario.setTelefone(usuarioAtualizado.getTelefone());

	            usuarioService.update(usuario);

	            // Invalida a sessão atual
	            session.invalidate();

	            // Redireciona para a página de login
	            return "redirect:/AKECY/usuario/login"; 
	        } else {
	            usuario.setDataNasc(usuarioAtualizado.getDataNasc());
	            usuario.setCpf(usuarioAtualizado.getCpf());
	            usuario.setSexo(usuarioAtualizado.getSexo());
	            usuario.setTelefone(usuarioAtualizado.getTelefone());

	            usuarioService.update(usuario);
	        }
	    }

	    return "redirect:/AKECY/usuario/minha-conta";
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

            String novaSenhaCodificada = Base64.getEncoder().encodeToString(novaSenha.getBytes());
            usuario.setSenha(novaSenhaCodificada);
            usuarioService.update(usuario);

            model.addAttribute("serverMessage", "Senha alterada com sucesso!");
        } else {
            model.addAttribute("serverMessage", "Erro ao alterar a senha. Tente novamente.");
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

}