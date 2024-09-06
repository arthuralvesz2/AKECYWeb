package br.itb.projeto.AKECY.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.rest.exception.ResourceNotFoundException;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
import br.itb.projeto.AKECY.service.UsuarioService;


@Controller
@RequestMapping("/usuario/")
public class UsuarioController {

	private UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		super();
		this.usuarioService = usuarioService;
	}
	
    
	@GetMapping("/login")
    public String loginUsuario() {
        return "login"; 
    }
	
	@GetMapping("/cadastro")
    public String cadastroUsuario() {
        return "cadastro"; 
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
