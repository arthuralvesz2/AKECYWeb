package br.itb.projeto.AKECY.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	@Query(value = "SELECT * FROM Produto WHERE statusProd = 'ATIVO' AND idCategoria IN (SELECT idCategoria FROM Categoria WHERE nome IN ('Equipamentos', 'Bolas')) ORDER BY NEWID() OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", nativeQuery = true)
	List<Produto> findRandom10ProductsFromCategories();

	@Query(value = "SELECT * FROM Produto WHERE statusProd = 'ATIVO' ORDER BY idProduto DESC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", nativeQuery = true)
	List<Produto> findTop10RecentProducts();

	@Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.statusProd = 'ATIVO'")
	List<Produto> findByCategoriaAtiva(@Param("categoria") Categoria categoria);

	@Query("SELECT p FROM Produto p WHERE (LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))) AND p.statusProd = 'ATIVO'")
	List<Produto> findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(@Param("nome") String nome, @Param("descricao") String descricao);

}	