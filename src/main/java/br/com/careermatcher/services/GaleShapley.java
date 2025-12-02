package br.com.careermatcher.services;

import br.com.careermatcher.enums.Modalidade;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class GaleShapley {

    private CandidatoRepository candidatoRepository;
    private VagaRepository vagaRepository;

    public void galeShapleyAlgorithm() {
        // Inicializa todos os candidatos e vagas como livres
        for(Candidato candidato: candidatoRepository.findAll()) {
            candidato.setVagaEscolhida(null);
        }
        for(Vaga vaga: vagaRepository.findAll()) {
            vaga.setCandidatoEscolhido(null);
        }

        // Fila de candidatos livres
        Queue<Candidato> candidatosLivres = new LinkedList<>(candidatoRepository.findAll());

        // Mapa para controlar o próximo índice de proposta de cada candidato
        Map<Candidato, Integer> indiceProposta = new HashMap<>();
        for(Candidato candidato : candidatoRepository.findAll()) {
            indiceProposta.put(candidato, 0);
        }

        // Algoritmo Gale-Shapley
        while(!candidatosLivres.isEmpty() && existeVagaSemCandidato()) {
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

    private boolean existeVagaSemCandidato() {
        long qtdeVagasSemCandidato = vagaRepository.findAll()
                .stream()
                .filter(v -> v.getCandidatoEscolhido() == null)
                .count();
        return qtdeVagasSemCandidato > 0;
    }

    // Método auxiliar para verificar o matching resultante
    public void mostrarResultado() {
        System.out.println("=== RESULTADO DO MATCHING GALE-SHAPLEY ===");
        for(Vaga vaga : vagaRepository.findAll()) {
            if(vaga.getCandidatoEscolhido() != null) {
                System.out.println("Vaga: " + vaga.getId() + " → Candidato: " + vaga.getCandidatoEscolhido().getId());
            } else {
                System.out.println("Vaga: " + vaga.getId() + " → SEM CANDIDATO");
            }
        }

        System.out.println("\nCandidatos sem vaga:");
        for(Candidato cand : candidatoRepository.findAll()) {
            if(cand.getVagaEscolhida() == null) {
                System.out.println("Candidato: " + cand.getId() + " → SEM VAGA");
            }
        }
    }
}