package br.itb.projeto.AKECY.controller;

import java.util.List;
import java.io.IOException;
import java.util.Base64;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.UsuarioRepository;
import br.itb.projeto.AKECY.rest.exception.ResourceNotFoundException;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
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
            usuarioService.create(usuario);
            session.setAttribute("serverMessage", "Usuário cadastrado com sucesso!!!");
        } else {
            session.setAttribute("serverMessage", "Usuário já cadastrado no sistema!");
        }

        if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()) {
            session.setAttribute("serverMessage", "Dados Incompletos!!!");
        }

        return "redirect:/AKECY/usuario/login";
    }

    @GetMapping("/login")
    public String showFormLogin(Model model, HttpSession session) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
        session.removeAttribute("serverMessage"); // Remove a mensagem após ser adicionada ao modelo
        return "login";
    }



    @PostMapping("/verificar-login")
    public String verificarLogin(@ModelAttribute("usuario") Usuario usuario, Model model, HttpSession session) {
        Usuario _usuario = usuarioService.acessar(usuario.getEmail(), usuario.getSenha());
        

        if (_usuario != null) {
            if ("INATIVO".equals(_usuario.getStatusUsuario())) {
                session.setAttribute("serverMessage", "Seu usuário está inativo.");
                return "login";
            }

            if ("ADMIN".equals(_usuario.getNivelAcesso())) {
                return "redirect:/AKECY/usuario/index-adm";
            } else {
                return "redirect:/AKECY/usuario/index";
            }
        } else {
            session.setAttribute("serverMessage", "Email ou senha incorretos.");
            return "login"; // Retorna para a página de login
        }
    }


    @GetMapping("/mudar-senha")
    public String showFormMudarSenha(Model model, HttpSession session) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
        session.removeAttribute("serverMessage");
        return "mudar-senha";
    }

    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "telefone", required = false) String telefone,
                               HttpSession session) {
        if ((email == null || email.isEmpty()) && (telefone == null || telefone.isEmpty())) {
            session.setAttribute("serverMessage", "Informe o e-mail ou o telefone.");
            return "redirect:/AKECY/usuario/mudar-senha";
        }

        Usuario usuario = usuarioService.solicitarTrocaSenha(email != null ? email : telefone);

        if (usuario == null) {
            session.setAttribute("serverMessage", "Usuário não encontrado.");
        } else if ("INATIVO".equals(usuario.getStatusUsuario())) {
            session.setAttribute("serverMessage", "Usuário inativo.");
        } else {
            session.setAttribute("emailOuTelefoneRecuperado", email != null ? email : telefone);
            session.setAttribute("serverMessage", "Código enviado para o e-mail/telefone informado.");
        }

        return "redirect:/AKECY/usuario/mudar-senha";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam("codigo") String codigo, HttpSession session) {
        if ("000000".equals(codigo)) {
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        } else {
            session.setAttribute("serverMessage", "Código inválido.");
            return "redirect:/AKECY/usuario/mudar-senha";
        }
    }

    @GetMapping("/mudar-senha-confirmar")
    public String showMudarSenhaConfirmar(Model model, HttpSession session) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", session.getAttribute("serverMessage") != null ? session.getAttribute("serverMessage") : "");
        session.removeAttribute("serverMessage");
        return "mudar-senha-confirmar";
    }

    
    @PostMapping("/mudar-senha-confirmar")
    public String mudarSenhaConfirmar(@RequestParam("novaSenha") String novaSenha, HttpSession session) {
        String emailOuTelefoneRecuperado = (String) session.getAttribute("emailOuTelefoneRecuperado");

        if (emailOuTelefoneRecuperado == null || emailOuTelefoneRecuperado.isEmpty()) {
            session.setAttribute("serverMessage", "Erro ao alterar a senha. Tente novamente.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }

        Usuario usuario = usuarioService.findByEmail(emailOuTelefoneRecuperado);
        if (usuario == null) {
            usuario = usuarioService.findByTelefone(emailOuTelefoneRecuperado);
        }

        String novaSenhaConfirmacao = (String) session.getAttribute("novaSenhaConfirmacao");

        if (novaSenhaConfirmacao == null || !novaSenhaConfirmacao.equals(novaSenha)) {
            session.setAttribute("serverMessage", "As senhas não coincidem. Por favor, verifique.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }

        if (usuario != null && "TROCAR_SENHA".equals(usuario.getStatusUsuario())) {
            usuario.setSenha(novaSenha); // Certifique-se de que a senha seja criptografada
            usuario.setStatusUsuario("ATIVO");
            usuarioService.update(usuario);
            session.setAttribute("serverMessage", "Senha alterada com sucesso!");
            return "redirect:/AKECY/usuario/login";
        } else {
            session.setAttribute("serverMessage", "Usuário não encontrado ou não está autorizado a trocar a senha.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }
    }




    
    @GetMapping("/index")
    public String showIndex(Model model) {
        return "index";
    }

    @GetMapping("/index-adm")
    public String showIndexAdm(Model model) {
        return "index - adm";
    }


		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("findAll")
	public ResponseEntity<List<Usuario>> findAll() {
		List<Usuario> usuarios = usuarioService.findAll();

		return new ResponseEntity<List<Usuario>>(usuarios, HttpStatus.OK);
	}

	@GetMapping("findById/{id}")
	public ResponseEntity<Usuario> findById(@PathVariable long id) {

		Usuario usuario = usuarioService.findById(id);

		if (usuario != null) {
			return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException("*** Usuário não encontrado! *** " + "ID: " + id);
		}

	}

	@GetMapping("findByEmail/")
	public ResponseEntity<Usuario> findByEmail(@RequestParam String email) {

		Usuario usuario = usuarioService.findByEmail(email);

		if (usuario != null) {
			return new ResponseEntity<Usuario>(usuario, HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException("*** Usuário não encontrado! *** " + "E-mail: " + email);
		}

	}

	@PostMapping("create")
	public ResponseEntity<?> create(@RequestBody Usuario usuario) {

		Usuario _usuario = usuarioService.create (usuario);
		
		if (_usuario == null) {

			return ResponseEntity.badRequest().body(
					new MessageResponse("Usuário já cadastrado!"));
		}
		return ResponseEntity.ok()
				.body(new MessageResponse("Usuário cadastrado com sucesso!"));
	}
/*
	@PostMapping("signin")
	public ResponseEntity<?> signin(
			@RequestParam String email, @RequestParam String senha) {

		Usuario usuario = usuarioService.signin(email, senha);
		if (usuario != null) {
			return ResponseEntity.ok().body(usuario);
		}
		return ResponseEntity.badRequest().body("Dados incorretos!");
	}
	*/
	
	@PostMapping("/signin")
	public ResponseEntity<?> signin(@RequestBody Usuario usuario) {

		Usuario _usuario = usuarioService
				.signin(usuario.getEmail(), usuario.getSenha());

		if (_usuario == null) {
			throw new ResourceNotFoundException("*** Dados Incorretos! *** ");
		}

		return ResponseEntity.ok(_usuario);
	}
	

	@PutMapping("inativar/{id}")
	public ResponseEntity<Usuario> inativar(@PathVariable long id) {

		Usuario _usuario = usuarioService.inativar(id);

		return new ResponseEntity<Usuario>(_usuario, HttpStatus.OK);
	}

	@PutMapping("reativar/{id}")
	public ResponseEntity<Usuario> reativar(@PathVariable long id) {

		Usuario _usuario = usuarioService.reativar(id);

		return new ResponseEntity<Usuario>(_usuario, HttpStatus.OK);
	}

	@PutMapping("alterarSenha/{id}")
	public ResponseEntity<?> alterarSenha(
			@PathVariable long id, @RequestBody Usuario usuario) {

		Usuario _usuario = usuarioService.alterarSenha(id, usuario);

		//return new ResponseEntity<Usuario>(_usuario, HttpStatus.OK);
		return ResponseEntity.ok()
				.body(new MessageResponse("Senha alterada com sucesso!"));
	}

}