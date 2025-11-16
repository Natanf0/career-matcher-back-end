package br.com.careermatcher.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("POSDOUTORADO")
@AllArgsConstructor
@Getter
public class PosDoutorado {
    @Id @GeneratedValue
    private Long id;

    private String curso, instituicao, subarea;
    private Integer ano_conclusao;
    private Boolean concluido;
}
