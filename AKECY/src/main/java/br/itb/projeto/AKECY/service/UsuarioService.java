package br.itb.projeto.AKECY.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

	private UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> findAll() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios;
	}

	public Usuario findById(long id) {

		Optional<Usuario> usuario = usuarioRepository.findById(id);

		if (usuario.isPresent()) {
			return usuario.get();
		}

		return null;
	}

	public Usuario findByEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email);
		if (usuario != null) {
			return usuario;
		}
		return null;
	}
	
	public Usuario findByTelefone(String telefone) {
        return usuarioRepository.findByTelefone(telefone);
    }

	// A CONTA DE USUÁRIO SERÁ CRIADA COM UMA SENHA PADRÃO
	// ELE DEVE ALTERAR NO PRIMEIRO ACESSO
	@Transactional
	public Usuario createNew(Usuario usuario) {
		
		Usuario _usuario = usuarioRepository.findByEmail(usuario.getEmail());

		if (_usuario == null) {
			String senha = Base64.getEncoder()
					.encodeToString("12345678".getBytes());
			
			usuario.setSenha(senha);
			usuario.setDataCadastro(LocalDateTime.now());
			usuario.setStatusUsuario("TROCAR_SENHA");
			
			return usuarioRepository.save(usuario);
		}
		return null;
		
	}
	
	// A CONTA DE USUÁRIO SERÁ CRIADA COM SENHA DEFINIDA POR ELE
	@Transactional
	public Usuario create(Usuario usuario) {
		
		String senha = Base64.getEncoder()
			.encodeToString(usuario.getSenha().getBytes());
		
		usuario.setSenha(senha);
		usuario.setDataCadastro(LocalDateTime.now());
		usuario.setStatusUsuario("ATIVO");
		
		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public Usuario acessar(String email, String senha) {
	    Usuario usuario = usuarioRepository.findByEmail(email);
	    
	    // Converte a senha em base64
	    String _senha = Base64.getEncoder().encodeToString(senha.getBytes());
	    
	    // Se o usuário existe
	    if (usuario != null) {
	        // Verifica se a senha é correta
	        if (usuario.getSenha().equals(_senha)) {
	            return usuario; // Retorna o usuário, independentemente do status
	        }
	    }
	    
	    return null; // Se a senha estiver errada ou o usuário não existir, retorna null
	}

	
	@Transactional
	public Usuario signin(String email, String senha) {
	 Usuario usuario = usuarioRepository.findByEmail(email);
		
	 if(usuario != null) {
		if (!usuario.getStatusUsuario().equals("INATIVO")) {
			byte[] decodedPass = Base64.getDecoder()
								.decode(usuario.getSenha());
			if (new String(decodedPass).equals(senha)) {
				return usuario;
			}
		}
	 }
		return null;
	}
	
	@Transactional
	public Usuario inativar(long id) {
		Optional<Usuario> _usuario = 
				usuarioRepository.findById(id);
		
		if (_usuario.isPresent()) {
			Usuario usuarioAtualizada = _usuario.get();
			usuarioAtualizada.setStatusUsuario("INATIVO");
			
			return usuarioRepository.save(usuarioAtualizada);
		}
		return null;
	}
	
	@Transactional
	public Usuario reativar(long id) {
		Optional<Usuario> _usuario = 
				usuarioRepository.findById(id);
		
		if (_usuario.isPresent()) {
			Usuario usuarioAtualizado = _usuario.get();
			String senha = Base64.getEncoder()
					.encodeToString("12345678".getBytes());
				
			usuarioAtualizado.setSenha(senha);
			usuarioAtualizado.setDataCadastro(LocalDateTime.now());
			usuarioAtualizado.setStatusUsuario("ATIVO");
			
			return usuarioRepository.save(usuarioAtualizado);
		}
		return null;
	}
	
	@Transactional
	public Usuario alterarSenha(long id, Usuario usuario) {
		Optional<Usuario> _usuario = 
				usuarioRepository.findById(id);
		
		if (_usuario.isPresent()) {
			Usuario usuarioAtualizado = _usuario.get();
			String senha = Base64.getEncoder()
					.encodeToString(usuario.getSenha().getBytes());
				
			usuarioAtualizado.setSenha(senha);
			usuarioAtualizado.setDataCadastro(LocalDateTime.now());
			usuarioAtualizado.setStatusUsuario("ATIVO");
			
			return usuarioRepository.save(usuarioAtualizado);
		}
		return null;
	}
	
	
	
	@Transactional
	public Usuario solicitarTrocaSenha(String emailOuTelefone) {
	    // Procura o usuário pelo email
	    Usuario usuario = usuarioRepository.findByEmail(emailOuTelefone);
	    
	    // Se não encontrar pelo email, tenta encontrar pelo telefone
	    if (usuario == null) {
	        usuario = usuarioRepository.findByTelefone(emailOuTelefone);
	    }
	    
	    // Se o usuário for encontrado e não estiver inativo
	    if (usuario != null && !"INATIVO".equals(usuario.getStatusUsuario())) {
	        usuario.setStatusUsuario("TROCAR_SENHA");
	        return usuarioRepository.save(usuario);
	    }
	    
	    // Se o usuário não for encontrado ou estiver inativo
	    return null;
	}
	
	
	
	
	@Transactional
	public Usuario update(Usuario usuario) {
	    Optional<Usuario> existingUser = usuarioRepository.findById(usuario.getIdUsuario());

	    if (existingUser.isPresent()) {
	        Usuario usuarioAtualizado = existingUser.get();

	        // Criptografar a nova senha se fornecida
	        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
	            String senhaCriptografada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());
	            usuarioAtualizado.setSenha(senhaCriptografada);
	        }

	        // Atualizar outras informações, se necessário
	        usuarioAtualizado.setDataCadastro(LocalDateTime.now());
	        usuarioAtualizado.setStatusUsuario("ATIVO");

	        return usuarioRepository.save(usuarioAtualizado);
	    }

	    return null; // Retorna null se o usuário não for encontrado
	}
	
}
