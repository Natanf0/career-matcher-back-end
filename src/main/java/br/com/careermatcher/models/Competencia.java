package br.com.careermatcher.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("COMPETENCIA")
@AllArgsConstructor
@Getter
public class Competencia {
    @Id @GeneratedValue
    private Long id;

    private String nome;
}
