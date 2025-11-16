package br.com.careermatcher.models;

import br.com.careermatcher.enums.Modalidade;
import br.com.careermatcher.enums.Senioridade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("VAGA")

@AllArgsConstructor
public class Vaga {
    @Id @GeneratedValue
    @Getter private Long id;
    @Getter private String empresa, cargo, cidade;
    @Getter private Senioridade senioridade;
    @Getter private Modalidade  modalidade;

    @Relationship(type = "REQUISITA_FORMACAO_EM", direction = Relationship.Direction.OUTGOING)
    private List<Graduacao> graduacao; // Normalmente vagas possuem uma lista de graduações correlatas de interesse. Então decidi cobrir isto

    @Relationship(type = "REQUISITA_EXPERIENCIA_EM", direction = Relationship.Direction.OUTGOING)
    private Experiencia experiencia; // As descrições das vagas são agnósticas as formas das experiencias. Costumam pedir algo mais exato como "Y anos de experiência em"

    @Relationship(type = "REQUISITA_COMPETENCIA_EM", direction = Relationship.Direction.OUTGOING)
    private List<Competencia> competencias;

    @Relationship(type = "REQUISITA_FORMACAO_EM", direction = Relationship.Direction.OUTGOING)
    private Mestrado mestrado;

    @Relationship(type = "REQUISITA_FORMACAO_EM", direction = Relationship.Direction.OUTGOING)
    private Doutorado doutorado;

    @Relationship(type = "REQUISITA_FORMACAO_EM", direction = Relationship.Direction.OUTGOING)
    private PosDoutorado posDoutorado;
}