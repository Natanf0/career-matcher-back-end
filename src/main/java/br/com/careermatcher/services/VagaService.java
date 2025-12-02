package br.com.careermatcher.services;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class VagaService {

    private final VagaRepository vagaRepository;
    private RankerService rankerService;

    public List<Vaga> findAll(){
        return vagaRepository.findAll();
    }

    public void createRankedListCandidatosTodasAsVagas(List<Vaga> todasAsVagas, List<Candidato> todosOsCandidatos){
        for(Vaga vaga: todasAsVagas){
            createRankedListCandidatos(vaga, todosOsCandidatos);
        }
    }


    private void createRankedListCandidatos(Vaga vaga, List<Candidato> todosOsCandidatos){
        /*
            Função que cria uma lista de preferências de Candidatos para uma Vaga
         */
        Map<Candidato, Double> rankedMapCandidatos = new HashMap<>();
        List<Candidato> rankedListCandidatos = new ArrayList<>();
        double rank;

        for (Candidato candidato : todosOsCandidatos) {
                rank = 0;

                rank += rankerService.senioridadeRanker(vaga, candidato)
                        + rankerService.localidadeXmodalidadeRanker(vaga, candidato)
                        + rankerService.graduacaoRanker(vaga, candidato)
                        + rankerService.experienciaRanker(vaga, candidato)
                        + rankerService.competenciasRanker(vaga, candidato)
                        + rankerService.mestradoRanker(vaga, candidato)
                        + rankerService.doutoradoRanker(vaga, candidato)
                        + rankerService.posDoutoradoRanker(vaga, candidato);

                        rankedMapCandidatos.put(candidato, rank);
                }

        rankedListCandidatos = orderByRank(rankedMapCandidatos);
        vaga.setListaPreferenciaCandidatos(rankedListCandidatos);
    }

    private List<Candidato> orderByRank(Map<Candidato, Double> rankedMapCandidatos){
        // Ordenação decrescente pelo valor Integer
        return rankedMapCandidatos.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Decrescente
                .map(entry -> entry.getKey())
                .toList();
    }
}
