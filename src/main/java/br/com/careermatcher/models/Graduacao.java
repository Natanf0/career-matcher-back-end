package br.com.careermatcher.models;
import br.com.careermatcher.enums.TipoGraduacao;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("GRADUACAO")
@AllArgsConstructor
@Getter
public class Graduacao {
    @Id @GeneratedValue
    private Long id;

    private String curso, instituicao;
    private TipoGraduacao tipoGraduacao;
    private Integer ano_conclusao;
    private Boolean concluido;
}
