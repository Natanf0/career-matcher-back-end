package br.com.careermatcher.models;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.Node;

@Node("DOUTORADO")
@Getter
public class Doutorado extends PosGraduacao {

    public Doutorado(Long id, String curso, String instituicao, String subarea, Integer ano_conclusao) {
        super(id, curso, instituicao, subarea, ano_conclusao);
    }
}
