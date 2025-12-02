package br.com.careermatcher.relations;

import br.com.careermatcher.models.Competencia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HabilEm {
    @RelationshipId
    private Long id;

    private Integer peso;

    @TargetNode
    private Competencia competencia;
}