package com.projetopgd.bancoeapipgd.dtos.imports;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanoImportDTO {

    private static final String SEPARADOR_DATA_HORA = " ";
    private static final String DATA_INVALIDA = "0000-00-00";


    @JsonProperty("id_plano")
    private Long id;

    @Setter(AccessLevel.NONE)
    private UsuarioImportDTO usuario;

    private Long idUnidade;

    private String siglaUnidade;

    @Setter(AccessLevel.NONE)
    private Boolean estaAvaliado;

    private Double tempoTotal; //horas brutas do mês

    private Double tempoProporcional; //após afastamentos registrados

    private Double tempoPactuado; //prometido pelo servidor no início do mês

    private Double tempoEntregue; //efetivamente entregue pelo servidor

    @Setter(AccessLevel.NONE)
    private LocalDate inicioVigencia;

    @Setter(AccessLevel.NONE)
    private LocalDate fimVigencia;

    @Setter(AccessLevel.NONE)
    private LocalDate homologacao;

    @Setter(AccessLevel.NONE)
    private LocalDate arquivamento;

    @JsonProperty("homologado")
    private Boolean estaHomologado;


    @JsonProperty("id_user")
    public void setUsuario(Long idUsuarioJson) {
        if (idUsuarioJson == null) {
            this.usuario = null;
        } else {
            UsuarioImportDTO usuario = new UsuarioImportDTO();
            usuario.setId(idUsuarioJson);
            this.usuario = usuario;
        }
    }

    @JsonProperty("avaliacao_plano")
    public void setEstaAvaliado(JsonNode node) {
        this.estaAvaliado = !node.isBoolean(); //no JSON, quando avaliado, vira um array
    }

    @JsonProperty("data_inicio_vigencia")
    public void setInicioVigencia(String inicioVigenciaJson) {
        this.inicioVigencia = converterStringEmData(StringUtils.substringBefore(inicioVigenciaJson, SEPARADOR_DATA_HORA));

    }

    @JsonProperty("data_fim_vigencia")
    public void setFimVigencia(String fimVigenciaJson) {
        this.fimVigencia = converterStringEmData(StringUtils.substringBefore(fimVigenciaJson, SEPARADOR_DATA_HORA));

    }

    @JsonProperty("data_homologacao")
    public void setHomologacao(String homologacaoJson) {
        this.homologacao = converterStringEmData(StringUtils.substringBefore(homologacaoJson, SEPARADOR_DATA_HORA));

    }

    @JsonProperty("data_arquivamento")
    public void setArquivamento(String arquivamentoJson) {
        this.arquivamento = converterStringEmData(StringUtils.substringBefore(arquivamentoJson, SEPARADOR_DATA_HORA));

    }


    private LocalDate converterStringEmData(String dataEmTexto) {
        if (StringUtils.isBlank(dataEmTexto) || StringUtils.equals(dataEmTexto, DATA_INVALIDA)) {
            return null;
        }

        return LocalDate.parse(dataEmTexto);
    }

}
