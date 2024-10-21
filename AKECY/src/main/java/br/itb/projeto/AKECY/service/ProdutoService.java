package br.itb.projeto.AKECY.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
    @Transactional
    public Produto save(Produto produto) {
        return produtoRepository.save(produto);
    }
    
    public void update(MultipartFile foto1, MultipartFile foto2, MultipartFile foto3, MultipartFile foto4, MultipartFile foto5, Produto produto) {
        try {
            if (foto1 != null && !foto1.isEmpty()) {
                produto.setFoto1(foto1.getBytes());
            }
            if (foto2 != null && !foto2.isEmpty()) {
                produto.setFoto2(foto2.getBytes());
            } else {
                produto.setFoto2(null); // Remove a imagem se foto2 for null
            }
            if (foto3 != null && !foto3.isEmpty()) {
                produto.setFoto3(foto3.getBytes());
            } else {
                produto.setFoto3(null); // Remove a imagem se foto3 for null
            }
            if (foto4 != null && !foto4.isEmpty()) {
                produto.setFoto4(foto4.getBytes());
            } else {
                produto.setFoto4(null); // Remove a imagem se foto4 for null
            }
            if (foto5 != null && !foto5.isEmpty()) {
                produto.setFoto5(foto5.getBytes());
            } else {
                produto.setFoto5(null); // Remove a imagem se foto5 for null
            }
            save(produto);
        } catch (IOException e) {
            // Trate a exceção adequadamente, por exemplo, registrando o erro
            e.printStackTrace(); 
        }
    }

	public Produto saveNew(MultipartFile foto1, MultipartFile foto2, MultipartFile foto3, MultipartFile foto4,
			MultipartFile foto5, Produto produto) {

		try {
			if (foto1 != null && !foto1.isEmpty()) {
				produto.setFoto1(foto1.getBytes());
			}
			if (foto2 != null && !foto2.isEmpty()) {
				produto.setFoto2(foto2.getBytes());
			}
			if (foto3 != null && !foto3.isEmpty()) {
				produto.setFoto3(foto3.getBytes());
			}
			if (foto4 != null && !foto4.isEmpty()) {
				produto.setFoto4(foto4.getBytes());
			}
			if (foto5 != null && !foto5.isEmpty()) {
				produto.setFoto5(foto5.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		produto.setStatusProd("ATIVO");
		return produtoRepository.save(produto);
	}

	public List<Produto> findAll() {
		List<Produto> produtos = produtoRepository.findAll();
		return produtos;
	}

	public Optional<Produto> findById(Long id) {
		return produtoRepository.findById(id);
	}
	
	public List<Produto> findAllProdutos() { 
	    return produtoRepository.findAll();
	}

	@Transactional
	public Produto inativar(long id) {
		Optional<Produto> _produto = produtoRepository.findById(id);

		if (_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();
			produtoAtualizado.setStatusProd("INATIVO");

			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}

	@Transactional
	public Produto alterar(long id, Produto produto) {
		Optional<Produto> _produto = produtoRepository.findById(id);

		if (_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();

			produtoAtualizado.setPreco(produto.getPreco());

			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}

	@Transactional
	public Produto reativar(long id) {
		Optional<Produto> _produto = produtoRepository.findById(id);

		if (_produto.isPresent()) {
			Produto produtoAtualizado = _produto.get();
			produtoAtualizado.setStatusProd("ATIVO");

			return produtoRepository.save(produtoAtualizado);
		}
		return null;
	}

	public List<Produto> getProdutosEmDestaque() {
		return produtoRepository.findRandom10ProductsFromCategories();
	}

	public List<Produto> getProdutosRecentes() {
		return produtoRepository.findTop10RecentProducts();
	}

	public List<Produto> buscarProdutosPorPalavraChave(String palavraChave) {
		return produtoRepository.findByNomeContainingIgnoreCaseOrDescricaoContainingIgnoreCase(palavraChave,
				palavraChave);
	}
}