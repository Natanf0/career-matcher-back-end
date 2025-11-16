package br.com.careermatcher.repositories;

import br.com.careermatcher.models.Vaga;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface VagaRepository extends Neo4jRepository<Vaga, Long> {
}
