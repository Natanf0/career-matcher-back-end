package br.com.careermatcher.models;

import br.com.careermatcher.enums.Senioridade;
import br.com.careermatcher.relations.HabilEm;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("CANDIDATO")
@Getter
@RequiredArgsConstructor
public class Candidato {
    @Id @GeneratedValue @NonNull private Long id;
    @NonNull private String nome, cargo, país, cidade, telefone, email;
    @NonNull private Senioridade senioridade;
    @Setter private Long idVagaEscolhida;

    @Relationship(type = "HABIL_EM", direction = Relationship.Direction.OUTGOING)
    private List<HabilEm> competencias;

    @Relationship(type = "TRABALHOU_EM", direction = Relationship.Direction.OUTGOING)
    private List<Experiencia> experiencias;

    @Relationship(type = "GRADUADO_EM", direction = Relationship.Direction.OUTGOING)
    private Graduacao graduacao;

    @Relationship(type = "MESTRE_EM", direction = Relationship.Direction.OUTGOING)
    private Mestrado mestrado;

    @Relationship(type = "DOUTOR_EM", direction = Relationship.Direction.OUTGOING)
    private Doutorado doutorado;

    @Relationship(type = "POSDOUTOR_EM", direction = Relationship.Direction.OUTGOING)
    private PosDoutorado posDoutorado;

    @Transient @Setter
    private List<Vaga> listaPreferenciaVagas; // Aqui, vou alocar as Vagas em ordem de preferencia do candidato


}
