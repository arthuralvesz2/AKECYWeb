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
        session.removeAttribute("serverMessage"); // Remover a mensagem após ser adicionada ao modelo
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("usuario") Usuario usuario, HttpSession session) {
        Usuario _usuario = usuarioService.findByEmail(usuario.getEmail());

        if (_usuario == null) {
            if (usuario.getNome().isEmpty() || usuario.getEmail().isEmpty() || usuario.getSenha().isEmpty()) {
                session.setAttribute("serverMessage", "Dados Incompletos!!!");
            } else {
                usuarioService.create(usuario); // Senha será codificada no serviço
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
        session.removeAttribute("serverMessage"); // Remover a mensagem após ser adicionada ao modelo
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
            
         // Extrair o primeiro nome do usuário
            String primeiroNome = _usuario.getNome().split(" ")[0];
            System.out.println("Primeiro Nome: " + primeiroNome);
            // Armazenar o primeiro nome do usuário na sessão
            session.setAttribute("loggedInUser", primeiroNome);

            if ("ADMIN".equals(_usuario.getNivelAcesso())) {
                return "redirect:/AKECY/usuario/index-adm";
            } else {
                return "redirect:/AKECY/usuario/index";
            }
        } else {
            session.setAttribute("serverMessage", "Email ou senha incorretos.");
            return "redirect:/AKECY/usuario/login";
        }
    }

    @GetMapping("/mudar-senha")
    public String showFormMudarSenha(Model model, HttpSession session) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", session.getAttribute("serverMessage"));
        model.addAttribute("codigoEnviado", session.getAttribute("codigoEnviado")); // Adiciona ao modelo
        session.removeAttribute("serverMessage"); // Remover a mensagem após ser adicionada ao modelo
        session.removeAttribute("codigoEnviado"); // Remover códigoEnviado após ser usado
        return "mudar-senha";
    }

    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam(value = "email", required = false) String email,
                               HttpSession session) {
        if (email == null || email.isEmpty()) {
            session.setAttribute("serverMessage", "Informe o e-mail.");
            session.setAttribute("codigoEnviado", false); // Garante que não exibirá o campo de código
            return "redirect:/AKECY/usuario/mudar-senha";
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
            session.setAttribute("codigoEnviado", true); // Sinaliza que o código foi enviado com sucesso
        }

        return "redirect:/AKECY/usuario/mudar-senha";
    }

    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam("codigo") String codigo, HttpSession session) {
        if ("000000".equals(codigo)) { // Simulação de código
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
        session.removeAttribute("serverMessage"); // Remover a mensagem após ser adicionada ao modelo
        return "mudar-senha-confirmar";
    }

    @PostMapping("/mudar-senha-confirmar")
    public String mudarSenhaConfirmar(@RequestParam("senha") String novaSenha, 
                                      @RequestParam("novaSenhaConfirmacao") String novaSenhaConfirmacao, 
                                      HttpSession session) {
        System.out.println("Nova Senha: " + novaSenha);
        System.out.println("Confirmação de Senha: " + novaSenhaConfirmacao);
        String emailRecuperado = (String) session.getAttribute("emailRecuperado");

        if (emailRecuperado == null || emailRecuperado.isEmpty()) {
            System.out.println("Email não encontrado.");
            session.setAttribute("serverMessage", "Erro ao alterar a senha. Tente novamente.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }

        if (novaSenha == null || novaSenha.isEmpty() || novaSenhaConfirmacao == null || novaSenhaConfirmacao.isEmpty()) {
            System.out.println("Senha ou confirmação de senha estão vazias.");
            session.setAttribute("serverMessage", "Por favor, insira e confirme sua nova senha.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }

        if (!novaSenha.equals(novaSenhaConfirmacao)) {
            System.out.println("As senhas não coincidem.");
            session.setAttribute("serverMessage", "As senhas não coincidem. Por favor, verifique.");
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        }

        Usuario usuario = usuarioService.findByEmail(emailRecuperado);
        System.out.println("Usuário encontrado: " + usuario);

        if (usuario != null && "TROCAR_SENHA".equals(usuario.getStatusUsuario())) {
            usuario.setSenha(novaSenha); // O serviço irá codificar a senha
            usuario.setStatusUsuario("ATIVO");

            Usuario usuarioAtualizado = usuarioService.update(usuario);
            System.out.println("Usuário atualizado: " + usuarioAtualizado);

            if (usuarioAtualizado != null) {
                session.setAttribute("serverMessage", "Senha alterada com sucesso!");
                return "redirect:/AKECY/usuario/login";
            } else {
                System.out.println("Erro ao atualizar o usuário.");
                session.setAttribute("serverMessage", "Erro ao atualizar a senha.");
                return "redirect:/AKECY/usuario/mudar-senha-confirmar";
            }
        } else {
            System.out.println("Usuário não encontrado ou não autorizado.");
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
        return "index-adm";
    }
    
    @GetMapping("/dados-pessoais")
    public String showDadosPessoais(Model model) {
        return "dados-pessoais";
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

		Usuario _usuario = usuarioService.create(usuario);

		if (_usuario == null) {

			return ResponseEntity.badRequest().body(new MessageResponse("Usuário já cadastrado!"));
		}
		return ResponseEntity.ok().body(new MessageResponse("Usuário cadastrado com sucesso!"));
	}
	/*
	 * @PostMapping("signin") public ResponseEntity<?> signin(
	 * 
	 * @RequestParam String email, @RequestParam String senha) {
	 * 
	 * Usuario usuario = usuarioService.signin(email, senha); if (usuario != null) {
	 * return ResponseEntity.ok().body(usuario); } return
	 * ResponseEntity.badRequest().body("Dados incorretos!"); }
	 */


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
	public ResponseEntity<?> alterarSenha(@PathVariable long id, @RequestBody Usuario usuario) {

		Usuario _usuario = usuarioService.alterarSenha(id, usuario);

		// return new ResponseEntity<Usuario>(_usuario, HttpStatus.OK);
		return ResponseEntity.ok().body(new MessageResponse("Senha alterada com sucesso!"));
	}

}
