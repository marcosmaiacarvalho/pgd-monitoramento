package com.projetopgd.bancoeapipgd.controllers.rest;


import com.projetopgd.bancoeapipgd.dtos.responses.PaginaDashboardDTO;
import com.projetopgd.bancoeapipgd.services.responses.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rest/dashboard")
public class DashboardRestController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<PaginaDashboardDTO> buscarDashboard() {
        return ResponseEntity.ok(dashboardService.montarPaginaDashboard());
    }
}
