package br.com.acompanheme.gestaosolicitacoes.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Procedimento {

    @Column(name = "codigo_procedimento", length = 20)
    private String codigoProcedimento;

    @Column(name = "descricao_procedimento", nullable = false, length = 255)
    private String descricaoProcedimento;
}
