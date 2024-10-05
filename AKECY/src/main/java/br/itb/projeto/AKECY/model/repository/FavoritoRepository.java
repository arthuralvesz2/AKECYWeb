package br.itb.projeto.AKECY.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.itb.projeto.AKECY.model.entity.Favorito;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    boolean existsByUsuarioIdUsuarioAndProdutoIdProduto(Long idUsuario, Long idProduto);
    void deleteByUsuarioIdUsuarioAndProdutoIdProduto(Long idUsuario, Long idProduto);
}