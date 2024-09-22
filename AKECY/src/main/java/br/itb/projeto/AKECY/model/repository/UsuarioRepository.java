package br.itb.projeto.AKECY.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.itb.projeto.AKECY.model.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmailAndSenha(String email, String senha);
	Usuario findByEmail(String email);
	Usuario findByTelefone(String telefone);
	
	
}
