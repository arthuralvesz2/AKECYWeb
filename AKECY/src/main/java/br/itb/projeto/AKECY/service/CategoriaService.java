package br.itb.projeto.AKECY.service;

import java.util.List;
import org.springframework.stereotype.Service;
import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.repository.CategoriaRepository;
import br.itb.projeto.AKECY.model.repository.ProdutoRepository;

@Service
public class CategoriaService {

    private CategoriaRepository categoriaRepository;
    private ProdutoRepository produtoRepository; // Adicione esta linha

    public CategoriaService(CategoriaRepository categoriaRepository, ProdutoRepository produtoRepository) { // Modifique esta linha
        super();
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository; // Adicione esta linha
    }

    public List<Produto> findProdutosByNomeCategoria(String nomeCategoria) {
        Categoria categoria = categoriaRepository.findByNome(nomeCategoria);
        return produtoRepository.findByCategoria(categoria);
    }
    
	public List<Categoria> findAll() {
		List<Categoria> categorias = categoriaRepository.findAll();
		return categorias;
	}
}