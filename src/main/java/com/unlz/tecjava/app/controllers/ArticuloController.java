package com.unlz.tecjava.app.controllers;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.unlz.tecjava.app.models.entity.Articulo;
import com.unlz.tecjava.app.models.service.IArticuloService;
import com.unlz.tecjava.app.util.paginator.PageRender;

@Controller()
@RequestMapping("/articulos")
@SessionAttributes("articulo")
public class ArticuloController {

    @Autowired
    private IArticuloService articuloService;

    private final static String UPLOADS_FOLDER = "uploads";

    private static final String PATH_REDIRECT_ARTICULOS_LISTAR = "redirect:/articulos/listar";
    private static final String PATH_ARTICULOS_LISTAR = "abm/articulos/listar";
    private static final String PATH_ARTICULOS_FORM = "abm/articulos/form";
    private static final String PATH_ARTICULOS_VER = "abm/articulos/ver";

    private final Logger log = LoggerFactory.getLogger(getClass());

    // Opcional para no agregar el ResourceHandlers en el MvcConfig.
    // Se puede comentar todo este metodo.
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename){
        Path pathFoto = Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
        log.info("pathFoto filename:" + pathFoto);
        Resource resource = null;
        try {
            resource = new UrlResource(pathFoto.toUri());
            if(!resource.exists() && !resource.isReadable()){
                throw new RuntimeException("No se puede cargar la imagen "+pathFoto.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+filename+"\"")
                .body(resource);

        }

    @GetMapping({ "/listar", "/" })
    public String listar(@RequestParam(name="page", defaultValue = "0") int page, Model model){

        Pageable pageRequest = PageRequest.of(page, 4);
        Page<Articulo> articulos = articuloService.findAll(pageRequest);

        PageRender<Articulo> pageRender = new PageRender<>("/articulos/listar/", articulos);
        model.addAttribute("titulo", "Listado de Articulos");
        model.addAttribute("articulos", articulos);
        model.addAttribute("page", pageRender);
        return PATH_ARTICULOS_LISTAR;
    }

    @GetMapping("/crear") // CREAR ARTICULO (GET)
    public String crear(Model model){
        Articulo articulo = new Articulo();
        model.addAttribute("articulo", articulo);
        model.addAttribute("titulo", "Formulario de Articulo");
        model.addAttribute("boton", "Crear Articulo");

        return PATH_ARTICULOS_FORM;
    }

    @PostMapping("/crear") // GUARDAR ARTICULO (POST) @ModelAttribute("articulo")
    public String guardar(@Valid Articulo articulo, BindingResult result, Model model, RedirectAttributes flash,@RequestParam("file") MultipartFile foto,SessionStatus status){

        if (articulo.getId() == null){
            model.addAttribute("boton", "Crear Articulo");
            model.addAttribute("titulo", "Formulario de Articulo");
        } else {
            model.addAttribute("boton", "Editar Articulo");
            model.addAttribute("titulo", "Formulario de Articulo (Editar)");
        }

        if(!foto.isEmpty()){

            if(articulo.getId() != null && articulo.getId()>0 && articulo.getFoto()!= null && articulo.getFoto().length() > 0){
                Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(articulo.getFoto()).toAbsolutePath();
                File archivo = rootPath.toFile();

                if(archivo.exists() && archivo.canRead()){
                    archivo.delete();
                }
            }

            String uniqueFilename = UUID.randomUUID().toString().concat("_" + foto.getOriginalFilename());
            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(uniqueFilename);
            Path rootAbsolutPath = rootPath.toAbsolutePath();
            log.info("rootPath: "+rootPath);
            log.info("rootAbsolutPath: "+rootAbsolutPath);
            log.info("rootAbsolutPath: " + Paths.get(UPLOADS_FOLDER).toString());

            File dir = new File(Paths.get(UPLOADS_FOLDER).toString());

            if (!dir.exists()) {
               dir.mkdir();
            }

            try {
                // Para guardar dentro del proyecto por fuera
                Files.copy(foto.getInputStream(), rootAbsolutPath);

                //byte[] bytes = foto.getBytes();
                //Path completePath = Paths.get(rootPath + "//" + uniqueFilename);
                //Files.write(rootAbsolutPath, bytes);

                flash.addFlashAttribute("info",
                        "Haz subido correctamente el archivo '".concat(foto.getOriginalFilename() + "'"));

                articulo.setFoto(uniqueFilename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(result.hasErrors()){
            return PATH_ARTICULOS_FORM;
        }

        flash.addFlashAttribute("success", "Articulo guardado con exito!.");
        articuloService.save(articulo);
        status.setComplete();
        return PATH_REDIRECT_ARTICULOS_LISTAR;
    }

    @GetMapping("/eliminar/{id}") // ELIMINAR ARTICULO (GET)
    public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash){

        if(id > 0){
            Articulo articulo = articuloService.findById(id);
            flash.addFlashAttribute("success", "Articulo eliminado con exito!.");
            articuloService.delete(id);

            Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(articulo.getFoto()).toAbsolutePath();
            File archivo = rootPath.toFile();

            if(archivo.exists() && archivo.canRead()){
                if(archivo.delete()){
                    flash.addFlashAttribute("info", "Imagen del articulo eliminado con exito! ("+articulo.getFoto()+")");
                }
            }
            //return PATH_REDIRECT_ARTICULOS_LISTAR;
        }

        return PATH_REDIRECT_ARTICULOS_LISTAR;
    }

    @GetMapping("/editar/{id}") // EDITAR ARTICULO (GET)
    public String editar (@PathVariable Long id, Model model, RedirectAttributes flash){

        Articulo articulo = null;

        if(id > 0){
            articulo = articuloService.findById(id);
            if (articulo == null){
                flash.addFlashAttribute("error", "El Articulo ID ".concat(id.toString()).concat(" no existe en la base de datos."));
                return PATH_REDIRECT_ARTICULOS_LISTAR;
            }
        } else {
            flash.addFlashAttribute("error", "El ID del Articulo no puede ser 0");
            return PATH_REDIRECT_ARTICULOS_LISTAR;
        }

        model.addAttribute("articulo", articulo);
        model.addAttribute("titulo", "Formulario de Articulo (Editar)");
        model.addAttribute("boton", "Editar Articulo");
        return PATH_ARTICULOS_FORM;
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes flash){
        Articulo articulo = articuloService.findById(id);

        if(articulo==null){
            flash.addFlashAttribute("error", "El articulo no existe en la base de datos");
            return PATH_REDIRECT_ARTICULOS_LISTAR;
        }

        model.addAttribute("articulo", articulo);
        model.addAttribute("titulo", "Perfil Articulo " + articulo.getId());

        return PATH_ARTICULOS_VER;
    }

}
