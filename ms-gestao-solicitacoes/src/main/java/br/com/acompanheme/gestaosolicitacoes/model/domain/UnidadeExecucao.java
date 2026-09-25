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
public class UnidadeExecucao {

    @Column(length = 20)
    private String codigo;

    @Column(length = 150)
    private String nome;

    @Column(length = 100)
    private String municipio;

    @Column(length = 100)
    private String bairro;

    @Column(length = 255)
    private String endereco;

    @Column(length = 3)
    private String ddd;

    @Column(length = 20)
    private String telefone;
}
