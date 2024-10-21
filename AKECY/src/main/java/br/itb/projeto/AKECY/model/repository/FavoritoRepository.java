package br.itb.projeto.AKECY.model.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.entity.Usuario;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    
    @Query("SELECT COUNT(f) > 0 FROM Favorito f WHERE f.usuario.id = :idUsuario AND f.produto.id = :idProduto AND f.produto.statusProd = 'ATIVO'")
    boolean existsByUsuarioIdUsuarioAndProdutoIdProduto(@Param("idUsuario") Long idUsuario, @Param("idProduto") Long idProduto);

    @Modifying
    @Query("DELETE FROM Favorito f WHERE f.usuario.id = :idUsuario AND f.produto.id = :idProduto AND f.produto.statusProd = 'ATIVO'")
    void deleteByUsuarioIdUsuarioAndProdutoIdProduto(@Param("idUsuario") Long idUsuario, @Param("idProduto") Long idProduto);

    @Query("SELECT f FROM Favorito f WHERE f.usuario = :usuario AND f.produto.statusProd = 'ATIVO'")
    List<Favorito> findByUsuario(@Param("usuario") Usuario usuario);
}
