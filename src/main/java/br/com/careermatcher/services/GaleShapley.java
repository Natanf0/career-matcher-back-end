package br.com.careermatcher.services;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class GaleShapley {

    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;
    private CandidatoService candidatoService;
    private VagaService vagaService;

    @Transactional
    public void galeShapleyAlgorithm(List<Candidato> todosOsCandidatos, List<Vaga> todasAsVagas) {
        // Inicializa todos os candidatos e vagas como livres
        for(Candidato candidato: todosOsCandidatos) {
            candidatoRepository.upsertCandidatoComId(candidato.getNome(), candidato.getCargo(), candidato.getPaís(), candidato.getCidade(), candidato.getTelefone(), candidato.getEmail(), candidato.getSenioridade().name(), candidato.getId());
            candidato.setIdVagaEscolhida(-1L);
        }
        for(Vaga vaga: todasAsVagas) {
            vagaRepository.upsertVagaComId(vaga.getCargo(), vaga.getCidade(), vaga.getEmpresa(), vaga.getSenioridade().name(), vaga.getModalidade().name(), vaga.getId() );
            vaga.setIdCandidatoEscolhido(-1L);
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

                if(vaga.getIdCandidatoEscolhido() == -1L) {
                    // Vaga está livre - faz o casamento
                    realizarCasamento(candidato, vaga);
                } else {
                    // Vaga já está ocupada - verifica se prefere este candidato
                    Optional<Candidato> candidatoAtual = candidatoRepository.findById(vaga.getIdCandidatoEscolhido());
                    if(vagaPrefereCandidato(vaga, candidato, candidatoAtual.get())) {
                        // Vaga prefere o novo candidato - troca o casamento
                        desfazerCasamento(candidatoAtual.get());
                        candidatosLivres.add(candidatoAtual.get());
                        realizarCasamento(candidato, vaga);
                    } else {
                        // Vaga rejeita o candidato - ele continua livre
                        candidatosLivres.add(candidato);
                    }
                }
            }
        }
        for(Candidato candidato : todosOsCandidatos) {
            System.out.println(candidato.getCargo() + " -- " + vagaRepository.findById(candidato.getIdVagaEscolhida()).orElseThrow().getCargo());
        }
    }

    private void realizarCasamento(Candidato candidato, Vaga vaga) {
        candidato.setIdVagaEscolhida(vaga.getId());
        vaga.setIdCandidatoEscolhido(candidato.getId());
        candidatoRepository.atualizarVagaEscolhida(candidato.getId(), vaga.getId());
        vagaRepository.atualizarCandidatoEscolhido(vaga.getId(), candidato.getId());
    }

    private void desfazerCasamento(Candidato candidato) {
        if(candidato.getIdVagaEscolhida() != null) {
            vagaRepository.findById(candidato.getIdVagaEscolhida()).orElseThrow().setIdCandidatoEscolhido(-1L);
            candidato.setIdVagaEscolhida(-1L);
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
                .filter(v -> v.getIdCandidatoEscolhido() == -1)
                .count();
        return qtdeVagasSemCandidato > 0;
    }
}