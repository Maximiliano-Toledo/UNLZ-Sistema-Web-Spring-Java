package com.unlz.tecjava.app.security.token.service.Impl;

import java.nio.charset.Charset;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.security.token.Token;
import com.unlz.tecjava.app.security.token.dao.ITokenDao;
import com.unlz.tecjava.app.security.token.service.ITokenService;

@Service
public class TokenServiceImpl implements ITokenService{

    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Autowired
    private ITokenDao tokenDao;

    @Transactional(readOnly = true)
    @Override
    public Token findByToken(String token) {
        return tokenDao.findByTokenValue(token);
    }

    @Transactional
    @Override
    public void deleteByToken(String token) {
        tokenDao.deleteByTokenValue(token);
    }

    @Transactional
    @Override
    public void save(Token token) {
        tokenDao.save(token);
    }

    @Transactional
    @Override
    public Token createToken(Usuario usuario) {
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII);
        Token token = new Token(usuario,tokenValue);
        token.setTokenValue(tokenValue);
        token.setUsuario(usuario);
        this.save(token);
        return token;
    }

}
