package br.itb.projeto.AKECY.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Usuario;
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

    public Usuario findById(long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.orElse(null);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
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
            String senhaPadrao = "12345678";
            String senhaCodificada = Base64.getEncoder().encodeToString(senhaPadrao.getBytes());

            usuario.setSenha(senhaCodificada);
            usuario.setDataCadastro(LocalDateTime.now());
            usuario.setStatusUsuario("TROCAR_SENHA");

            return usuarioRepository.save(usuario);
        }
        return null;
    }

    // A CONTA DE USUÁRIO SERÁ CRIADA COM SENHA DEFINIDA POR ELE
    @Transactional
    public Usuario create(Usuario usuario) {
        String senhaCodificada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());

        usuario.setSenha(senhaCodificada);
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setStatusUsuario("ATIVO");

        return usuarioRepository.save(usuario);
    }

    // Método para acessar (login) comparando senha com Base64
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

            // Verifica se há uma nova senha e a criptografa
            if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
                String senhaCriptografada = Base64.getEncoder().encodeToString(usuario.getSenha().getBytes());
                usuarioAtualizado.setSenha(senhaCriptografada);
            }

            // Atualiza o status do usuário
            usuarioAtualizado.setStatusUsuario("ATIVO");

            return usuarioRepository.save(usuarioAtualizado);
        }

        return null; // Retorna null se o usuário não for encontrado
    }
}

