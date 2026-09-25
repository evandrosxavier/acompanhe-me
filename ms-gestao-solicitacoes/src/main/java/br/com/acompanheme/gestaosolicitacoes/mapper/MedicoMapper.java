package br.com.acompanheme.gestaosolicitacoes.mapper;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.MedicoDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Medico;
public class MedicoMapper {

    public static Medico toEntity(MedicoDTO dto) {
        if (dto == null) {
            return null;
        }
        Medico medico = new Medico();
        medico.setNome(dto.nome());
        medico.setCrm(dto.crm());
        medico.setEspecialidade(dto.especialidade());
        return medico;
    }

    public static MedicoDTO toDTO(Medico medico) {
        if (medico == null) {
            return null;
        }
        return new MedicoDTO(medico.getNome(), medico.getCrm(), medico.getEspecialidade());
    }
}
