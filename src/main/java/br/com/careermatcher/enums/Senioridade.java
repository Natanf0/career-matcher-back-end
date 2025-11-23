package br.com.careermatcher.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
@AllArgsConstructor
public enum Senioridade {
    Estágio(0),
    Trainee(1),
    Júnior(1),
    Pleno(2),
    Sênior(3),
    Gestor(4),
    Gerente(5),
    Arquiteto(6),
    Especialista(7);
    // verificar mais opções e ajustar o banco de dadps

    @Getter
    private int valor;
}
