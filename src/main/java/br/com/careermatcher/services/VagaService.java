package br.com.careermatcher.services;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;

    public List<Vaga> findAll(){
        return vagaRepository.findAll();
    }


    public List<Candidato> createRankedListVagas(){
        /*
            Função que cria uma lista de preferências de Candidatos para uma Vaga
         */
        List<Candidato> rankedListVagas = new ArrayList<>();
        List<Vaga> vagas = vagaRepository.findAll();
        List<Candidato> candidatos = candidatoRepository.findAll();
        // em andamento
        return rankedListVagas;
    }


}
