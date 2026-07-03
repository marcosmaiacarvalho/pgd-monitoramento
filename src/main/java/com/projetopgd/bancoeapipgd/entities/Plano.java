package com.projetopgd.bancoeapipgd.entities;


import com.projetopgd.bancoeapipgd.dtos.imports.PlanoImportDTO;
import com.projetopgd.bancoeapipgd.enums.StatusExecucao;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "tb_planos")
public class Plano {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String siglaUnidade;

    private Boolean estaAvaliado;

    private Double tempoTotal; //horas brutas do mês

    private Double tempoProporcional; //após afastamentos registrados

    private Double tempoPactuado; //prometido pelo servidor

    private Double tempoEntregue; //efetivamente entregue pelo servidor

    private LocalDate inicioVigencia;

    private LocalDate fimVigencia;

    private LocalDate homologacao;

    private LocalDate arquivamento;

    private Boolean estaHomologado;

    public Plano(PlanoImportDTO dto) {
        this.id = dto.getId();
        this.usuario = new Usuario(dto.getUsuario().getId());
        this.siglaUnidade = dto.getSiglaUnidade();
        this.estaAvaliado = dto.getEstaAvaliado();
        this.tempoTotal = dto.getTempoTotal();
        this.tempoProporcional = dto.getTempoProporcional();
        this.tempoPactuado = dto.getTempoPactuado();
        this.tempoEntregue = dto.getTempoEntregue();
        this.inicioVigencia = dto.getInicioVigencia();
        this.fimVigencia = dto.getFimVigencia();
        this.homologacao = dto.getHomologacao();
        this.arquivamento = dto.getArquivamento();
        this.estaHomologado = dto.getEstaHomologado();
    }

    public Double calcularSaldoDeHoras() {
        return tempoEntregue - tempoProporcional;
    }

    public Double calcularPercentualEntregue() {
        if (tempoProporcional == null || tempoProporcional == 0.0) {
            return 0.0; //Possível 100% caso tempoProporcional == 0.0, pois se prevê 0.0 e fez 0.0, 100% entregue.
        }
        Double percentualEntregue = tempoEntregue / tempoProporcional * 100;
        return Math.floor(percentualEntregue * 10.0) / 10.0;
    }

    public StatusExecucao verificarStatusExecucao() {

        if (tempoProporcional == 0.0) {
            return StatusExecucao.AFASTAMENTO_INTEGRAL;
        }

        Double saldoDeHoras = this.calcularSaldoDeHoras();

        if (saldoDeHoras == 0.0) {
            return StatusExecucao.META_CUMPRIDA_EXATA;
        } else if (saldoDeHoras > 0.0) {
            return StatusExecucao.HORAS_EXCEDENTES;
        } else {
            return StatusExecucao.DEVENDO_HORAS;
        }
    }

}
