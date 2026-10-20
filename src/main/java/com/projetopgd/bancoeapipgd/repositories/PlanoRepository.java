package com.projetopgd.bancoeapipgd.repositories;

import com.projetopgd.bancoeapipgd.entities.Plano;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PlanoRepository extends JpaRepository<Plano, Long> {

    List<Plano> findByUsuarioIdOrderByInicioVigenciaDesc(Long id);

    //Busca planos COMPLETOS + NÃO AVALIADOS
    //Ignora unidades INATIVAS
    //UTILIZADO PARA EXPORTAR EM CSV
    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario
            WHERE p.tempoEntregue >= p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPorVigenciaCompletosNaoAvaliados(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    //Busca INCOMPLETOS + NÃO AVALIADOS
    //Incompletos avaliados não são considerados
    //Ignora unidades INATIVAS
    //UTILIZADO PARA EXPORTAR EM CSV
    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario
            WHERE p.tempoEntregue < p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPorVigenciaIncompletosNaoAvaliados(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);


    //BUSCO TODOS OS PLANOS (COMPLETOS OU NÃO) QUE NÃO ESTEJAM AVALIADOS
    //MUITO UTILIZADO PRA SEPARAR IN MEMORY
    @Query("""
            SELECT p FROM Plano p
            WHERE p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPorVigenciaTodosNaoAvaliados(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    // ========================================================================
    // // INCOMPLETOS EM 2 ETAPAS (UTILIZANDO IDS DOS USUÁRIOS)
    // ========================================================================

    @Query("""
            SELECT u.id FROM Plano p
            JOIN p.usuario u
            WHERE p.tempoEntregue < p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            GROUP BY u.id, u.nome
            ORDER BY u.nome
            """)
    Page<Long> buscarIdsUsuariosPaginadosPorPlanosIncompletos(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, Pageable pageable);

    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario u
            WHERE u.id IN :idsUsuarios
            AND p.tempoEntregue < p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPlanosIncompletosPorIdsUsuarios(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, List<Long> idsUsuarios);

    // ========================================================================
    // NÃO AVALIADOS EM 2 ETAPAS (UTILIZANDO SIGLAS DAS UNIDADES)
    // ========================================================================

    @Query("""
            SELECT p.siglaUnidade FROM Plano p
            WHERE p.tempoEntregue >= p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            GROUP BY p.siglaUnidade
            ORDER BY p.siglaUnidade
            """)
    Page<String> buscarSiglasUnidadesPaginadasPorPlanosNaoAvaliados(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, Pageable pageable);

    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario u
            WHERE p.siglaUnidade IN :siglasUnidades
            AND p.tempoEntregue >= p.tempoProporcional
            AND p.estaAvaliado = false
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPlanosNaoAvaliadosPorSiglasUnidades(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, List<String> siglasUnidades);

    // ========================================================================
    // TODOS OS PLANOS INDEPENDENTE DA CONDIÇÃO (AVALIADOS OU NÃO) POR PERÍODO EM 2 ETAPAS
    // ========================================================================

    @Query("""
            SELECT u.id FROM Plano p
            JOIN p.usuario u
            WHERE (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            GROUP BY u.id, u.nome
            ORDER BY u.nome
            """)
    Page<Long> buscarIdsUsuariosPaginadosPorPeriodo(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, Pageable pageable);

    @Query("""
            SELECT p FROM Plano p
            JOIN FETCH p.usuario u
            WHERE u.id IN :idsUsuarios
            AND (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarTodosOsPlanosPorIdsUsuarios(LocalDate dataInicioVigencia, LocalDate dataFimVigencia, List<Long> idsUsuarios);

    @Query("""
            SELECT p FROM Plano p
            WHERE (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    List<Plano> buscarPlanosPorVigencia(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    @Query("""
            SELECT COUNT(*)
            FROM Plano p
            WHERE (p.inicioVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND (p.fimVigencia BETWEEN :dataInicioVigencia AND :dataFimVigencia)
            AND p.siglaUnidade NOT LIKE '%INATIV%'
            """)
    Integer buscarQuantidadeDePlanos(LocalDate dataInicioVigencia, LocalDate dataFimVigencia);

    //DELETA planos que foram desativados no ANTT Pró.
    //Necessário pois deleta lixo que pode ter ficado no banco, pois o ddl-auto=update.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Plano p WHERE p.sincronizadoEm < :momentoDaSincronizacao")
    int deletarPlanosExcluidos(LocalDateTime momentoDaSincronizacao);
}
