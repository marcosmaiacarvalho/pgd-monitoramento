package com.projetopgd.bancoeapipgd.dtos.responses.aggregates;

import com.projetopgd.bancoeapipgd.dtos.responses.base.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UsuarioUnidadesDTO {

    private UsuarioDTO usuario;
    private List<UnidadePlanosDTO> unidades;


}
