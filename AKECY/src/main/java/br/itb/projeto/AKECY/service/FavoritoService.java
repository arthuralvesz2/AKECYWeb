package br.itb.projeto.AKECY.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.repository.FavoritoRepository;
import jakarta.transaction.Transactional;

@Service
public class FavoritoService {
	
	private FavoritoRepository favoritoRepository;
	
	public FavoritoService(FavoritoRepository favoritoRepository) {
		super();
			this.favoritoRepository = favoritoRepository;
	}
	
	public List<Favorito> findAll(){
		List<Favorito> favoritos = favoritoRepository.findAll();
		return favoritos;
	}
	
	@Transactional
	public Favorito create(Favorito favorito) {
		
		return favoritoRepository.save(favorito);
	}

}
