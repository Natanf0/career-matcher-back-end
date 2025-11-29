package br.com.careermatcher.models;

import br.com.careermatcher.enums.Senioridade;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

    @Relationship(type = "HABIL_EM", direction = Relationship.Direction.OUTGOING)
    private List<Competencia> competencias;

    @Relationship(type = "TRABALHOU_EM", direction = Relationship.Direction.OUTGOING)
    private List<Experiencia> experiencias;

    @Relationship(type = "FORMADO_EM", direction = Relationship.Direction.OUTGOING)
    private Graduacao graduacao;

    @Relationship(type = "FORMADO_EM", direction = Relationship.Direction.OUTGOING)
    private Mestrado mestrado;

    @Relationship(type = "FORMADO_EM", direction = Relationship.Direction.OUTGOING)
    private Doutorado doutorado;

    @Relationship(type = "FORMADO_EM", direction = Relationship.Direction.OUTGOING)
    private PosDoutorado posDoutorado;

    @Transient
    private List<Vaga> listaPreferenciaVagas; // Aqui, vou alocar as Vagas em ordem de preferencia do candidato
}
