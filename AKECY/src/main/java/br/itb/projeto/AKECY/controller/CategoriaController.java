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
import br.itb.projeto.AKECY.model.entity.Categoria;
import br.itb.projeto.AKECY.rest.exception.ResourceNotFoundException;
import br.itb.projeto.AKECY.rest.response.MessageResponse;
import br.itb.projeto.AKECY.service.CategoriaService;

@RestController
@RequestMapping("/categoria/")
public class CategoriaController {

	private CategoriaService categoriaService;

	public CategoriaController(CategoriaService categoriaService) {
		super();
		this.categoriaService = categoriaService;
	}

	@GetMapping("findAll")
	public ResponseEntity<List<Categoria>> findAll() {
		List<Categoria> categorias = categoriaService.findAll();

		return new ResponseEntity<List<Categoria>>(categorias, HttpStatus.OK);
	}
	
	@GetMapping("findById/{id}")
	public ResponseEntity<Categoria> findById(@PathVariable long id){
		
		Categoria categoria = categoriaService.findById(id);
		
		if(categoria != null) { 
			return new ResponseEntity<Categoria>(categoria, HttpStatus.OK);
		} else {
			throw new ResourceNotFoundException("*** Categoria não encontrada! *** " + "ID: " + id);
		}
	}
	
	@PostMapping("create")
	public ResponseEntity<?> create(@RequestBody Categoria categoria){
		
		Categoria _categoria = categoriaService.create (categoria);
		
		if (_categoria == null) {
			
			return ResponseEntity.badRequest().body(
					  new MessageResponse("Categoria já Cadastrada!"));		
		}
		return ResponseEntity.ok()
				.body(new MessageResponse ("Categoria Cadastrada com sucesso!"));
	}
	
	
}
