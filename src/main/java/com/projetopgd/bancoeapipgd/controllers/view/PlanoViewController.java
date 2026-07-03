package com.projetopgd.bancoeapipgd.controllers.view;

import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UnidadeUsuariosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UsuarioUnidadesDTO;
import com.projetopgd.bancoeapipgd.services.responses.PlanoService;
import com.projetopgd.bancoeapipgd.utils.DataUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/planos")
public class PlanoViewController {

    private final PlanoService planoService;

    @GetMapping("/por-servidor")
    public String porServidor(@RequestParam(required = false) String matricula, Model model) {
        if (matricula != null) {
            List<UsuarioUnidadesDTO> planosDTO = planoService.buscarPlanosPorServidor(matricula);
            model.addAttribute("dadosBackend", planosDTO);
        }
        return "por-servidor";
    }

    @GetMapping("/filtro-periodo")
    public String filtroPeriodo(Model model, @RequestParam("tipoConsulta") String tipoConsulta) {

        String endpointDestino = switch (tipoConsulta) {
            case "por-periodo" -> "/view/planos/por-periodo";
            case "pendencia-servidor" -> "/view/planos/pendencia-servidor";
            case "pendencia-chefia" -> "/view/planos/pendencia-chefia";
            default -> "";
        };

        model.addAttribute("endpointDestino", endpointDestino);
        model.addAttribute("minPeriodo", DataUtils.getMesAnoLimiteInicio());
        model.addAttribute("maxPeriodo", DataUtils.getMesAnoLimiteFim());

        return "filtro-periodo";
    }

    @GetMapping("/por-periodo")
    public String buscarPorPeriodo(@RequestParam(required = false) YearMonth inicioVigencia,
                                   @RequestParam(required = false) YearMonth fimVigencia,
                                   Model model) {

        List<UsuarioUnidadesDTO> planosDTO = planoService.buscarPlanosPorPeriodo(inicioVigencia, fimVigencia);
        model.addAttribute("dadosBackend", planosDTO);
        return "por-periodo";
    }

    @GetMapping("/pendencia-servidor")
    public String buscarPorPendenciaServidor(@RequestParam(required = false) YearMonth inicioVigencia,
                                             @RequestParam(required = false) YearMonth fimVigencia,
                                             Model model, Pageable pageable) {

        Page<UsuarioUnidadesDTO> pagina = planoService.buscarPlanosIncompletosPaginados(inicioVigencia, fimVigencia, pageable);
        model.addAttribute("pagina", pagina);
        return "pendencia-servidor";
    }

    @GetMapping("/pendencia-chefia")
    public String buscarPorPendenciaChefia(@RequestParam(required = false) YearMonth inicioVigencia,
                                           @RequestParam(required = false) YearMonth fimVigencia,
                                           Model model, Pageable pageable) {

        Page<UnidadeUsuariosDTO> pagina = planoService.buscarPlanosNaoAvaliadosPaginados(inicioVigencia, fimVigencia, pageable);
        model.addAttribute("pagina", pagina);
        return "pendencia-chefia";
    }

    @GetMapping(value = "/pendencia-chefia", params = "format=csv")
    public void exportarPendenciaChefia(@RequestParam(required = false) YearMonth inicioVigencia,
                                        @RequestParam(required = false) YearMonth fimVigencia,
                                        HttpServletResponse response) {

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=pendencias-chefia.csv");

        try {
            PrintWriter writer = response.getWriter();
            writer.write('\uFEFF');

            planoService.exportarNaoAvaliadosParaCsv(inicioVigencia, fimVigencia, writer);

        } catch (IOException e) {
            throw new RuntimeException("Erro de I/O ao tentar enviar o arquivo CSV para o navegador.");
        }

    }


}
