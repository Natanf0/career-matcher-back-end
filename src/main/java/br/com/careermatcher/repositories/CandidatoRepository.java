package br.com.careermatcher.repositories;

import br.com.careermatcher.models.Candidato;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface CandidatoRepository extends Neo4jRepository<Candidato, Long> {

}
