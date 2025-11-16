package br.com.careermatcher.enums;

import lombok.Getter;

public enum Senioridade {
    Júnior(0),
    Pleno(0),
    Sênior(0),
    PESQUISAROR(0),
    RESEARCHER(0),
    ESTAGIO(1),
    INTERN(1),
    TRAINEE(2),
    JUNIOR(3),
    PLENO(4),
    MIDLEVEL(4),
    SENIOR(5),
    SÊNIOR(5),
    GERENTE(6),
    GESTOR(6),
    TECHLEAD(7),
    ARQUITETO(8),
    CTO(9);

    // verificar mais opções
    @Getter
    private int valor;
    Senioridade(int valor) {
        this.valor = valor;
    }
}
