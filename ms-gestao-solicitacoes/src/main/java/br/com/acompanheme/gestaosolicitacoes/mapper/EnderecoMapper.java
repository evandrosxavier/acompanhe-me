package br.com.acompanheme.gestaosolicitacoes.mapper;

import br.com.acompanheme.gestaosolicitacoes.dto.solicitacao.EnderecoDTO;
import br.com.acompanheme.gestaosolicitacoes.model.domain.Endereco;

public class EnderecoMapper {

    public static Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }
        Endereco endereco = new Endereco();
        endereco.setMunicipio(dto.municipio());
        endereco.setBairro(dto.bairro());
        return endereco;
    }

    public static EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(endereco.getMunicipio(), endereco.getBairro());
    }
}
