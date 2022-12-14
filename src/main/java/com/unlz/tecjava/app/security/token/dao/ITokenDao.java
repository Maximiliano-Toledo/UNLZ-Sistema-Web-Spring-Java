package com.unlz.tecjava.app.security.token.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unlz.tecjava.app.security.token.Token;

@Repository
public interface ITokenDao extends JpaRepository<Token, Long>{

    Token findByTokenValue(String token);

    void deleteByTokenValue(String token);

}
