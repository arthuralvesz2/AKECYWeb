package br.itb.projeto.AKECY.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.itb.projeto.AKECY.model.entity.Mensagem;
import br.itb.projeto.AKECY.model.repository.MensagemRepository;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository mensagemRepository;

    public void salvarMensagem(Mensagem mensagem) {
        mensagemRepository.save(mensagem);
    }
}