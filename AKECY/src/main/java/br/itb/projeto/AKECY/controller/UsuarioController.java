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


@Controller
@RequestMapping("/AKECY/usuario/")
public class UsuarioController {

	
	private UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		super();
		this.usuarioService = usuarioService;
	}
	
	private String serverMessage = null;
	
	//leva para a tela de cadastro
	@GetMapping("/cadastro")
    public String showFormCadastroUsuario(Usuario usuario, Model model) {
		
		model.addAttribute("usuario", usuario);
		model.addAttribute("serverMessage", serverMessage);
		serverMessage = "";
		
		return "cadastro";
	}
	
	//action do formulario que salva os usuarios e retorna para a tela de login
	@PostMapping("/salvar")
	public String salvar(ModelMap model,
			@ModelAttribute("usuario") Usuario usuario) {
		
		Usuario _usuario = usuarioService.findByEmail(usuario.getEmail());
		
		if (_usuario == null) {
			
			usuarioService.create(usuario);
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Usuário cadastrado com sucesso!!!";
			
		} else if (_usuario != null) {
			
			model.addAttribute("usuario", new Usuario());
			serverMessage = "Usuário já cadastrado no sistema!";	
			
		}
		
		if (usuario.getNome().equals("") || usuario.getEmail().equals("") || usuario.getSenha().equals("")) {
			
			serverMessage = "Dados Incompletos!!!";	
			
		} 
	
		return "redirect:/AKECY/usuario/login";
	}
    
	//leva para a tela de login
	@GetMapping("/login")
    public String showFormLogin(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", serverMessage);
		serverMessage = "";
        return "login";
    }
	
	//action para se o usuario está ativo ou se é um adm
	@PostMapping("/verificar-login")
	public String verificarLogin(@ModelAttribute("usuario") Usuario usuario, Model model) {
	    // Chama o método acessar do serviço para verificar o login
	    Usuario _usuario = usuarioService.acessar(usuario.getEmail(), usuario.getSenha());

	    if (_usuario != null) {
	        // Se o status for INATIVO, exibe a mensagem apropriada
	        if ("INATIVO".equals(_usuario.getStatusUsuario())) {
	            model.addAttribute("serverMessage", "Seu usuário está inativo.");
	            return "login"; // Mantém o usuário na página de login com a mensagem de erro
	        }
	        
	        // Verifica o nível de acesso para redirecionar o usuário para a página correta
	        if ("ADMIN".equals(_usuario.getNivelAcesso())) {
	            return "redirect:/AKECY/usuario/index-adm"; // Página para ADMIN
	        } else {
	            return "redirect:/AKECY/usuario/index"; // Página padrão para usuários ativos não-admin
	        }
	    } else {
	        // Mensagem genérica se o login falhar
	        model.addAttribute("serverMessage", "Email ou senha incorretos.");
	        return "login"; // Retorna para a página de login com mensagem de erro
	    }
	}
	
	
	
	private boolean codigoEnviado = false;

	// Exibe a página de troca de senha (única para enviar e verificar código)
    @GetMapping("/mudar-senha")
    public String showFormMudarSenha(Model model) {
    	model.addAttribute("usuario", new Usuario());
        model.addAttribute("serverMessage", serverMessage);
        model.addAttribute("codigoEnviado", codigoEnviado);
        serverMessage = "";
        return "mudar-senha";
    }

    // Action que envia o código
    @PostMapping("/enviar-codigo")
    public String enviarCodigo(@RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "telefone", required = false) String telefone,
                               Model model) {
        if ((email == null || email.isEmpty()) && (telefone == null || telefone.isEmpty())) {
            serverMessage = "Informe o e-mail ou o telefone.";
            return "redirect:/AKECY/usuario/mudar-senha";
        }

        Usuario usuario = null;

        if (email != null && !email.isEmpty()) {
            usuario = usuarioService.findByEmail(email);
        } else if (telefone != null && !telefone.isEmpty()) {
            usuario = usuarioService.findByTelefone(telefone);
        }

        if (usuario != null) {
            // Simulando que o código foi enviado
            serverMessage = "Código enviado para o e-mail/telefone informado.";
            codigoEnviado = true;  // Código foi enviado, agora podemos verificar
        } else {
            serverMessage = "Usuário não encontrado.";
            codigoEnviado = false;
        }

        return "redirect:/AKECY/usuario/mudar-senha";
    }

    // Action que verifica o código e redireciona para a confirmação de troca de senha
    @PostMapping("/verificar-codigo")
    public String verificarCodigo(@RequestParam("codigo") String codigo,
                                  Model model) {
        // Verifica se o código é "000000"
        if ("000000".equals(codigo)) {
            return "redirect:/AKECY/usuario/mudar-senha-confirmar";
        } else {
            serverMessage = "Código inválido.";
            return "redirect:/AKECY/usuario/mudar-senha";
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
		
	@GetMapping("/mudar-senha-confirmar")
    public String showMudarSenhaConfirmar(Model model) {
        return "mudar-senha-confirmar";
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
