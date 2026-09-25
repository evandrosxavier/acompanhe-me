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
public class Endereco {

    @Column(name = "paciente_endereco_municipio", nullable = false, length = 100)
    private String municipio;

    @Column(name = "paciente_endereco_bairro", nullable = false, length = 100)
    private String bairro;
}
