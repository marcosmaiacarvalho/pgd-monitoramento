package com.projetopgd.bancoeapipgd.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TipoPerfil {

    @JsonProperty("Administrador")
    ADMINISTRADOR,

    @JsonProperty("Gestor")
    GESTOR,

    @JsonProperty("Usuário")
    USUARIO,

    @JsonProperty("Usuário Autônomo")
    USUARIO_AUTONOMO,

    @JsonProperty("Visualizador")
    VISUALIZADOR

}
