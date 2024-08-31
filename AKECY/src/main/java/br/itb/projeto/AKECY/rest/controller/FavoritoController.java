package br.itb.projeto.AKECY.rest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.entity.Produto;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.service.FavoritoService;

@RestController
@RequestMapping("/favorito/")
public class FavoritoController {
	
	private FavoritoService favoritoService;
	
	public FavoritoController(FavoritoService favoritoService) {
		super();
		this.favoritoService = favoritoService;
	}
	
	@GetMapping("findAll")
	public ResponseEntity<List<Favorito>> findAll() {
		List<Favorito> favoritos = favoritoService.findAll();
		
		return new ResponseEntity<List<Favorito>>(favoritos, HttpStatus.OK);
	}
   }


