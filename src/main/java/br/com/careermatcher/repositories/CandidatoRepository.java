package br.com.careermatcher.repositories;
import br.com.careermatcher.models.Candidato;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface CandidatoRepository extends Neo4jRepository<Candidato, Long> {

    @Query("""
        MERGE (c:CANDIDATO {email: $email})
        SET c.nome = $nome,
            c.cargo = $cargo,
            c.país = $país,
            c.cidade = $cidade,
            c.telefone = $telefone,
            c.senioridade = $senioridade,
            c.id = $idCandidato
        RETURN c
    """)
    Candidato upsertCandidatoComId(
            @Param("nome") String nome,
            @Param("cargo") String cargo,
            @Param("país") String país,
            @Param("cidade") String cidade,
            @Param("telefone") String telefone,
            @Param("email") String email,
            @Param("senioridade") String senioridade,
            @Param("idCandidato") Long idCandidato
    );


    @Query("MATCH (c:CANDIDATO) WHERE c.id = $candidatoId SET c.idVagaEscolhida = $vagaId")
    void atualizarVagaEscolhida(Long candidatoId, Long vagaId);


}
