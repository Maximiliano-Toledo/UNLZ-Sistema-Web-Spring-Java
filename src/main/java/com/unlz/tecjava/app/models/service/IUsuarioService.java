package com.unlz.tecjava.app.models.service;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.unlz.tecjava.app.exception.InvalidTokenException;
import com.unlz.tecjava.app.models.entity.Usuario;

/*
    IUsuarioService se extiende de UserDetailsService porque en SpringSecurityConfig
    se esta estableciendo en el metodo bean 'DaoAuthenticationProvider'. En el mismo
    se setea con el metodo .setUserDetailService(usuarioService).
    La interfaz UserDetailsService tiene un metodo importante que es loadUserByUsername
    permite buscar el usuario por su username (en este caso el email), y con el mismo
    puedo obtener sus roles.
*/
public interface IUsuarioService extends UserDetailsService{

    void save(Usuario usuario);

    List<Usuario> findAll();

    Page<Usuario> findAll(Pageable page);

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    void delete(Long id);

    Usuario fetchByIdWithFacturas(Long id);

    Usuario fetchByEmailWithFacturas(String email);

    void sendRegistrationConfirmationEmail(Usuario usuario);

    void verify(String token) throws InvalidTokenException;

    void registro(Usuario usuario);

    void activar(Usuario usuario);

    void desactivar(Usuario usuario);
}
