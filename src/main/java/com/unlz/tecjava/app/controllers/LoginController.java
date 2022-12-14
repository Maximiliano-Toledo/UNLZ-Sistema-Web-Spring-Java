package com.unlz.tecjava.app.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unlz.tecjava.app.models.service.ICarroService;

@Controller
public class LoginController {

    @Autowired
    private ICarroService carroService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout, Model model, Principal principal, RedirectAttributes flash){

           if(principal != null){
               flash.addFlashAttribute("info", "Ya ha iniciado sesion anteriormente");
               return "redirect:/tienda/";
           }

           if(error != null) {
               model.addAttribute("error", "El Nombre o la Contraseña son incorrectos. Por favor, vuelva a intentar.");
           }

           if (logout != null) {
               carroService.clearArticulos();
               model.addAttribute("success", "Has cerrado sesión con éxito!");
           }

        return "login";
    }
}
