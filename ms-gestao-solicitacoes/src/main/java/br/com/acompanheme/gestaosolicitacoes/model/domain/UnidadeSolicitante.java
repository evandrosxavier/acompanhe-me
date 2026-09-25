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
public class UnidadeSolicitante {

    @Column(length = 20)
    private String codigo;

    @Column(length = 150)
    private String nome;

    @Column(length = 100)
    private String municipio;

    @Column(length = 100)
    private String bairro;
}
