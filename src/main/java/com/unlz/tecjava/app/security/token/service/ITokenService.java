package com.unlz.tecjava.app.security.token.service;

import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.security.token.Token;

public interface ITokenService {

    Token createToken(Usuario usuario);

    void save(Token token);

    Token findByToken(String token);

    void deleteByToken(String token);

}
