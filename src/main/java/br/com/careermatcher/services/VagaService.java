package br.com.careermatcher.services;

import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    public List<Vaga> findAll(){
        return vagaRepository.findAll();
    }
}
