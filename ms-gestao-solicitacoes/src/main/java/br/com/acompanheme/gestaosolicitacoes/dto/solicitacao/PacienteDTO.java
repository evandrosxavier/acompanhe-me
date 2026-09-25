package br.com.acompanheme.gestaosolicitacoes.dto.solicitacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Dados do paciente")
public record PacienteDTO(

        @Schema(description = "Nome completo do paciente", example = "Maria Clara Santos")
        @NotBlank(message = "Nome do paciente é obrigatório")
        @Size(max = 150, message = "Nome do paciente deve ter no máximo 150 caracteres")
        String nome,

        @Schema(description = "CPF com 11 dígitos numéricos sem pontuação", example = "12345678901")
        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos sem pontuação")
        String cpf,

        @Schema(description = "Cartão Nacional de Saúde, 15 dígitos numéricos", example = "898000000000000")
        @NotBlank(message = "CNS é obrigatório")
        @Pattern(regexp = "\\d{15}", message = "CNS deve conter 15 dígitos numéricos")
        String cns,

        @Schema(description = "Data de nascimento (formato: yyyy-MM-dd)", example = "1990-05-20")
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser uma data no passado")
        LocalDate dataNascimento,

        @Schema(description = "Telefone do paciente", example = "19987654321")
        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Schema(description = "E-mail do paciente", example = "maria.santos@email.com")
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 150, message = "E-mail deve ter no máximo 150 caracteres")
        String email,

        @Schema(description = "Endereço resumido do paciente")
        @NotNull(message = "Endereço é obrigatório")
        @Valid
        EnderecoDTO endereco
) {}
