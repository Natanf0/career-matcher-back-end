package br.com.careermatcher.services;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class CandidatoService {
    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;
    private final RankerService rankerService;

    public CandidatoService(CandidatoRepository candidatoRepository, VagaRepository vagaRepository, RankerService rankerService) {
        this.candidatoRepository = candidatoRepository;
        this.vagaRepository = vagaRepository;
        this.rankerService = rankerService;
    }

    public List<Candidato> findAllmodificado(){
        return candidatoRepository.findAllWithRelations(); // apenas para testar
    }

    public void createRankedListVagasTodosOsCandidatos(){
        for(Candidato candidato : candidatoRepository.findAll()){
            createRankedListVagas(candidato);
            System.out.println("SUCESSO NTNTNTNTTTTTTTT");
        }
    }

    private void createRankedListVagas(Candidato candidato){
        Map<Vaga, Double> rankedMapVagas = new HashMap<>();
        List<Vaga> rankedListVagas = new ArrayList<>();
        double rank;

        for(Vaga vaga: vagaRepository.findAll()){
            if(!vaga.getCargo().equals(candidato.getCargo())) {
                rankedMapVagas.put(vaga,0.) ;
            }else{
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
