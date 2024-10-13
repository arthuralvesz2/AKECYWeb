package br.itb.projeto.AKECY.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.time.format.DateTimeFormatter;

import br.itb.projeto.AKECY.model.entity.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/AKECY/ADM/")
public class ADMController {

	@Autowired
	private br.itb.projeto.AKECY.service.UsuarioService usuarioService;

	@GetMapping("/adicionar-produto")
	public String adicionar(Model model) {

		return "adm-adicionar-produto";
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