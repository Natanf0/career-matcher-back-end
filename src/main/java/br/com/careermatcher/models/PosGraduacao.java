package br.com.careermatcher.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

@AllArgsConstructor
@Getter
public abstract class PosGraduacao {
    @Id
    @GeneratedValue
    private Long id;

    private String curso, instituicao, subarea;
    private Integer ano_conclusao;
}
