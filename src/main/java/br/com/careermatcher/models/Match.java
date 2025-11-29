package br.com.careermatcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Match {
    private Vaga vaga;
    private Candidato candidato;
    private int scoreVaga;
    private int scoreCandidato;
    
    @Override
    public String toString() {
        return String.format("Match{Vaga: %s (empresa: %s), Candidato: %s, ScoreVaga: %d, ScoreCandidato: %d}",
                vaga.getCargo(), vaga.getEmpresa(), candidato.getNome(), scoreVaga, scoreCandidato);
    }
}
