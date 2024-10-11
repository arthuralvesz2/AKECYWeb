package br.itb.projeto.AKECY.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.FavoritoRepository;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    public List<Favorito> findByUsuario(Usuario usuario) {
        return favoritoRepository.findByUsuario(usuario);
    }
}