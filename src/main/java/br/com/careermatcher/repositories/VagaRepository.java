package br.com.careermatcher.repositories;
import br.com.careermatcher.models.Vaga;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    @Query("""
        MATCH (v:VAGA)
        WHERE ($senioridade IS NULL OR TOUPPER(v.senioridade) IN [sen IN $senioridade | TOUPPER(sen)])
        AND ($modalidade IS NULL OR TOUPPER(v.modalidade) IN [mod IN $modalidade | TOUPPER(mod)])
        AND ($cargo IS NULL OR
             ANY(c IN $cargo WHERE TOLOWER(v.cargo) CONTAINS TOLOWER(c)))
        AND ($empresa IS NULL OR TOLOWER(v.empresa) CONTAINS TOLOWER($empresa))
        AND ($cidade IS NULL OR TOLOWER(v.cidade) CONTAINS TOLOWER($cidade))
        RETURN v
    """)
    List<Vaga> findByFilters(
        @Param("senioridade") List<String> senioridade,
        @Param("modalidade") List<String> modalidade,
        @Param("cargo") List<String> cargo,
        @Param("empresa") String empresa,
        @Param("cidade") String cidade
    );

}
