package br.com.careermatcher.services;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Match;
import br.com.careermatcher.models.PreferenceList;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StableMarriageService {
    
    private final VagaRepository vagaRepository;
    private final CandidatoRepository candidatoRepository;
    private final RankerService rankerService;
    
    private List<PreferenceList<Candidato>> criarListaPreferenciasVaga(Vaga vaga, List<Candidato> todosCandidatos) {
        List<Candidato> candidatosFiltrados = todosCandidatos.stream()
                .filter(c -> c.getCargo().equalsIgnoreCase(vaga.getCargo()))
                .collect(Collectors.toList());
        
        List<PreferenceList<Candidato>> preferencias = new ArrayList<>();
        for (Candidato candidato : candidatosFiltrados) {
            int score = rankerService.calcularScoreTotal(vaga, candidato);
            preferencias.add(new PreferenceList<>(candidato, score));
        }
        
        Collections.sort(preferencias);
        
        return preferencias;
    }
    
    private List<PreferenceList<Vaga>> criarListaPreferenciasCandidato(Candidato candidato, List<Vaga> todasVagas) {
        List<Vaga> vagasFiltradas = todasVagas.stream()
                .filter(v -> v.getCargo().equalsIgnoreCase(candidato.getCargo()))
                .collect(Collectors.toList());
        
        List<PreferenceList<Vaga>> preferencias = new ArrayList<>();
        for (Vaga vaga : vagasFiltradas) {
            int score = rankerService.calcularScoreTotal(vaga, candidato);
            preferencias.add(new PreferenceList<>(vaga, score));
        }
        
        Collections.sort(preferencias);
        
        return preferencias;
    }
    
    public List<Match> executarCasamentoEstavel() {
        List<Vaga> todasVagas = vagaRepository.findAll();
        List<Candidato> todosCandidatos = candidatoRepository.findAll();
        
        Map<Long, List<PreferenceList<Candidato>>> preferenciasVagas = new HashMap<>();
        for (Vaga vaga : todasVagas) {
            preferenciasVagas.put(vaga.getId(), criarListaPreferenciasVaga(vaga, todosCandidatos));
        }
        
        Map<Long, List<PreferenceList<Vaga>>> preferenciasCandidatos = new HashMap<>();
        for (Candidato candidato : todosCandidatos) {
            preferenciasCandidatos.put(candidato.getId(), criarListaPreferenciasCandidato(candidato, todasVagas));
        }
        
        Map<Long, Candidato> matchVagaToCandidato = new HashMap<>();
        Map<Long, Vaga> matchCandidatoToVaga = new HashMap<>();
        Map<Long, Integer> proximaProposta = new HashMap<>();
        Queue<Candidato> candidatosLivres = new LinkedList<>(todosCandidatos);
        
        Map<Long, Vaga> vagasById = new HashMap<>();
        for (Vaga vaga : todasVagas) {
            vagasById.put(vaga.getId(), vaga);
        }
        
        for (Candidato candidato : todosCandidatos) {
            proximaProposta.put(candidato.getId(), 0);
        }
        
        while (!candidatosLivres.isEmpty()) {
            Candidato candidato = candidatosLivres.poll();
            List<PreferenceList<Vaga>> listaPrefs = preferenciasCandidatos.get(candidato.getId());
            int indice = proximaProposta.get(candidato.getId());
            
            // Se o candidato já propôs para todas as vagas possíveis, pula
            if (indice >= listaPrefs.size()) {
                continue;
            }
            
            // Pega a próxima vaga na lista de preferências
            PreferenceList<Vaga> prefAtual = listaPrefs.get(indice);
            Vaga vaga = prefAtual.getItem();

            proximaProposta.put(candidato.getId(), indice + 1);
            
            if (!matchVagaToCandidato.containsKey(vaga.getId())) {
                // Vaga está livre, aceita a proposta
                matchVagaToCandidato.put(vaga.getId(), candidato);
                matchCandidatoToVaga.put(candidato.getId(), vaga);
            } else {
                // Vaga já está emparelhada, precisa decidir
                Candidato candidatoAtual = matchVagaToCandidato.get(vaga.getId());
                
                int scoreCandidatoAtual = encontrarScore(preferenciasVagas.get(vaga.getId()), candidatoAtual);
                int scoreNovoCandidato = encontrarScore(preferenciasVagas.get(vaga.getId()), candidato);
                
                if (scoreNovoCandidato > scoreCandidatoAtual) {
                    // Vaga prefere o novo candidato
                    matchCandidatoToVaga.remove(candidatoAtual.getId());
                    candidatosLivres.add(candidatoAtual);
                    
                    matchVagaToCandidato.put(vaga.getId(), candidato);
                    matchCandidatoToVaga.put(candidato.getId(), vaga);
                } else {
                    // Vaga rejeita o novo candidato
                    candidatosLivres.add(candidato);
                }
            }
        }
        
        // Lista de matches finais
        List<Match> matches = new ArrayList<>();
        for (Map.Entry<Long, Candidato> entry : matchVagaToCandidato.entrySet()) {
            Long vagaId = entry.getKey();
            Candidato candidato = entry.getValue();
            Vaga vaga = vagasById.get(vagaId);
            
            if (vaga != null && candidato != null) {
                int scoreVaga = encontrarScore(preferenciasVagas.get(vaga.getId()), candidato);
                int scoreCandidato = encontrarScore(preferenciasCandidatos.get(candidato.getId()), vaga);
                
                matches.add(new Match(vaga, candidato, scoreVaga, scoreCandidato));
            }
        }
        
        return matches;
    }
    
    private <T> int encontrarScore(List<PreferenceList<T>> preferencias, T item) {
        for (PreferenceList<T> pref : preferencias) {
            if (pref.getItem().equals(item)) {
                return pref.getScore();
            }
        }
        return Integer.MIN_VALUE; // Não encontrado
    }
}
