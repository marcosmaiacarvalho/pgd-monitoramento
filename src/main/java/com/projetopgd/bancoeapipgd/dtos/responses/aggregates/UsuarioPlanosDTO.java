package com.projetopgd.bancoeapipgd.dtos.responses.aggregates;

import com.projetopgd.bancoeapipgd.dtos.responses.base.PlanoDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.base.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class UsuarioPlanosDTO {

    private UsuarioDTO usuario;
    private List<PlanoDTO> planos;

}
