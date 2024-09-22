package br.itb.projeto.AKECY.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.repository.ProdutoRepository;
import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository produtoRepository;

	public ProdutoService(ProdutoRepository produtoRepository) {
		super();
		this.produtoRepository = produtoRepository;
	}
	
	public List<Produto> findAll(){
		List<Produto> produtos = produtoRepository.findAll();
		return produtos;
	}
	
	@Transactional
	public Produto create(Produto produto) {
		
		produto.setIdProduto(null);
		produto.setNome(null);
		produto.setDescricao(null);
		produto.setDescricao_completa(null);
		produto.setTamanhos_disponiveis(null);
		produto.setFoto1(null);
		produto.setFoto2(null);
		produto.setFoto3(null);
		produto.setFoto4(null);
		produto.setFoto5(null);
		produto.setPreco(null);
		produto.setCategoria(null);
		produto.setStatusProd("ATIVO");
		
		return produtoRepository.save(produto);
	}
	
	public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }
	
	@Transactional
	public Produto inativar(long id) {
		Optional<Produto> _produto = 
				produtoRepository.findById(id);
		
		if (_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();
			produtoAtualizado.setStatusProd("INATIVO");
			
			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}
	
	@Transactional
	public Produto alterar(long id, Produto produto) {
		Optional<Produto> _produto = 
				produtoRepository.findById(id);
		
		if (_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();
			
			produtoAtualizado.setPreco(produto.getPreco());
			
			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}
	
	@Transactional
	public Produto reativar(long id) {
		Optional<Produto> _produto = 
				produtoRepository.findById(id);
		
		if(_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();
			produtoAtualizado.setStatusProd("ATIVO");
			
			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}
	
	    public List<Produto> getProdutosEmDestaque() {
	        return produtoRepository.findRandom10Products(); 
	    }
	    
	    public List<Produto> getProdutosRecentes() {
	        return produtoRepository.findAllByOrderByIdProdutoDesc(); 
	    }
	}