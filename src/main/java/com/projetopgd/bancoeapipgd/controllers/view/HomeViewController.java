package com.projetopgd.bancoeapipgd.controllers.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/view/painel")
public class HomeViewController {


    @GetMapping
    public String index(Model model) {
        return "index";
    }

}
