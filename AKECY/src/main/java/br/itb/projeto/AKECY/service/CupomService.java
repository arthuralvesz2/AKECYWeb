package br.itb.projeto.AKECY.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Cupom;
import br.itb.projeto.AKECY.model.repository.CupomRepository;
import jakarta.transaction.Transactional;

@Service
public class CupomService {
	
	private CupomRepository cupomRepository;
	
	public CupomService(CupomRepository cupomRepository) {
		super();
		this.cupomRepository = cupomRepository;
	}
	
	public List<Cupom> findAll(){
		List<Cupom> cupoms = cupomRepository.findAll();
		return cupoms;
	}
	
	@Transactional
	public Cupom create(Cupom cupom) {
		
		return cupomRepository.save(cupom);
	}
	
	public Cupom findById(long id) {
		
		Optional<Cupom> cupom = cupomRepository.findById(id);
		
		if(cupom.isPresent()) {
			return cupom.get();
		}
		
		return null;
	}

}
