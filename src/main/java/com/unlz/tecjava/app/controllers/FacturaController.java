package com.unlz.tecjava.app.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.unlz.tecjava.app.models.entity.Articulo;
import com.unlz.tecjava.app.models.entity.Factura;
import com.unlz.tecjava.app.models.entity.ItemFactura;
import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.IArticuloService;
import com.unlz.tecjava.app.models.service.IFacturaService;
import com.unlz.tecjava.app.models.service.IUsuarioService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IArticuloService articuloService;

    @Autowired
    private IFacturaService facturaService;

    private static final String PATH_REDIRECT_USUARIOS_LISTAR = "redirect:/usuarios/listar";
    private static final String PATH_FACTURA_FORM = "factura/form";
    private static final String PATH_FACTURA_VER = "factura/ver";

    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/form/{usuarioId}")
    public String crear(@PathVariable("usuarioId") Long usuarioId, Model model, RedirectAttributes flash){

        Usuario usuario = usuarioService.findById(usuarioId);

        if(usuario == null) {
            flash.addFlashAttribute("error", "El usuario no existe en la base de datos.");
            return PATH_REDIRECT_USUARIOS_LISTAR;
        }

        Factura factura = new Factura();
        factura.setUsuario(usuario);

        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Crear Factura");

        return PATH_FACTURA_FORM;
    }

    @GetMapping(value="/cargar-articulos/{term}",produces = { "application/json" })
    public @ResponseBody List<Articulo> cargarArticulos(@PathVariable("term") String term){

        return articuloService.findByNombre(term);

        // El resultado es convertido a un json y lo registra dentro del body
        // de la respuesta.
    }

    @PostMapping("/form")
    public String guardar(Factura factura, Model model,
            @RequestParam(name = "item_id[]", required = false) Long[] item_id,
            @RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, RedirectAttributes flash,
            SessionStatus status) {

        if (factura.getDescripcion().isEmpty() || factura.getDescripcion() == null) {
            model.addAttribute("error", "Error: La factura no puede tener la descripcion vacia.");
            model.addAttribute("titulo", "Crear Factura");
            return PATH_FACTURA_FORM;
        }

        if (item_id == null || item_id.length == 0) {
            model.addAttribute("error", "Error: La factura no puede no tener lineas.");
            model.addAttribute("titulo", "Crear Factura");
            return PATH_FACTURA_FORM;
        }

        for (int i = 0; i < item_id.length; i++) {
            Articulo articulo = articuloService.findById(item_id[i]);
            log.info("key:" + articulo);

            ItemFactura linea = new ItemFactura();
            linea.setCantidad(cantidad[i]);
            linea.setArticulo(articulo.getNombre());
            linea.setPrecio(articulo.getPrecio());
            log.info("linea:" + linea);
            factura.addItemFactura(linea);
            log.info("factura:" + factura);
            log.info("ID" + item_id[i].toString() + ", cantidad: " + cantidad[i].toString());
        }

        facturaService.save(factura);
        status.setComplete();
        flash.addFlashAttribute("success", "Factura creada con exito!");

        return "redirect:/usuarios/ver/" + factura.getUsuario().getId();
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        Factura factura = facturaService.findById(id);

        if (factura != null) {
            facturaService.delete(id);
            flash.addFlashAttribute("success", "Factura Eliminada con Exito!");
            return "redirect:/usuarios/ver/" + factura.getUsuario().getId();
        }

        flash.addFlashAttribute("error", "La factura no existe en la base de datos, no se pudo eliminar.");

        return PATH_REDIRECT_USUARIOS_LISTAR;
    }


    @GetMapping("/ver/{id}")
    public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash) {

        Factura factura = facturaService.fetchFacturaByIdWithUsuarioWithItemFacturaWithArticulo(id); // clienteService.findFacturaById(id);

        if (factura == null) {
            flash.addFlashAttribute("error", "Error: La factura no existe en la base de datos");
            return PATH_REDIRECT_USUARIOS_LISTAR;
        }

        model.addAttribute("factura", factura);
        model.addAttribute("titulo", "Factura: ".concat(factura.getDescripcion()));

        return PATH_FACTURA_VER;
    }



}
