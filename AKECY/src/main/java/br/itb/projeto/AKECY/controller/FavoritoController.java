package br.itb.projeto.AKECY.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.itb.projeto.AKECY.model.entity.Favorito;
import br.itb.projeto.AKECY.model.entity.Usuario;
import br.itb.projeto.AKECY.model.repository.FavoritoRepository;
import br.itb.projeto.AKECY.service.ProdutoService;
import br.itb.projeto.AKECY.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/AKECY/favorito/")
public class FavoritoController {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ProdutoService produtoService;

    @PostMapping("/alterar")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> alterarFavorito(
            @RequestParam Long idProduto, 
            HttpSession session) {

        String loggedInUserEmail = (String) session.getAttribute("loggedInUserEmail");

        if (loggedInUserEmail == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não logado");
        }

        Usuario usuario = usuarioService.findByEmail(loggedInUserEmail);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado");
        }

        if (favoritoRepository.existsByUsuarioIdUsuarioAndProdutoIdProduto(usuario.getIdUsuario(), idProduto)) {
            favoritoRepository.deleteByUsuarioIdUsuarioAndProdutoIdProduto(usuario.getIdUsuario(), idProduto);
            return ResponseEntity.ok("Produto removido dos favoritos");
        } else {
            Favorito favorito = new Favorito();
            favorito.setUsuario(usuario);
            favorito.setProduto(produtoService.findById(idProduto).get());
            favoritoRepository.save(favorito);
            return ResponseEntity.ok("Produto adicionado aos favoritos");
        }
    }
}