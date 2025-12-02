package br.com.careermatcher.services;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class GaleShapley {

    private CandidatoService candidatoService;
    private VagaService vagaService;

    public void galeShapleyAlgorithm(List<Candidato> todosOsCandidatos, List<Vaga> todasAsVagas) {
        // Inicializa todos os candidatos e vagas como livres
        for(Candidato candidato: todosOsCandidatos) {
            candidato.setVagaEscolhida(null);
        }
        for(Vaga vaga: todasAsVagas) {
            vaga.setCandidatoEscolhido(null);
        }

        // Computa as listas de preferencias:
        vagaService.createRankedListCandidatosTodasAsVagas(todasAsVagas,  todosOsCandidatos);
        candidatoService.createRankedListVagasTodosOsCandidatos(todosOsCandidatos, todasAsVagas);

        // Fila de candidatos livres
        Queue<Candidato> candidatosLivres = new LinkedList<>(todosOsCandidatos);

        // Mapa para controlar o próximo índice de proposta de cada candidato
        Map<Candidato, Integer> indiceProposta = new HashMap<>();
        for(Candidato candidato : todosOsCandidatos) {
            indiceProposta.put(candidato, 0);
        }

        // Algoritmo Gale-Shapley
        while(!candidatosLivres.isEmpty() && existeVagaSemCandidato(todasAsVagas)) {
            Candidato candidato = candidatosLivres.poll();
            int proximoIndice = indiceProposta.get(candidato);

            // Se o candidato ainda tem vagas para propor
            if(proximoIndice < candidato.getListaPreferenciaVagas().size()) {
                Vaga vaga = candidato.getListaPreferenciaVagas().get(proximoIndice);
                indiceProposta.put(candidato, proximoIndice + 1);

                if(vaga.getCandidatoEscolhido() == null) {
                    // Vaga está livre - faz o casamento
                    realizarCasamento(candidato, vaga);
                } else {
                    // Vaga já está ocupada - verifica se prefere este candidato
                    Candidato candidatoAtual = vaga.getCandidatoEscolhido();
                    if(vagaPrefereCandidato(vaga, candidato, candidatoAtual)) {
                        // Vaga prefere o novo candidato - troca o casamento
                        desfazerCasamento(candidatoAtual);
                        candidatosLivres.add(candidatoAtual);
                        realizarCasamento(candidato, vaga);
                    } else {
                        // Vaga rejeita o candidato - ele continua livre
                        candidatosLivres.add(candidato);
                    }
                }
            }
        }
    }

    private void realizarCasamento(Candidato candidato, Vaga vaga) {
        candidato.setVagaEscolhida(vaga);
        vaga.setCandidatoEscolhido(candidato);
    }

    private void desfazerCasamento(Candidato candidato) {
        if(candidato.getVagaEscolhida() != null) {
            candidato.getVagaEscolhida().setCandidatoEscolhido(null);
            candidato.setVagaEscolhida(null);
        }
    }

    private boolean vagaPrefereCandidato(Vaga vaga, Candidato novoCandidato, Candidato candidatoAtual) {
        List<Candidato> preferencias = vaga.getListaPreferenciaCandidatos();
        int indiceNovo = preferencias.indexOf(novoCandidato);
        int indiceAtual = preferencias.indexOf(candidatoAtual);

        // Quanto menor o índice, maior a preferência (lista ordenada decrescentemente)
        return indiceNovo < indiceAtual;
    }

    private boolean existeVagaSemCandidato(List<Vaga> vagas) {
        long qtdeVagasSemCandidato = vagas
                .stream()
                .filter(v -> v.getCandidatoEscolhido() == null)
                .count();
        return qtdeVagasSemCandidato > 0;
    }
}