package br.itb.projeto.AKECY.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.model.repository.CategoriaRepository;
import jakarta.transaction.Transactional;

@Service
public class CategoriaService {

	private CategoriaRepository categoriaRepository;

	public CategoriaService(CategoriaRepository categoriaRepository) {
		super();
		this.categoriaRepository = categoriaRepository;
	}
	
	public List<Categoria> findAll(){
		List<Categoria> categorias = categoriaRepository.findAll();
		return categorias;
	}
	
	@Transactional
	public Categoria create(Categoria categoria) {
		
		return categoriaRepository.save(categoria);
	}
	
	public Categoria findById(long id) {
		
		Optional<Categoria> categoria = categoriaRepository.findById(id);
		
		if(categoria.isPresent()) {
			return categoria.get();
		}
	   
		return null;	
	}
	
}
