package com.projetopgd.bancoeapipgd.repositories;

import com.projetopgd.bancoeapipgd.entities.Plano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface PlanoRepository extends JpaRepository<Plano, Long> {


    List<Plano> findByUsuarioIdOrderByInicioVigenciaDesc(Long id);

    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario
            WHERE (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> findByVigencia(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    //Busca planos COMPLETOS + NÃO AVALIADOS
    //Ignora unidades INATIVAS
    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario
            WHERE p.tempoEntregue >= p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPorVigenciaNaoAvaliados(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    //Busca INCOMPLETOS + NÃO AVALIADOS
    //Incompletos avaliados não são considerados
    //Ignora unidades INATIVAS
    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario
            WHERE p.tempoEntregue < p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPorVigenciaIncompletos(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);
}
