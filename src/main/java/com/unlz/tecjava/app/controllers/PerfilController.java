package com.unlz.tecjava.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.IUsuarioService;

@Controller
@RequestMapping("/perfil")
@SessionAttributes("usuario")
public class PerfilController {

    @Autowired
    private IUsuarioService usuarioService;

    private final String PATH_PERFIL_VER = "perfil/ver";
    private final String PATH_PERFIL_FACTURAS = "perfil/facturas";
    private final String PATH_REDIRECT_PERFIL_FACTURAS = "redirect:/perfil";

    public Authentication contextAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping()
    public String perfil(Model model){

        String emailUser = contextAuth().getName();
        Usuario usuario = usuarioService.findByEmail(emailUser);

        usuarioService.findByEmail(emailUser);
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Perfil: "+ emailUser);

        return PATH_PERFIL_VER;
    }

    @GetMapping("/facturas")
    public String facturas(Model model) {

        String emailUser = contextAuth().getName();
        Usuario usuario = usuarioService.findByEmail(emailUser);

        usuario = usuarioService.fetchByIdWithFacturas(usuario.getId());

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Facturas: " + emailUser);

        return PATH_PERFIL_FACTURAS;
    }

    @GetMapping("/cartera")
    public String cartera(Model model) {

        String emailUser = contextAuth().getName();
        Usuario usuario = usuarioService.findByEmail(emailUser);

        model.addAttribute("titulo", "Cartera: "+ emailUser);
        model.addAttribute("usuario", usuario);

        return "cartera";
    }

    @PostMapping("/cartera")
    public String guardarDinero(Usuario usuario, Model model, SessionStatus status) {

        String emailUser = contextAuth().getName();

        usuarioService.save(usuario);

        status.setComplete();

        return PATH_REDIRECT_PERFIL_FACTURAS;
    }





}
