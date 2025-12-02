package br.com.careermatcher.services;

import br.com.careermatcher.enums.Modalidade;
import br.com.careermatcher.enums.PesosRanker;
import br.com.careermatcher.models.*;
import br.com.careermatcher.relations.HabilEm;
import br.com.careermatcher.relations.RequisitaCompetenciaEm;
import br.com.careermatcher.repositories.CandidatoRepository;
import br.com.careermatcher.repositories.VagaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.com.careermatcher.Regioes.*;

@Service
@AllArgsConstructor
public class RankerService {

    private final CandidatoRepository candidatoRepository;
    private final VagaRepository vagaRepository;

    /*
        A senioridade exigida na Vaga é utilizada como baseline, isto é, se o Candidato atende ou supera, ganha uma recompensa.
        Analogamente, se possui senioridade inferior sofre uma penalização no ranqueamento
    */
    public double senioridadeRanker(Vaga vaga, Candidato candidato){

        double proporcao = (double) candidato.getSenioridade().getValor() /vaga.getSenioridade().getValor();
        if(candidato.getSenioridade().getValor() < vaga.getSenioridade().getValor()) return proporcao*PesosRanker.SENIORIDADE_ABAIXO.getPeso();
        if(candidato.getSenioridade().getValor() == vaga.getSenioridade().getValor()) return proporcao*PesosRanker.SENIORIDADE_CUMPRIDA.getPeso();
        return proporcao*PesosRanker.SENIORIODADE_ACIMA.getPeso();
    }

    /*
        Para vagas Presenciais ou Híbridas, será recompensado o candidato que reside na mesma região da vaga e penalizado caso contrário
        Ou seja, se a Empresa estiver localizada na região Centro-Oeste do Brasil, os candidatos que residem em qualquer estado desta região obterão esta recompensa
    */
    public int localidadeXmodalidadeRanker(Vaga vaga, Candidato candidato){
        if(vaga.getModalidade().equals(Modalidade.PRESENCIAL) || vaga.getModalidade().equals(Modalidade.HIBRIDO)){
            if(getIdRegiao(getRegioes(), vaga.getCidade()).equals(getIdRegiao(getRegioes(), candidato.getCidade()))) return PesosRanker.LOCALIDADE_CUMPRIDA.getPeso();
            else return PesosRanker.LOCALIDADE_NAO_CUMPRIDA.getPeso();
        }
        return 0; // A vaga é remota
    }

    public int modalidadeRanker(Vaga vaga, Candidato candidato){
        if(vaga.getModalidade().equals(Modalidade.REMOTO)) return PesosRanker.VAGA_MODALIDADE_REMOTO.getPeso();
        return 0;
    }

    public int graduacaoRanker(Vaga vaga, Candidato candidato){
        if(vaga.getGraduacao() == null) return 0; // a vaga não exige nenhuma graduação
        if(candidato.getGraduacao() == null) return PesosRanker.GRADUACAO_NAO_CUMPRIDA.getPeso(); // candidato não possui nenhuma graduação

        int rank = computaGraduacaoRanker(vaga, candidato);
        return (rank == 0) ? PesosRanker.GRADUACAO_NAO_CUMPRIDA.getPeso() : rank ; // retorna negativo se o candidato não possuir uma graduação válida para a vaga
    }

    private int computaGraduacaoRanker(Vaga vaga, Candidato candidato){
        int rank;
        int rankMax =0;
        for(Graduacao graduacaoVaga : vaga.getGraduacao()){
            rank = 0;
            rankMax = 0;
            if(graduacaoVaga.getCurso().equalsIgnoreCase(candidato.getGraduacao().getCurso()) && graduacaoVaga.getTipoGraduacao()==candidato.getGraduacao().getTipoGraduacao()){
                rank = PesosRanker.GRADUACAO_CURSO_CUMPRIDO.getPeso();
                if(graduacaoVaga.getInstituicao().equalsIgnoreCase(candidato.getGraduacao().getInstituicao())){
                    rank += PesosRanker.GRADUACAO_INSTITUICAO_CUMPRIDO.getPeso();
                }
                if(graduacaoVaga.getAno_conclusao() >= candidato.getGraduacao().getAno_conclusao()){
                    rank += PesosRanker.GRADUACAO_TEMPO_CONCLUIDO_CUMPRIDO.getPeso();
                }
            }
            if(rank > rankMax){rankMax = rank;}
        }
        return rankMax;
    }

    public double experienciaRanker(Vaga vaga, Candidato candidato){
        if(vaga.getExperiencia() == null) return 0;
        if(candidato.getExperiencias() == null) return PesosRanker.EXPERIENCIA_NAO_CUMPRIDA.getPeso();

        return computaExperienciaRanker(vaga, candidato);
    }

