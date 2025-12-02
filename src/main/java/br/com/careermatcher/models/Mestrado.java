package br.com.careermatcher.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("MESTRADO")
@Getter
public class Mestrado extends PosGraduacao{

    public Mestrado(Long id, String curso, String instituicao, String subarea, Integer ano_conclusao) {
        super(id, curso, instituicao, subarea, ano_conclusao);
    }
}
