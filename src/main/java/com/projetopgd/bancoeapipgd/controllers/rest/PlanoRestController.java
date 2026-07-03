package com.projetopgd.bancoeapipgd.controllers.rest;


import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UnidadeUsuariosDTO;
import com.projetopgd.bancoeapipgd.dtos.responses.aggregates.UsuarioUnidadesDTO;
import com.projetopgd.bancoeapipgd.services.responses.PlanoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/planos")
public class PlanoRestController {

    private final PlanoService planoService;

    @GetMapping("por-servidor/{matricula}")
    public ResponseEntity<List<UsuarioUnidadesDTO>> buscarPorMatricula(@PathVariable String matricula) {
        List<UsuarioUnidadesDTO> planosDTO = planoService.buscarPlanosPorServidor(matricula);
        return ResponseEntity.ok(planosDTO);
    }

    @GetMapping("/por-periodo")
    public ResponseEntity<List<UsuarioUnidadesDTO>> buscarPorPeriodo(@RequestParam(required = false) YearMonth inicioVigencia,
                                                                     @RequestParam(required = false) YearMonth fimVigencia) {

        List<UsuarioUnidadesDTO> planosDTO = planoService.buscarPlanosPorPeriodo(inicioVigencia, fimVigencia);
        return ResponseEntity.ok(planosDTO);
    }

    @GetMapping("/pendencia-chefia")
    public ResponseEntity<Page<UnidadeUsuariosDTO>> buscarPlanosCompletosNaoAvaliados(@RequestParam(required = false) YearMonth inicioVigencia,
                                                                                      @RequestParam(required = false) YearMonth fimVigencia,
                                                                                      Pageable pageable) {
        Page<UnidadeUsuariosDTO> unidadeUsuariosDTO = planoService.buscarPlanosNaoAvaliadosPaginados(inicioVigencia, fimVigencia, pageable);
        return ResponseEntity.ok(unidadeUsuariosDTO);
    }

    @GetMapping("/pendencia-servidor")
    public ResponseEntity<Page<UsuarioUnidadesDTO>> buscarPlanosIncompletos(@RequestParam(required = false) YearMonth inicioVigencia,
                                                                            @RequestParam(required = false) YearMonth fimVigencia,
                                                                            Pageable pageable) {
        Page<UsuarioUnidadesDTO> planosDTO = planoService.buscarPlanosIncompletosPaginados(inicioVigencia, fimVigencia, pageable);
        return ResponseEntity.ok(planosDTO);
    }


}
