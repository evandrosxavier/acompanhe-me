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
public class Medico {

    @Column(name = "medico_nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "medico_crm", nullable = false, length = 20)
    private String crm;

    @Column(name = "medico_especialidade", nullable = false, length = 100)
    private String especialidade;
}
