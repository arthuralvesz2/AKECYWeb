package br.itb.projeto.AKECY.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.itb.projeto.AKECY.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Usuario findByTelefone(String telefone);
	Usuario findByEmailAndSenha(String email, String senha);
	Usuario findByEmail(String email);
    List<Usuario> findAllByOrderByDataCadastroDesc();
    
}
