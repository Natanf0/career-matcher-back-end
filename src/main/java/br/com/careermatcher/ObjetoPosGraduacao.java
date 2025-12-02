package br.com.careermatcher;

import br.com.careermatcher.models.PosGraduacao;
import lombok.AllArgsConstructor;

public class ObjetoPosGraduacao<T extends PosGraduacao> extends PosGraduacao {

    public ObjetoPosGraduacao(T t, Long id, String curso, String instituicao, String subarea, Integer ano_conclusao) {
        super(id, curso, instituicao, subarea, ano_conclusao);
    }
}
