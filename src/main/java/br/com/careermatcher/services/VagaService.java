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

    public Vaga findById(Long id) {
        return vagaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada com ID: " + id));
    }

    public List<Vaga> findWithFilters(List<String> senioridade, List<String> modalidade, List<String> cargo, String empresa, String cidade) {
        boolean hasSenioridade = senioridade != null && !senioridade.isEmpty();
        boolean hasModalidade = modalidade != null && !modalidade.isEmpty();
        boolean hasCargo = cargo != null && !cargo.isEmpty();
        boolean hasEmpresa = empresa != null && !empresa.trim().isEmpty();
        boolean hasCidade = cidade != null && !cidade.trim().isEmpty();

        List<Vaga> vagas;
        if (!hasSenioridade && !hasModalidade && !hasCargo && !hasEmpresa && !hasCidade) {
            vagas = vagaRepository.findAll();
        } else {
            List<Long> ids = vagaRepository.findIdsByFilters(
                hasSenioridade ? senioridade : null,
                hasModalidade ? modalidade : null,
                hasCargo ? cargo : null,
                hasEmpresa ? empresa : null,
                hasCidade ? cidade : null
            );
            vagas = vagaRepository.findAllById(ids).stream().toList();
        }


        return vagas;
    }


    public void createRankedListCandidatosTodasAsVagas(List<Vaga> todasAsVagas, List<Candidato> todosOsCandidatos){
        for(Vaga vaga: todasAsVagas){
            createRankedListCandidatos(vaga, todosOsCandidatos);
        }
    }


    private void createRankedListCandidatos(Vaga vaga, List<Candidato> todosOsCandidatos){
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
        return rankedMapCandidatos.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .map(entry -> entry.getKey())
                .toList();
    }
}
