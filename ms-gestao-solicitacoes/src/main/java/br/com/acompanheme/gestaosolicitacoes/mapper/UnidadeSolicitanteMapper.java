package br.com.acompanheme.gestaosolicitacoes.mapper;

import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.UnidadeSolicitanteDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.UnidadeSolicitante;  

public class UnidadeSolicitanteMapper {

    public static UnidadeSolicitante toEntity(UnidadeSolicitanteDTO dto) {
        if (dto == null) {
            return null;
        }
        UnidadeSolicitante unidadeSolicitante = new UnidadeSolicitante();
        unidadeSolicitante.setCodigo(dto.codigo());
        unidadeSolicitante.setNome(dto.nome());
        unidadeSolicitante.setBairro(dto.bairro());
        unidadeSolicitante.setMunicipio(dto.municipio());
        return unidadeSolicitante;
    }

    public static UnidadeSolicitanteDTO toDTO(UnidadeSolicitante unidade) {
        if (unidade == null) {
            return null;
        }
        return new UnidadeSolicitanteDTO(unidade.getCodigo(), unidade.getNome(),
                unidade.getMunicipio(), unidade.getBairro());
    }
}
