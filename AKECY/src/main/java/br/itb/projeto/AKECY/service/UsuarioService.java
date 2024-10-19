package br.itb.projeto.AKECY.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.entity.Usuario.NivelAcesso;
import br.itb.projeto.AKECY.model.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		super();
		this.usuarioRepository = usuarioRepository;
	}

	public List<Usuario> findAll() {
		List<Usuario> usuarios = usuarioRepository.findAll();
		return usuarios;
	}

	public void disable(long id) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario != null) {
			usuario.setStatusUsuario("INATIVO");
			usuarioRepository.save(usuario);
		}
	}

	public void enable(long id) {
		Usuario usuario = usuarioRepository.findById(id).orElse(null);
		if (usuario != null) {
			usuario.setStatusUsuario("ATIVO");
			usuarioRepository.save(usuario);
		}
	}
	
	public Usuario findById(long id) {
		  return usuarioRepository.findById(id).orElse(null);
		}
	
	public Usuario findByEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	public Usuario findByTelefone(String telefone) {
		return usuarioRepository.findByTelefone(telefone);
	}
	
	public Usuario findByCpf(String cpf) {
		return usuarioRepository.findByCpf(cpf);
	}

	@Transactional
	public Usuario create(Usuario usuario) {
		String senhaCodificada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());

		usuario.setSenha(senhaCodificada);
		usuario.setDataCadastro(LocalDateTime.now());
		usuario.setStatusUsuario("ATIVO");

		return usuarioRepository.save(usuario);
	}
	
	@Transactional
	public Usuario acessar(String email, String senha) {
		Usuario usuario = usuarioRepository.findByEmail(email);

		if (usuario != null) {
			String senhaCodificada = Base64.getEncoder().encodeToString(senha.getBytes());

			if (usuario.getSenha().equals(senhaCodificada)) {
				return usuario;
			}
		}

		return null;
	}

	@Transactional
	public Usuario inativar(long id) {
		Optional<Usuario> _usuario = usuarioRepository.findById(id);

		if (_usuario.isPresent()) {
			Usuario usuarioAtualizada = _usuario.get();
			usuarioAtualizada.setStatusUsuario("INATIVO");

			return usuarioRepository.save(usuarioAtualizada);
		}
		return null;
	}

	@Transactional
	public Usuario reativar(long id) {
		Optional<Usuario> _usuario = usuarioRepository.findById(id);

		if (_usuario.isPresent()) {
			Usuario usuarioAtualizado = _usuario.get();
			String senhaPadrao = "12345678";
			String senhaCodificada = Base64.getEncoder().encodeToString(senhaPadrao.getBytes());

			usuarioAtualizado.setSenha(senhaCodificada);
			usuarioAtualizado.setDataCadastro(LocalDateTime.now());
			usuarioAtualizado.setStatusUsuario("ATIVO");

			return usuarioRepository.save(usuarioAtualizado);
		}
		return null;
	}

	@Transactional
	public Usuario alterarSenha(long id, Usuario usuario) {
		Optional<Usuario> _usuario = usuarioRepository.findById(id);

		if (_usuario.isPresent()) {
			Usuario usuarioAtualizado = _usuario.get();
			String senhaCodificada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());

			usuarioAtualizado.setSenha(senhaCodificada);
			usuarioAtualizado.setDataCadastro(LocalDateTime.now());
			usuarioAtualizado.setStatusUsuario("ATIVO");

			return usuarioRepository.save(usuarioAtualizado);
		}
		return null;
	}

	public Usuario solicitarTrocaSenha(String email) {
	    return usuarioRepository.findByEmail(email); // Busca por email, sem filtro de status
	}

	@Transactional
	public Usuario update(Usuario usuario) {
	    Optional<Usuario> existingUser = usuarioRepository.findById(usuario.getIdUsuario());

	    if (existingUser.isPresent()) {
	        Usuario usuarioAtualizado = existingUser.get();

	        String novaSenhaCodificada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());
	        usuarioAtualizado.setSenha(novaSenhaCodificada);

	        usuarioAtualizado.setStatusUsuario("ATIVO");

	        return usuarioRepository.save(usuarioAtualizado);
	    }

	    return null;
	}
	
    @Transactional
    public Usuario updateMinhaConta(Usuario usuario) {
        Optional<Usuario> existingUser = usuarioRepository.findById(usuario.getIdUsuario());

        if (existingUser.isPresent()) {
            Usuario usuarioAtualizado = existingUser.get();

            usuarioAtualizado.setStatusUsuario("ATIVO");

            return usuarioRepository.save(usuarioAtualizado);
        }

        return null;
    }

	public List<Usuario> findAllOrderByDataCadastroDesc() {
		return usuarioRepository.findAllByOrderByDataCadastroDesc();
	}

	public void mudarNivelAcesso(int idUsuario) {
		Long usuarioId = (long) idUsuario;
		Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
		usuario.setNivelAcesso(usuario.getNivelAcesso() == NivelAcesso.USER ? NivelAcesso.ADMIN : NivelAcesso.USER);
		usuarioRepository.save(usuario);
	}

	public void save(Usuario usuario) {
		usuarioRepository.save(usuario);
	}
}
