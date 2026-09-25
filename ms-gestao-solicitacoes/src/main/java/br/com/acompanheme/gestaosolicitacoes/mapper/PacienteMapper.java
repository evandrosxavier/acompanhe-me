package br.com.acompanheme.gestaosolicitacoes.mapper;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.PacienteDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Paciente;
public class PacienteMapper {

    public static Paciente toEntity(PacienteDTO dto) {
        if (dto == null) {
            return null;
        }
        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setCpf(dto.cpf());
        paciente.setCns(dto.cns());
        paciente.setEmail(dto.email());
        paciente.setTelefone(dto.telefone());
        paciente.setEndereco(EnderecoMapper.toEntity(dto.endereco()));
        return paciente;
    }

    public static PacienteDTO toDTO(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        return new PacienteDTO(paciente.getNome(), paciente.getCpf(), paciente.getCns(),
                paciente.getDataNascimento(), paciente.getTelefone(), paciente.getEmail(),
                EnderecoMapper.toDTO(paciente.getEndereco()));
    }
}
