package br.com.careermatcher.repositories;

import br.com.careermatcher.models.Candidato;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CandidatoRepository extends Neo4jRepository<Candidato, Long> {
    @Query("MATCH (c:CANDIDATO) OPTIONAL MATCH (c)-[:HABIL_EM]->(comp:Competencia) " +
            "OPTIONAL MATCH (c)-[:TRABALHOU_EM]->(exp:Experiencia) " +
            "OPTIONAL MATCH (c)-[:GRADUADO_EM]->(grad:Graduacao) " +
            "OPTIONAL MATCH (c)-[:MESTRE_EM]->(mest:Mestrado) " +
            "OPTIONAL MATCH (c)-[:DOUTOR_EM]->(doc:Doutorado) " +
            "OPTIONAL MATCH (c)-[:POSDOUTOR_EM]->(posdoc:PosDoutorado) " +
            "RETURN c, collect(comp), collect(exp), grad, mest, doc, posdoc")
    List<Candidato> findAllWithRelations();

    @Query("MATCH (c:CANDIDATO)-[r:HÁBIL_EM]->(comp:COMPETENCIA) WHERE id(c) = $id RETURN c, collect(r), collect(comp)")
    Optional<Candidato> findWithCompetencias(@Param("id") Long id);
}
