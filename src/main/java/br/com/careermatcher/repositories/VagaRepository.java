package br.com.careermatcher.repositories;

import br.com.careermatcher.models.Vaga;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface VagaRepository extends Neo4jRepository<Vaga, Long> {
        // Query personalizada para carregar todos os relacionamentos
        @Query("MATCH (v:VAGA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(g:GRADUACAO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_EXPERIENCIA_EM]->(e:EXPERIENCIA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_COMPETENCIA_EM]->(c:COMPETENCIA) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(m:MESTRADO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(d:DOUTORADO) " +
                "OPTIONAL MATCH (v)-[:REQUISITA_FORMACAO_EM]->(p:POSDOUTORADO) " +
                "RETURN v, collect(DISTINCT g) as graduacoes, collect(DISTINCT e) as experiencias, " +
                "collect(DISTINCT c) as competencias, m, d, p")
        List<Vaga> findAllWithRelationships();

}
