package br.com.careermatcher.services;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.repositories.CandidatoRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CandidatoService {
    private final CandidatoRepository candidatoRepository;
    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    public List<Candidato> findAll(){
        return candidatoRepository.findAll(); // apenas para testar
    }
}
