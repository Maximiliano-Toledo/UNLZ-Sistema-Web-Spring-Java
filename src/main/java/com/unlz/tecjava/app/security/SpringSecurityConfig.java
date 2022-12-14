package com.unlz.tecjava.app.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.unlz.tecjava.app.models.service.IUsuarioService;

/*
    Clase de configuracion para Spring Security (autenticación, autorización y otras características de seguridad).
*/

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig implements AuthenticationSuccessHandler{

    /*
        Encriptador de Contraseñas
    */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
        Configuracion de Autorizaciones, permisos y accesos hacia las rutas
        de los controladores que estan mapeados dependiendo del rol del usuario.
        Basicamente se trata de validar los Requests.
    */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().antMatchers("/", "/uploads/**", "/css/**", "/js/**", "/images/**", "/tienda" , "/registro/**").permitAll()
                .antMatchers("/articulos/ver/**").hasAnyRole("USER")
                .antMatchers("/facturas/ver/**").hasAnyRole("USER")
                .antMatchers("/carro/**").hasAnyRole("USER")
                .antMatchers("/cartera/**").hasAnyRole("USER")
                .antMatchers("/articulos/**").hasAnyRole("ADMIN")
                .antMatchers("/usuarios/**").hasAnyRole("ADMIN")
                .antMatchers("/facturas/form/**").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .loginPage("/login")
                    .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // la ruta para hacer el logout
                .logoutSuccessUrl("/login?logout").permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/error_403");

        return http.build();
    }

    /*
        Es una implementacion centrada en el acceso a los datos que se encuentran almacenados
        dentro de una base de datos. Este proveedor específico requiere una atención especial.
        esta implementacion delega a su vez en un objeto de tipo UserDetailsService, una
        interfaz que define a un objeto de acceso a datos con un unico metodo: loadUserByUsername()
        que permite obtener la informacion de un usuario apartir de su nombre de usuario.

        En este metodo le inyectamos por parametro la clase servicio 'IUsuarioService'
        que implementa dicha interfaz (UserDetailsService)
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(IUsuarioService usuarioService) {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();

        auth.setUserDetailsService(usuarioService); // Extraer los datos de un usuario
        auth.setPasswordEncoder(passwordEncoder()); // Indicamos como vamos a codificar nuestra password

        return auth;
    }

    /*
        Metodo para configurar y registrar los usuarios de nuestro sistema
        de seguridad.
        Recibe por parametro el AuthenticactionManagerBuilder que sirve
        principalmente para registrar nuestros usuarios y validar su autenticacion.
    */
    @Autowired
    public void configure(AuthenticationManagerBuilder auth, IUsuarioService usuarioService) throws Exception{

        // Validar si es que los datos que estoy obteniendo en el metodo que creamos,
        // el authenticationProvider(), son validos o no.
         auth.authenticationProvider(authenticationProvider(usuarioService));


         PasswordEncoder encoder = new BCryptPasswordEncoder();
        /*
        *   Con UsersBuilder se pueden crear los usuarios y encriptar
        *   las contraseñas.
        */
         UserBuilder users = User.builder().passwordEncoder(encoder::encode);

        /*
         *  Sirve para crear usuarios por defecto en el backend
         *  de Spring. (En memoria)
         */
         auth.inMemoryAuthentication()
         .withUser(users.username("admin").password("admin").roles("ADMIN", "USER"))
         .withUser(users.username("maxi").password("12345").roles("USER"));

    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setUseReferer(true);

        return handler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {


        response.sendRedirect(request.getHeader("referer"));
    }



}
