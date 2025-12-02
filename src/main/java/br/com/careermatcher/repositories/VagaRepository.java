package br.com.careermatcher.repositories;

import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Vaga;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VagaRepository extends Neo4jRepository<Vaga, Long> {
        // Query personalizada para carregar todos os relacionamentos
        @Query("MATCH (v:VAGA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(g:GRADUACAO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_EXPERIENCIA_EM]->(e:EXPERIENCIA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_COMPETENCIA_EM]->(c:COMPETENCIA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(m:MESTRADO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(d:DOUTORADO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(p:POSDOUTORADO) " +
                "RETURN v AS vaga, " +
                "collect(DISTINCT g) AS graduacao, " +
                "collect(DISTINCT e) AS experiencias, " +
                "collect(DISTINCT c) AS competencias, " +
                "collect(DISTINCT m) AS mestrados, " +
                "collect(DISTINCT d) AS doutorados, " +
                "collect(DISTINCT p) AS posDoutorados")
        List<Vaga> findAllWithRelationships();

        @Query("MATCH (v:VAGA)-[r:REQUISITA_COMPETENCIA_EM]->(comp:COMPETENCIA) WHERE id(v) = $id RETURN v, collect(r), collect(comp)")
        Optional<Vaga> findWithCompetencias(@Param("id") Long id);

}
