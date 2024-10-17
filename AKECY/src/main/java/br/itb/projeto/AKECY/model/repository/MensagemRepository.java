package br.itb.projeto.AKECY.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.itb.projeto.AKECY.model.entity.Mensagem;

@Repository
public interface MensagemRepository extends JpaRepository<Mensagem, Long> {
	
	List<Mensagem> findByStatusMensagem(String status);

}
