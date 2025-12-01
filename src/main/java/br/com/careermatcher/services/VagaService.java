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
public class VagaService {

    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;
    private RankerService rankerService;

    public List<Vaga> findAll(){
        return vagaRepository.findAll();
    }


    public void createRankedListCandidatosTodasAsVagas(){
        for(Vaga vaga: vagaRepository.findAll()){
            createRankedListCandidatos(vaga);
        }
        System.out.println("Vagas salva com sucesso!");
    }


    private void createRankedListCandidatos(Vaga vaga){
        /*
            Função que cria uma lista de preferências de Candidatos para uma Vaga
         */

        Map<Candidato, Double> rankedMapCandidatos = new HashMap<>();
        List<Candidato> rankedListCandidatos = new ArrayList<>();
        double rank;

        for (Candidato candidato : candidatoRepository.findAll()) {
            if(!candidato.getCargo().equals(vaga.getCargo())){
                rankedMapCandidatos.put(candidato, 0.);
            }else{
                rank = 0;

                rank += rankerService.senioridadeRanker(vaga, candidato)
                         +rankerService.localidadeXmodalidadeRanker(vaga, candidato)
                        + rankerService.graduacaoRanker(vaga, candidato)
                        + rankerService.experienciaRanker(vaga, candidato)
                        + rankerService.competenciasRanker(vaga, candidato)
                        + rankerService.mestradoRanker(vaga, candidato)
                        + rankerService.doutoradoRanker(vaga, candidato)
                        + rankerService.posDoutoradoRanker(vaga, candidato);

                        rankedMapCandidatos.put(candidato, rank);

                }
            }

        rankedListCandidatos = orderByRank(rankedMapCandidatos);
        vaga.setListaPreferenciaCandidatos(rankedListCandidatos);

        System.out.println("SUCESSO!"+vaga.getListaPreferenciaCandidatos());
        System.out.println("NT");
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
