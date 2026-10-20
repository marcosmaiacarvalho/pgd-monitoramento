package com.projetopgd.bancoeapipgd.services.responses;


import com.projetopgd.bancoeapipgd.dtos.responses.PaginaDashboardDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.ResumoPendenciasDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.UnidadePendenciasDTO;
import com.projetopgd.bancoeapipgd.entities.Plano;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PlanoService planoService;

    public PaginaDashboardDTO montarPaginaDashboard() {

        PaginaDashboardDTO paginaDashboard = new PaginaDashboardDTO();

        YearMonth inicio = DataUtils.getMesAnoLimiteInicio();               // 06/2024
        YearMonth mesPassado = DataUtils.getMesAnoLimiteFim();              // mês atual - 1
        YearMonth mesRetrasado = DataUtils.getMesAnoLimitePrazoAvaliacao(); // mês atual - 2

        // Donuts de mês único: cada um usa o seu próprio mês como corte,
        // então as fatias (sem pendência / chefia / servidor) somam o total do mês.
        paginaDashboard.setResumoMesPassado(montarResumoDoMes(mesPassado));
        paginaDashboard.setResumoMesRetrasado(montarResumoDoMes(mesRetrasado));

        // Total geral: acumulado de 06/2024 até o mês retrasado (mês atual - 2), corte ÚNICO.
        // Uma busca só alimenta o resumo E os dois rankings, todos no mesmo período.
        List<Plano> naoAvaliadosPeriodo = planoService.buscarPlanosNaoAvaliados(inicio, mesRetrasado);
        Map<Boolean, List<Plano>> naoAvaliadosPorStatusEntrega = naoAvaliadosPeriodo.stream().collect(Collectors.partitioningBy(Plano::estaIncompleto));

        Integer totalPeriodo = planoService.buscarQuantidadeDePlanos(inicio, mesRetrasado);
        String rotuloPeriodoCompleto = DataUtils.formatarMesAno(inicio) + " até " + DataUtils.formatarMesAno(mesRetrasado);

        paginaDashboard.setResumoPeriodoCompleto(montarResumo(naoAvaliadosPorStatusEntrega, totalPeriodo, rotuloPeriodoCompleto));

        paginaDashboard.setUnidadesComPendenciaServidor(montarTopUnidadesComPendencia(naoAvaliadosPorStatusEntrega.get(true)));
        paginaDashboard.setUnidadesComPendenciaChefia(montarTopUnidadesComPendencia(naoAvaliadosPorStatusEntrega.get(false)));

        return paginaDashboard;

    }

    // Monta o resumo (total + pendências) de UM mês específico.
    private ResumoPendenciasDTO montarResumoDoMes(YearMonth mes) {

        List<Plano> planosDoMes = planoService.buscarTodosPlanosPorVigencia(mes, mes); //mesmo mês nos dois lados = um mês só

        //true = incompletos, false = completos, remove TODOS os avaliados
        Map<Boolean, List<Plano>> naoAvaliadosPorStatusEntrega = planosDoMes.stream()
                .filter(p -> !p.getEstaAvaliado()).collect(Collectors.partitioningBy(Plano::estaIncompleto));

        return montarResumo(naoAvaliadosPorStatusEntrega, planosDoMes.size(), DataUtils.formatarMesAno(mes));
    }

    private ResumoPendenciasDTO montarResumo(Map<Boolean, List<Plano>> naoAvaliadosPorStatusEntrega, Integer qtdTotalDePlanos, String rotuloPeriodo) {

        Integer qtdPlanosIncompletos = naoAvaliadosPorStatusEntrega.get(true).size();
        Integer qtdPlanosCompletosNaoAvaliados = naoAvaliadosPorStatusEntrega.get(false).size();

        return new ResumoPendenciasDTO(qtdTotalDePlanos, qtdPlanosCompletosNaoAvaliados, qtdPlanosIncompletos, rotuloPeriodo);
    }

    private List<UnidadePendenciasDTO> montarTopUnidadesComPendencia(List<Plano> planos) {

        Map<String, Long> unidadesQtdPendencia = planos.stream().collect(Collectors.groupingBy(Plano::getSiglaUnidade, Collectors.counting()));

        return unidadesQtdPendencia.entrySet().stream().map(
                        entry -> new UnidadePendenciasDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(UnidadePendenciasDTO::getQtdPendencia, Comparator.reverseOrder()))
                .limit(5).toList();
    }

}
