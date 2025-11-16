package br.com.careermatcher.models;

import br.com.careermatcher.enums.Senioridade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("EXPERIENCIA")
@AllArgsConstructor
@Getter
public class Experiencia {
    @Id @GeneratedValue
    private Long id;

    private String cargo;
    private Float duracao_meses;
    private Senioridade senioridade;
}
