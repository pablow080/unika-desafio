package com.example.backend.mapper;

import com.example.backend.dto.EnderecoDTO;
import com.example.backend.model.Endereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    EnderecoMapper INSTANCE = Mappers.getMapper(EnderecoMapper.class);

    @Mapping(target = "cliente", ignore = true)
    Endereco toEntity(EnderecoDTO enderecoDTO);

    @Mapping(target = "clienteId", source = "cliente.id")
    EnderecoDTO toDto(Endereco endereco);
}