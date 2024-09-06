package br.itb.projeto.AKECY.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.itb.projeto.AKECY.model.entity.Cupom;
import br.itb.projeto.AKECY.rest.exception.ResourceNotFoundException;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
import br.itb.projeto.AKECY.service.CupomService;

@RestController
@RequestMapping("/cupom/")
public class CupomController {
	
	private CupomService cupomService;
	
	public CupomController(CupomService cupomService) {
		super();
		this.cupomService = cupomService;
	}

	@GetMapping("findAll")
	public ResponseEntity<List<Cupom>> findAll() {
		List<Cupom> cupoms = cupomService.findAll();
		
		return new ResponseEntity<List<Cupom>>(cupoms, HttpStatus.OK);
	}
	
	@GetMapping("findById/{id}")
	public ResponseEntity<Cupom> findById(@PathVariable long id){
		
		Cupom cupom = cupomService.findById(id);
		
		if(cupom != null) {
			return new ResponseEntity<Cupom>(cupom, HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException("*** Cupom não encontrado! ***" + "ID>:" + id); 
		}
	}
	
	@PostMapping("create")
	public ResponseEntity<?> create(@RequestBody Cupom cupom){
		
		Cupom _cupom = cupomService.create (cupom);
		
		if (_cupom == null) { 
			
			return ResponseEntity.badRequest().body(
					new MessageResponse("Cupom já cadastrado!"));
		}
		return ResponseEntity.ok()
				.body(new MessageResponse ("Cupom cadastrado com sucesso!"));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
