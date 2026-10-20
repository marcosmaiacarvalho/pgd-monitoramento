package com.projetopgd.bancoeapipgd.dtos.imports;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RespostaApiAnttProDTO<T> {


    Integer status;

    //Pode ser usuários ou planos
    @JsonAlias({"result", "config"})
    List<T> itens;

    Integer offset;

}
