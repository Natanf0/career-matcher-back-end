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
import java.util.Map;


@Service
@AllArgsConstructor
public class CandidatoService {
    private final CandidatoRepository candidatoRepository;
    private final RankerService rankerService;

    public List<Candidato> findAll(){return candidatoRepository.findAll();}

    public void createRankedListVagasTodosOsCandidatos(List<Candidato> todosOsCandidatos, List<Vaga> todasAsVagas){
        for(Candidato candidato : todosOsCandidatos ){
            createRankedListVagas(candidato, todasAsVagas);
        }
    }

    private void createRankedListVagas(Candidato candidato, List<Vaga> todasAsVagas){
        Map<Vaga, Double> rankedMapVagas = new HashMap<>();
        List<Vaga> rankedListVagas = new ArrayList<>();
        double rank;

        for(Vaga vaga: todasAsVagas ){
                rank = 0.;
                rank+= rankerService.senioridadeRanker(vaga, candidato)
                        + rankerService.modalidadeRanker(vaga, candidato)
                        + rankerService.experienciaRanker(vaga, candidato)
                        + rankerService.competenciasRanker(vaga, candidato)
                        + rankerService.mestradoRanker(vaga, candidato)
                        + rankerService.graduacaoRanker(vaga, candidato)
                        + rankerService.doutoradoRanker(vaga, candidato)
                        + rankerService.posDoutoradoRanker(vaga, candidato);

                rankedMapVagas.put(vaga,rank);
            }

            rankedListVagas = orderByRank(rankedMapVagas);
            candidato.setListaPreferenciaVagas(rankedListVagas);
    }

    private List<Vaga> orderByRank(Map<Vaga, Double> rankedMapCandidatos){
        // Ordenação decrescente pelo valor Integer
        return rankedMapCandidatos.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())) // Decrescente
                .map(entry -> entry.getKey())
                .toList();
    }
}