    private double computaExperienciaRanker(Vaga vaga, Candidato candidato){
        // Só faz sentido avaliar as experiÊncias do candidato que fazem sentido para o cargo da Vaga
        List<Experiencia> experienciasFiltradas = candidato.getExperiencias().stream().filter(exp -> exp.getCargo().equalsIgnoreCase(vaga.getCargo())).toList();
        int totalMesesExperiencia = 0;
        double rank = 0.;
        if(!experienciasFiltradas.isEmpty()){
            for(Experiencia experiencia : experienciasFiltradas){
                totalMesesExperiencia += experiencia.getDuração_meses();

                if(experiencia.getSenioridade().getValor() > candidato.getSenioridade().getValor()) rank += PesosRanker.EXPERIENCIA_SENIORIODADE_ACIMA.getPeso();
                else if( experiencia.getSenioridade().getValor() == candidato.getSenioridade().getValor()) rank+= PesosRanker.EXPERIENCIA_SENIORIODADE_CUMPRIDA.getPeso();
                else rank += PesosRanker.EXPERIENCIA_SENIORIODADE_ABAIXO.getPeso();
            }

            rank += (PesosRanker.EXPERIENCIA_TEMPO.getPeso() * ((double) totalMesesExperiencia / vaga.getExperiencia().getDuração_meses()));
        }
        return rank;
    }

    public double competenciasRanker(Vaga vaga, Candidato candidato){
        double rank = 0;

        List<HabilEm> habilEm = candidato.getCompetencias();
        List<RequisitaCompetenciaEm> requisitaCompetenciaEm = vaga.getCompetencias();

        for(HabilEm candHabil : habilEm){
            for(RequisitaCompetenciaEm vagaReq : requisitaCompetenciaEm){
                if(candHabil.getCompetencia().getNome().equalsIgnoreCase(vagaReq.getCompetencia().getNome())){
                    rank+= ((double) candHabil.getPeso()/vagaReq.getPeso())*PesosRanker.COMPETENCIA_CUMPRIDA.getPeso();
                }
            }
        }
        return (rank==0.) ? (double) PesosRanker.COMPETENCIA_NAO_CUMPRIDA.getPeso() : rank;
    }


    public int mestradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;
        if(vaga.getMestrado() == null) return 0;
        if(candidato.getMestrado()==null) return PesosRanker.MESTRADO_NAO_CUMPRIDO.getPeso();

        if(vaga.getMestrado().getCurso().equalsIgnoreCase(candidato.getMestrado().getCurso())
            || vaga.getMestrado().getSubarea().equalsIgnoreCase(candidato.getMestrado().getSubarea())){

                    rank += PesosRanker.MESTRADO_CURSO_OU_SUBAREA_CUMPRIDO.getPeso();
                    if(vaga.getMestrado().getInstituicao().equalsIgnoreCase(candidato.getMestrado().getInstituicao())){
                        rank += PesosRanker.MESTRADO_INSTITUICAO_CUMPRIDA.getPeso();
                    }
                    if(vaga.getMestrado().getAno_conclusao() >= candidato.getMestrado().getAno_conclusao()){
                        rank += PesosRanker.MESTRADO_TEMPO_CONCLUIDO_CUMPRIDO.getPeso();
                    }
        }
        return (rank==0)? PesosRanker.MESTRADO_NAO_CUMPRIDO.getPeso() : rank;
    }

    public int doutoradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;
        if(vaga.getDoutorado() == null) return 0;
        if(candidato.getDoutorado()==null) return PesosRanker.DOUTORADO_NAO_CUMPRIDO.getPeso();

        if(vaga.getDoutorado().getCurso().equalsIgnoreCase(candidato.getDoutorado().getCurso())
                || vaga.getDoutorado().getSubarea().equalsIgnoreCase(candidato.getDoutorado().getSubarea())){

                    rank += PesosRanker.DOUTORADO_CURSO_OU_SUBAREA_CUMPRIDO.getPeso();

                    if(vaga.getDoutorado().getInstituicao().equalsIgnoreCase(candidato.getDoutorado().getInstituicao())){
                        rank += PesosRanker.DOUTORADO_INSTITUICAO_CUMPRIDA.getPeso();
                    }
                    if(vaga.getDoutorado().getAno_conclusao() >= candidato.getDoutorado().getAno_conclusao()){
                        rank += PesosRanker.DOUTORADO_TEMPO_CONCLUIDO_CUMPRIDO.getPeso();
                    }
        }
        return (rank == 0) ? PesosRanker.DOUTORADO_NAO_CUMPRIDO.getPeso() : rank ;
    }

    public int posDoutoradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;

        if(vaga.getPosDoutorado() == null) return 0;
        if(candidato.getPosDoutorado()==null) return PesosRanker.POSDOUTORADO_NAO_CUMPRIDO.getPeso();

        if(vaga.getPosDoutorado().getCurso().equalsIgnoreCase(candidato.getPosDoutorado().getCurso())
                || vaga.getPosDoutorado().getSubarea().equalsIgnoreCase(candidato.getPosDoutorado().getSubarea())){

                    rank += PesosRanker.POSDOUTORADO_CURSO_OU_SUBAREA_CUMPRIDO.getPeso();

                    if(vaga.getPosDoutorado().getInstituicao().equalsIgnoreCase(candidato.getPosDoutorado().getInstituicao())){
                        rank += PesosRanker.POSDOUTORADO_INSTITUICAO_CUMPRIDA.getPeso();
                    }
                    if(vaga.getPosDoutorado().getAno_conclusao() >= candidato.getPosDoutorado().getAno_conclusao()){
                     rank += PesosRanker.POSDOUTORADO_TEMPO_CONCLUIDO_CUMPRIDO.getPeso();
                    }
        }
        return ( rank == 0) ? PesosRanker.POSDOUTORADO_NAO_CUMPRIDO.getPeso() : rank ;
    }
}