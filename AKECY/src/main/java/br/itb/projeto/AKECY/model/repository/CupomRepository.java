package br.itb.projeto.AKECY.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.itb.projeto.AKECY.model.entity.Cupom;


@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {
    Cupom findByCodigo(String codigo);
}
