package br.com.acompanheme.gestaosolicitacoes.mapper;
import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.ProcedimentoDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Procedimento;
public class ProcedimentoMapper {
    public static Procedimento toEntity(ProcedimentoDTO dto) {
        if (dto == null) {
            return null;
        }
        Procedimento procedimento = new Procedimento();
        procedimento.setCodigoProcedimento(dto.codigoProcedimento());
        procedimento.setDescricaoProcedimento(dto.descricaoProcedimento());
        return procedimento;
    }

    public static ProcedimentoDTO toDTO(Procedimento procedimento) {
        if (procedimento == null) {
            return null;
        }
        return new ProcedimentoDTO(procedimento.getCodigoProcedimento(), procedimento.getDescricaoProcedimento());
    }
}
