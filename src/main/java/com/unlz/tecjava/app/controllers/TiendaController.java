package com.unlz.tecjava.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestParam;

import com.unlz.tecjava.app.models.entity.Articulo;
import com.unlz.tecjava.app.models.service.IArticuloService;
import com.unlz.tecjava.app.util.paginator.PageRender;

@Controller
public class TiendaController {

    @Autowired
    private IArticuloService articuloService;

    @GetMapping("/tienda")
    public String index(@RequestParam(name = "page", defaultValue = "0") int page, Model model){

        Pageable pageRequest = PageRequest.of(page, 3);
        Page<Articulo> articulos = articuloService.findAll(pageRequest);

        PageRender<Articulo> pageRender = new PageRender<>("/usuarios/listar/", articulos);
        model.addAttribute("titulo", "Tienda");
        model.addAttribute("articulos", articulos);
        model.addAttribute("page", pageRender);


        return "tienda";
    }

}
