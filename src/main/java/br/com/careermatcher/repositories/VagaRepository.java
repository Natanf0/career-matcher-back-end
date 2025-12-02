package br.com.careermatcher.repositories;
import br.com.careermatcher.models.Vaga;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

public interface VagaRepository extends Neo4jRepository<Vaga, Long> {

    @Query("""
        MERGE (v:VAGA {
            cargo: $cargo,
            cidade: $cidade,
            empresa: $empresa,
            senioridade: $senioridade,
            modalidade: $modalidade
        })
        SET v.id = $idVaga
    """)
    void upsertVagaComId(
            @Param("cargo") String cargo,
            @Param("cidade") String cidade,
            @Param("empresa") String empresa,
            @Param("senioridade") String senioridade,
            @Param("modalidade") String modalidade,
            @Param("idVaga") Long idVaga
    );


    @Query("MATCH (v:VAGA) WHERE v.id = $vagaId SET v.idCandidatoEscolhido = $candidatoId")
    void atualizarCandidatoEscolhido(Long vagaId, Long candidatoId);

}
