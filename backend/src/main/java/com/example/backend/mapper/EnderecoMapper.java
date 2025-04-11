package com.example.backend.mapper;

import com.example.backend.dto.EnderecoDTO;
import com.example.backend.model.Endereco;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    @Mapping(target = "cliente", ignore = true) // Ignora cliente ao converter DTO -> entidade
    Endereco toEntity(EnderecoDTO dto);

    @Mapping(target = "clienteId", source = "cliente.id") // Converte entidade -> DTO usando só o ID
    EnderecoDTO toDto(Endereco endereco);
}
