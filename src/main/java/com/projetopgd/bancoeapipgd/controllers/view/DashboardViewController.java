package com.projetopgd.bancoeapipgd.controllers.view;

import com.projetopgd.bancoeapipgd.services.responses.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/view/dashboard")
public class DashboardViewController {

    private final DashboardService dashboardService;

    @GetMapping
    public String exibirDashboard(Model model) {
        model.addAttribute("dadosDashboard", dashboardService.montarPaginaDashboard());
        return "dashboard";
    }
}
