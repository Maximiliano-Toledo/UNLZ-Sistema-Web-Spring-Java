package com.unlz.tecjava.app.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.unlz.tecjava.app.exception.InvalidTokenException;
import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.IUsuarioService;
import com.unlz.tecjava.app.security.token.Token;
import com.unlz.tecjava.app.security.token.service.ITokenService;

@Controller
@RequestMapping("/registro")
@SessionAttributes("usuario")
public class RegistroController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private ITokenService tokenService;

    private static final String PATH_REGISTRO_MAIL = "registro/mail";
    private static final String PATH_REGISTRO_FORM = "registro/form";

    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @GetMapping("/form")
    public String mostrarFormularioDeRegistro(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Registro");
        return PATH_REGISTRO_FORM;
    }

    @PostMapping("/form")
    public String registrarCuentaDeUsuario(@Valid Usuario usuario, BindingResult result, Model model, SessionStatus status) {

        Usuario usuario_email = usuarioService.findByEmail(usuario.getEmail());

        if (usuario_email != null){
            model.addAttribute("error", "El email " + usuario_email.getEmail() + " ya existe en el sistema.");
            return PATH_REGISTRO_FORM;
        }

        if (result.hasErrors()) {
            model.addAttribute("titulo", "Registro");
            return PATH_REGISTRO_FORM;
        }

        model.addAttribute("titulo", "Se ha enviado un mail de confirmacion.");
        usuarioService.registro(usuario);
        usuarioService.sendRegistrationConfirmationEmail(usuario);

        model.addAttribute("email", usuario.getEmail());

        status.setComplete();

        return PATH_REGISTRO_MAIL;
    }


    @GetMapping("/verificacion")
    public String verificacion(@RequestParam(name = "t", required = false) String tokenValue, Model model) throws InvalidTokenException{

        Token token = tokenService.findByToken(tokenValue);
        Usuario usuario = null;

        if(token != null){
            usuario = token.getUsuario();
        }

        try {
            usuarioService.verify(tokenValue);
        } catch (InvalidTokenException e) {
            model.addAttribute("titulo", "Token invalido.");
            model.addAttribute("verified", false);
            return "registro/verificacion";
        }

        if (usuario != null) {
            model.addAttribute("email", usuario.getEmail());
        }

        model.addAttribute("titulo", "Cuenta verificada con exito!");
        model.addAttribute("verified", true);

        return "registro/verificacion";
    }

}
