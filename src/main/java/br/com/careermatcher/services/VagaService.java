package br.com.careermatcher.services;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;
    private RankerService rankerService;

    public List<Vaga> findAll(){
        return vagaRepository.findAllWithRelationships();
    }


    public HashMap<Integer, List<Candidato>> createRankedListVagas(){
        /*
            Função que cria uma lista de preferências de Candidatos para uma Vaga
         */
        List<Vaga> vagas = vagaRepository.findAll();
        List<Candidato> candidatos = candidatoRepository.findAll();

        HashMap<Integer, List<Candidato>> rankedListVagasMap = new HashMap<>();
        List<Candidato> candidatosVagas = new ArrayList<>();

        for(Vaga vaga : vagas){
            for(Candidato candidatoCargoDiferente : candidatos.stream().filter(x -> ! (x.getCargo().equalsIgnoreCase(vaga.getCargo()))).toList()) {
                candidatosVagas.add(candidatoCargoDiferente);
                rankedListVagasMap.put(0, candidatosVagas);
            }

            for(Candidato candidatoCargoDaVaga : candidatos.stream().filter(x -> x.getCargo().equalsIgnoreCase(vaga.getCargo())).toList()) {
                int rank = 0;

                rank   += rankerService.senioridadeRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.localidadeXmodalidadeRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.graduacaoRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.experienciaRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.competenciasRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.mestradoRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.doutoradoRanker(vaga, candidatoCargoDaVaga)
                        + rankerService.posDoutoradoRanker(vaga, candidatoCargoDaVaga);

                candidatosVagas.add(candidatoCargoDaVaga);
                rankedListVagasMap.put(rank, candidatosVagas);
            }
        }
        return rankedListVagasMap;
    }


}
