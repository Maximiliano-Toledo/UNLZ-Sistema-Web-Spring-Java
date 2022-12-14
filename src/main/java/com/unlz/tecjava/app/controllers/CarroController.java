package com.unlz.tecjava.app.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unlz.tecjava.app.exception.OutOfStockArticuloException;
import com.unlz.tecjava.app.models.entity.Articulo;

import com.unlz.tecjava.app.models.service.IArticuloService;
import com.unlz.tecjava.app.models.service.ICarroService;

@ControllerAdvice
@RequestMapping("/carro")
public class CarroController {

    @Autowired
    private IArticuloService articuloService;

    @Autowired
    private ICarroService carroService;

    private static final String PATH_REDIRECT_TIENDA = "redirect:/tienda";
    private static final String PATH_REDIRECT_CARRO = "redirect:/carro";
    private static final String PATH_CARRO = "carro";
    private static final String PATH_COMPRA_SUCCESS = "compra_realizada";

    public Authentication contextAuth(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @ModelAttribute("carroelementos")
    public int carroelementos() {
        return carroService.getArticulosCarro().size();
    }

    @ModelAttribute("elementos")
    public int elementos() {
        return carroService.getElementos();
    }

    @GetMapping({"/", ""})
    public String carro(Model model){

        if(contextAuth().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
            return PATH_REDIRECT_TIENDA;
        }

        String emailUser = contextAuth().getName();

        model.addAttribute("titulo", "Carro: " + emailUser);
        model.addAttribute(PATH_CARRO, carroService.getArticulosCarro());

        return PATH_CARRO;
    }


    @GetMapping("/eliminarCompletamenteArticuloCarro/{articuloId}")
    public String carroEliminarCompletamenteArticulo(@PathVariable("articuloId") Long articuloId, RedirectAttributes flash, Model model) {

        if (contextAuth().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return PATH_REDIRECT_TIENDA;
        }

        Articulo articulo = articuloService.findById(articuloId);

        if (articulo == null) {
            flash.addFlashAttribute("error", "El articulo que desea eliminar completamente del carro NO existe.");
            return PATH_REDIRECT_CARRO;
        }

        flash.addFlashAttribute("success",
                "El articulo ".concat(articulo.getNombre()).concat(" fue eliminado completamente con exito!"));
        carroService.completeDeleteFromCarro(articulo);

        return PATH_REDIRECT_CARRO;
    }

    @GetMapping("/quitar/{articuloId}")
    public String carroQuitarArticulo(@PathVariable("articuloId") Long articuloId, RedirectAttributes flash,
            Model model) {

        if (contextAuth().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return PATH_REDIRECT_TIENDA;
        }

        Articulo articulo = articuloService.findById(articuloId);

        if (articulo == null) {
            flash.addFlashAttribute("error", "El articulo que desea eliminar al carro NO existe.");
            return PATH_REDIRECT_CARRO;
        }

        flash.addFlashAttribute("success",
                "El articulo ".concat(articulo.getNombre()).concat(" fue eliminado con exito!"));
        carroService.removeArticulo(articulo);

        return PATH_REDIRECT_CARRO;
    }

    @GetMapping("/agregar/{articuloId}")
    public String carroAgregarArticulo(@PathVariable("articuloId") Long articuloId, RedirectAttributes flash, Model model) {

        if (contextAuth().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return PATH_REDIRECT_TIENDA;
        }

        Articulo articulo = articuloService.findById(articuloId);

        if(articulo == null){
            flash.addFlashAttribute("error", "El articulo que desea agregar al carro NO existe.");
            return PATH_REDIRECT_CARRO;
        }

        if(articulo.getStock() == 0){
            flash.addFlashAttribute("error", "No hay stock del articulo "+articulo.getNombre());
            return PATH_REDIRECT_TIENDA;
        }

        try {
            carroService.addArticulo(articulo);
        } catch (OutOfStockArticuloException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return PATH_REDIRECT_CARRO;
        }

        flash.addFlashAttribute("success",
                "El articulo ".concat(articulo.getNombre()).concat(" fue agregado con exito!"));


        return PATH_REDIRECT_CARRO;
    }


    @GetMapping("/pagar")
    public String pagar(Model model, RedirectAttributes flash){

        if (contextAuth().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return PATH_REDIRECT_TIENDA;
        }

        if(carroService.getUnmodifiablemapArticulosCarro().isEmpty()){
            flash.addFlashAttribute("warning", "El carro esta vacio.");
            return PATH_REDIRECT_TIENDA;
        }

        try {
            carroService.pagar();
        } catch (NullPointerException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return PATH_REDIRECT_TIENDA;
        } catch (OutOfStockArticuloException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return PATH_REDIRECT_TIENDA;
        }

        return PATH_COMPRA_SUCCESS;
    }

    @GetMapping("/vaciar")
    public String vaciarCarro(Model model, RedirectAttributes flash){

        if(!carroService.getUnmodifiablemapArticulosCarro().isEmpty()){
            carroService.clearArticulos();
            flash.addFlashAttribute("success", "El carro se vacío correctamente!");
        } else {
            flash.addFlashAttribute("warning", "El carro ya esta vacío.");
        }

        return PATH_REDIRECT_CARRO;
    }

}