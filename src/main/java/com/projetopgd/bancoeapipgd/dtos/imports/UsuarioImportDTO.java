package com.projetopgd.bancoeapipgd.dtos.imports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.projetopgd.bancoeapipgd.enums.TipoPerfil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Data
@JsonIgnoreProperties(ignoreUnknown = true) // ignora campos extras que vierem da API
public class UsuarioImportDTO {

    private final static String SEPARADOR_EXTENSO_SIGLA = " - ";

    @JsonProperty("id_user")
    private Long id;

    @Setter(AccessLevel.NONE)
    private String matricula;

    @Setter(AccessLevel.NONE)
    private String email;


    @Setter(AccessLevel.NONE)
    private String nome;

    @JsonProperty("nome_perfil")
    private TipoPerfil tipoPerfil;

    @Setter(AccessLevel.NONE)
    private String unidadeAtual;


    @JsonProperty("matricula")
    public void setMatricula(String matriculaJson) {
        this.matricula = StringUtils.isBlank(matriculaJson) ? null : matriculaJson;
    }

    @JsonProperty("list_planos")
    public void setUnidadeAtual(String unidadeJson) {
        this.unidadeAtual = unidadeJson == null || unidadeJson.trim().equalsIgnoreCase("false") ? null : pegarSiglaUnidade(unidadeJson);
    }

    @JsonProperty("email")
    public void setEmail(String emailJson) {
        this.email = emailJson == null ? null : removerEspacos(emailJson).toLowerCase();
    }

    @JsonProperty("nome_completo")
    public void setNome(String nomeJson) {
        this.nome = nomeJson == null ? null : removerEspacos(nomeJson).toUpperCase();
    }

    private String pegarSiglaUnidade(String unidadeJson) {
        if (unidadeJson.contains(SEPARADOR_EXTENSO_SIGLA)) {
            return removerEspacos(StringUtils.substringAfterLast(unidadeJson, SEPARADOR_EXTENSO_SIGLA)).toUpperCase();
        }
        return removerEspacos(unidadeJson).toUpperCase();
    }

    private String removerEspacos(String texto) {
        return StringUtils.normalizeSpace(texto);
    }

}
