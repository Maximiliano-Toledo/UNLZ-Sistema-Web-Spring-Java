package com.unlz.tecjava.app.controllers;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.IUsuarioService;
import com.unlz.tecjava.app.util.paginator.PageRender;

@Controller
@RequestMapping("/usuarios")
@SessionAttributes("usuario")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    private static final String PATH_REDIRECT_USUARIOS_LISTAR = "redirect:/usuarios/listar";
    private static final String PATH_USUARIOS_LISTAR = "abm/usuarios/listar";
    private static final String PATH_USUARIOS_FORM = "abm/usuarios/form";
    private static final String PATH_USUARIOS_VER = "abm/usuarios/ver";

    @GetMapping("/listar")
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Usuario> usuarios = usuarioService.findAll(pageRequest);

        PageRender<Usuario> pageRender = new PageRender<>("/usuarios/listar/", usuarios);
        model.addAttribute("titulo", "Listado de Usuarios");
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("page", pageRender);

        return PATH_USUARIOS_LISTAR;
    }

    @GetMapping("/crear") // CREAR Usuario (GET)
    public String crear(Model model) {
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Formulario de Usuario");
        model.addAttribute("boton", "Crear Usuario");

        return PATH_USUARIOS_FORM;
    }

    @PostMapping("/crear") // GUARDAR Usuario (POST) @ModelAttribute("usuario")
    public String guardar(@Valid Usuario usuario, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

        if (usuario.getId() == null) {
            model.addAttribute("boton", "Crear usuario");
            model.addAttribute("titulo", "Formulario de Usuario");
        } else {
            model.addAttribute("boton", "Editar usuario");
            model.addAttribute("titulo", "Formulario de Usuario (Editar)");
        }

        if (result.hasErrors()) {
           return PATH_USUARIOS_FORM;
        }

        flash.addFlashAttribute("success", "Usuario guardado con exito!.");
        usuarioService.registro(usuario);
        status.setComplete();
        return PATH_REDIRECT_USUARIOS_LISTAR;
    }

    @GetMapping("/activar/{id}")
    public String activar(@PathVariable Long id, Model model, RedirectAttributes flash){

        Usuario usuario = usuarioService.findById(id);

        if(usuario == null){
            flash.addAttribute("error", "El usuario no existe en la base de datos.");
        }else{
            usuarioService.activar(usuario);
            flash.addAttribute("success", "El usuario activado con exito!");
        }


        return PATH_REDIRECT_USUARIOS_LISTAR;
    }

    @GetMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Usuario usuario = usuarioService.findById(id);

        if (usuario == null) {
            flash.addAttribute("error", "El usuario no existe en la base de datos.");
        }else{
            usuarioService.desactivar(usuario);
            flash.addAttribute("success", "El usuario desactivado con exito!");
        }


        return PATH_REDIRECT_USUARIOS_LISTAR;
    }

    @GetMapping("/editar/{id}") // EDITAR ARTICULO (GET)
    public String editar(@PathVariable Long id, Model model, RedirectAttributes flash) {

        Usuario usuario = null;

        if (id > 0) {
            usuario = usuarioService.findById(id);
            if (usuario == null) {
                flash.addFlashAttribute("error",
                        "El Usuario ID ".concat(id.toString()).concat(" no existe en la base de datos."));
                return PATH_REDIRECT_USUARIOS_LISTAR;
            }
        } else {
            flash.addFlashAttribute("error", "El ID del Usuario no puede ser 0.");
            return PATH_REDIRECT_USUARIOS_LISTAR;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Formulario de Usuario (Editar)");
        model.addAttribute("boton", "Editar Usuario");
        return PATH_USUARIOS_FORM;
    }

    @GetMapping("/eliminar/{id}") // ELIMINAR USUARIO (GET)
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash){

        if(id != null){
            flash.addFlashAttribute("success", "Usuario eliminado con exito!.");
            usuarioService.delete(id);
        }else{
            flash.addFlashAttribute("error", "El usuario que deseas eliminar no existe.");
        }
        return PATH_REDIRECT_USUARIOS_LISTAR;

    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes flash) {

        //Usuario usuario = usuarioService.findById(id);
        Usuario usuario = usuarioService.fetchByIdWithFacturas(id);

        if (usuario == null) {
            flash.addFlashAttribute("error", "El usuario no existe en la base de datos.");
            return PATH_REDIRECT_USUARIOS_LISTAR;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Detalle cliente " + usuario.getNombre() + " " + usuario.getApellido());

        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Perfil Usuario " + usuario.getId());

        return PATH_USUARIOS_VER;
    }

}
