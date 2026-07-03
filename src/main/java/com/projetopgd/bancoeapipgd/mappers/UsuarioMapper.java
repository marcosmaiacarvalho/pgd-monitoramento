package com.projetopgd.bancoeapipgd.mappers;

import com.projetopgd.bancoeapipgd.dtos.responses.base.UsuarioDTO;
import com.projetopgd.bancoeapipgd.entities.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {


    public UsuarioDTO entidadeParaDTO(Usuario entidade) {

        if (entidade == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();

        dto.setMatricula(entidade.getMatricula() == null ? "Matrícula não cadastrada." : entidade.getMatricula());
        dto.setNome(entidade.getNome());
        dto.setEmail(entidade.getEmail());
        dto.setUnidadeAtual(entidade.getUnidadeAtual() == null ? "Unidade não cadastrada." : entidade.getUnidadeAtual());


        return dto;
    }

}
