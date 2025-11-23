package br.com.careermatcher.services;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;
    private RankerService rankerService;

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

        for(Vaga vaga : vagas){
            List<Candidato> candidatoesFiltradosCargo = candidatos.stream().filter(x -> x.getCargo().equalsIgnoreCase(vaga.getCargo())).toList(); // Filtra os candidatos que possuem o mesmo carga da vaga

            for(Candidato candidato : candidatoesFiltradosCargo) {
                int rank = 0;

                rank   += rankerService.senioridadeRanker(vaga, candidato)
                        + rankerService.localidadeXmodalidadeRanker(vaga, candidato)
                        + rankerService.graduacaoRanker(vaga, candidato)
                        + rankerService.experienciaRanker(vaga, candidato)
                        + rankerService.competenciasRanker(vaga, candidato)
                        + rankerService.mestradoRanker(vaga, candidato)
                        + rankerService.doutoradoRanker(vaga, candidato)
                        + rankerService.posDoutoradoRanker(vaga, candidato);
            }
        }
        return rankedListVagas;
    }


}
