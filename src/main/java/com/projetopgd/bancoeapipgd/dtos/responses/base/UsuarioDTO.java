package com.projetopgd.bancoeapipgd.dtos.responses.base;


import lombok.Data;

@Data
public class UsuarioDTO {

    private String matricula;
    private String nome;
    private String email;
    private String unidadeAtual;

}
