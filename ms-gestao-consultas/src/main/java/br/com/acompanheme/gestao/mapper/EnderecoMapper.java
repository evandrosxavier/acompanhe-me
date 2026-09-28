package br.com.acompanheme.gestao.mapper;

import br.com.acompanheme.gestao.dto.endereco.EnderecoRequest;
import br.com.acompanheme.gestao.dto.endereco.EnderecoResponse;
import br.com.acompanheme.gestao.dto.endereco.EnderecoUpdate;
import br.com.acompanheme.gestao.model.domain.Endereco;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    Endereco toEntity(EnderecoRequest dto);

    EnderecoResponse toResponseDTO(Endereco endereco);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EnderecoRequest dto, @MappingTarget Endereco endereco);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EnderecoUpdate dto, @MappingTarget Endereco endereco);
}
