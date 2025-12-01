package br.com.careermatcher.models;

import br.com.careermatcher.enums.Modalidade;
import br.com.careermatcher.enums.Senioridade;
import br.com.careermatcher.models.relations.RequisitaCompetenciaEm;
import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashMap;
import java.util.List;

@Node("VAGA")
@Getter
@RequiredArgsConstructor
public class Vaga {
    @Id @GeneratedValue private Long id;
    @NonNull private String empresa, cargo, cidade;
    @NonNull private Senioridade senioridade;
     @NonNull private Modalidade  modalidade;

    @Relationship(type = "REQUISITA_GRADUACAO_EM", direction = Relationship.Direction.OUTGOING)
    private List<Graduacao> graduacao; // Normalmente vagas possuem uma lista de graduações correlatas de interesse. Então decidi cobrir isto

    @Relationship(type = "REQUISITA_EXPERIENCIA_EM", direction = Relationship.Direction.OUTGOING)
    private Experiencia experiencia; // As descrições das vagas são agnósticas as formas das experiencias. Costumam pedir algo mais exato como "Y anos de experiência em"

    @Relationship(type = "REQUISITA_COMPETENCIA_EM", direction = Relationship.Direction.OUTGOING)
    private List<RequisitaCompetenciaEm> competencias;

    @Relationship(type = "REQUISITA_MESTRADO_EM", direction = Relationship.Direction.OUTGOING)
    private Mestrado mestrado;

    @Relationship(type = "REQUISITA_DOUTORADO_EM", direction = Relationship.Direction.OUTGOING)
    private Doutorado doutorado;

    @Relationship(type = "REQUISITA_POSDOUTORADO_EM", direction = Relationship.Direction.OUTGOING)
    private PosDoutorado posDoutorado;

    @Transient @Setter
    private List<Candidato> listaPreferenciaCandidatos; // aqui, vou alocar os candidatos preferenciais da vaga

    @Transient @Setter
    private Candidato candidatoEscolhido;
}