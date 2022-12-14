package com.unlz.tecjava.app;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer{

    @Override
    public void addViewControllers(ViewControllerRegistry registry){

        registry.addViewController("/error_403").setViewName("error_403");

    }

    // Mapeo de ruta URL/PATH para guardar las fotos de los articulos/usuarios
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);

        String resourcePath = Paths.get("uploads").toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/**") // Ruta URL que va a estar mapeado al dir. fisico.
                //.addResourceLocations("file:/C:/temp/uploads/"); // Configuramos el directorio como estatico con ruta url que se pueda ver en la vista y se pueda acceder a la imagen mediante el nombre de la foto
                .addResourceLocations(resourcePath);
    }

}
