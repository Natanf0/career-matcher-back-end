package br.com.careermatcher.services;

import br.com.careermatcher.enums.Modalidade;
import br.com.careermatcher.enums.PesosRanker;
import br.com.careermatcher.models.Candidato;
import br.com.careermatcher.models.Experiencia;
import br.com.careermatcher.models.Graduacao;
import br.com.careermatcher.models.Vaga;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static br.com.careermatcher.Regioes.*;

@Service
@AllArgsConstructor
public class RankerService {

    public int senioridadeRanker(Vaga vaga, Candidato candidato){
        /*
        A senioridade exigida na Vaga é utilizada como baseline, isto é, se o Candidato atende ou supera, ganha uma recompensa.
        Analogamente, se possui senioridade inferior sofre uma penalização no ranqueamento
         */
        if(vaga.getSenioridade().getValor() > candidato.getSenioridade().getValor()) {
            return PesosRanker.SENIORIDADE_NAO_CUMPRIDA.getPeso();
        }
        return PesosRanker.SENIORIDADE_CUMPRIDA.getPeso();
    }

    public int localidadeXmodalidadeRanker(Vaga vaga, Candidato candidato){
        /*
        Para vagas Presenciais ou Híbridas, será recompensado o candidato que reside na mesma região da vaga e penalizado caso contrário
        Ou seja, se a Empresa estiver localizada na região Centro-Oeste do Brasil, os candidatos que residem em qualquer estado desta região obterão esta recompensa
         */
        if(!vaga.getModalidade().equals(Modalidade.Remoto)){
            if(getIdRegiao(getRegioes(), vaga.getCidade()).equals(getIdRegiao(getRegioes(), candidato.getCidade()))) return PesosRanker.LOCALIDADE_CUMPRIDA.getPeso();
        }
        return PesosRanker.LOCALIDADE_NAO_CUMPRIDA.getPeso();
    }

    public int graduacaoRanker(Vaga vaga, Candidato candidato){
        int rank;
        int rankMax =0;
        if(vaga.getGraduacao() != null){
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
        }
        return (rankMax == 0 && vaga.getGraduacao()!=null) ? PesosRanker.GRADUACAO_NAO_CUMPRIDA.getPeso() : rankMax ;
    }

    public int experienciaRanker(Vaga vaga, Candidato candidato){
        List<Experiencia> experienciasFiltradas = candidato.getExperiencias().stream().filter(exp -> exp.getCargo().equalsIgnoreCase(vaga.getCargo())).toList();
        int totalMesesExperiencia = 0, rank = 0;
        for(Experiencia experiencia : experienciasFiltradas){
            totalMesesExperiencia += experiencia.getDuracao_meses();

            if(experiencia.getSenioridade().getValor() >= candidato.getSenioridade().getValor()){ // quebrar em iguak ou maior
                rank += PesosRanker.EXPERIENCIA_SENIORIODADE_ACIMA.getPeso();

            }else rank += PesosRanker.EXPERIENCIA_SENIORIODADE_ABAIXO.getPeso();
        }

        rank += (int) (PesosRanker.EXPERIENCIA_TEMPO.getPeso() * (totalMesesExperiencia / vaga.getExperiencia().getDuracao_meses()));
        return rank;
    }

    public int competenciasRanker(Vaga vaga, Candidato candidato){
        return 0;
    }

    public int mestradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;
        if(vaga.getMestrado() != null){
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
        }
        return ( rank == 0 && vaga.getMestrado() != null) ? PesosRanker.MESTRADO_NAO_CUMPRIDO.getPeso() : rank ;
    }

    public int doutoradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;
        if(vaga.getDoutorado() != null){
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
        }
        return ( rank == 0 && vaga.getDoutorado() != null) ? PesosRanker.DOUTORADO_NAO_CUMPRIDO.getPeso() : rank ;
    }

    public int posDoutoradoRanker(Vaga vaga, Candidato candidato){
        int rank = 0 ;
        if(vaga.getPosDoutorado() != null){
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
        }
        return ( rank == 0 && vaga.getPosDoutorado() != null) ? PesosRanker.POSDOUTORADO_NAO_CUMPRIDO.getPeso() : rank ;
    }


}