package br.com.acompanheme.gestaosolicitacoes.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Paciente {

    @Column(name = "paciente_nome", nullable = false, length = 150)
    private String nome;

    @Column(name = "paciente_cpf", nullable = false, length = 14)
    private String cpf;

    @Column(name = "paciente_cns", nullable = false, length = 15)
    private String cns;

    @Column(name = "paciente_data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "paciente_telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "paciente_email", nullable = false, length = 150)
    private String email;

    @Embedded
    private Endereco endereco;
}
