package com.example.backend.mapper;

import com.example.backend.dto.ClienteDTO;
import com.example.backend.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {EnderecoMapper.class})
public interface ClienteMapper {

    ClienteMapper INSTANCE = Mappers.getMapper(ClienteMapper.class);

    @Mapping(target = "enderecos", source = "enderecos")
    Cliente toEntity(ClienteDTO clienteDTO);

    @Mapping(target = "enderecos", source = "enderecos")
    ClienteDTO toDto(Cliente cliente);
}