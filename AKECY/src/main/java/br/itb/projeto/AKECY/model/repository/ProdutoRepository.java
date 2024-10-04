package br.itb.projeto.AKECY.model.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

	@Query(value = "SELECT * FROM Produto ORDER BY NEWID() OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY", nativeQuery = true)
	List<Produto> findRandom10Products();

	List<Produto> findAllByOrderByIdProdutoDesc();

	List<Produto> findByCategoria(Categoria categoria);
	
	List<Produto> findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(String nome, String descricao);
}	