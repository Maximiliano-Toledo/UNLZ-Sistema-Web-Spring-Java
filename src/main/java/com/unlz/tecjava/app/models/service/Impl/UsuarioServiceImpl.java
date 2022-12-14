package com.unlz.tecjava.app.models.service.Impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlz.tecjava.app.exception.InvalidTokenException;
import com.unlz.tecjava.app.models.dao.IUsuarioDao;
import com.unlz.tecjava.app.models.entity.Rol;
import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.EmailService;
import com.unlz.tecjava.app.models.service.IUsuarioService;
import com.unlz.tecjava.app.security.token.Token;
import com.unlz.tecjava.app.security.token.service.ITokenService;

@Service
public class UsuarioServiceImpl implements IUsuarioService{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private IUsuarioDao usuarioDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ITokenService tokenService;

    public UsuarioServiceImpl(IUsuarioDao usuarioDao) {
		super();
        this.usuarioDao = usuarioDao;
	}

    @Transactional
    @Override
    public void registro(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRoles(Arrays.asList(new Rol("ROLE_USER")));

        usuarioDao.save(usuario);
    }

    @Transactional
    @Override
    public void save(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // the username is an email, the login is via email and password
        Usuario usuario = usuarioDao.findByEmail(username);
        if (usuario == null){
            throw new UsernameNotFoundException("Usuario o password invalidos");
        }

        boolean verified = !usuario.isEnabled();

        // return new User(usuario.getEmail(), usuario.getPassword(),
        // mapRolesToAuthorities(usuario.getRoles()));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .disabled(verified)
                .authorities(mapRolesToAuthorities(usuario.getRoles()))
                .build();
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Rol> roles){
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getNombre())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll() {
        return (List<Usuario>) usuarioDao.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Usuario> findAll(Pageable page) {
        return  usuarioDao.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario findById(Long id) {
        return usuarioDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        usuarioDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Usuario fetchByIdWithFacturas(Long id) {
        return usuarioDao.fetchByIdWithFacturas(id);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    public Usuario fetchByEmailWithFacturas(String email) {
        return usuarioDao.fetchByEmailWithFacturas(email);
    }

    @Transactional
    @Override
    public void sendRegistrationConfirmationEmail(Usuario usuario) {
        Token token = tokenService.createToken(usuario);
        try {
            emailService.sendSimpleEmail(usuario.getEmail(), "Completa tu registro!", "Hola " + usuario.getNombre().toUpperCase() + "! Tu cuenta fue creada con exito. Para activarla haz click en el siguiente enlace: http://localhost:8080/registro/verificacion?t="+token.getTokenValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Transactional
    @Override
    public void verify(String tokenValue) throws InvalidTokenException {

        Token token = tokenService.findByToken(tokenValue);

        if(token == null){
            throw new InvalidTokenException("Token Invalido");
        }

        Usuario usuario = token.getUsuario();
        usuario.setEnabled(true);
        tokenService.deleteByToken(tokenValue);

        this.save(usuario);
    }

    @Override
    public void activar(Usuario usuario) {
        usuario.setEnabled(true);

        usuarioDao.save(usuario);
    }

    @Override
    public void desactivar(Usuario usuario) {
        usuario.setEnabled(false);

        usuarioDao.save(usuario);
    }

}
