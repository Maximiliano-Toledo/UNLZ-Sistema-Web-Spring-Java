package com.unlz.tecjava.app.security.token;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.unlz.tecjava.app.models.entity.Usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="tokens")
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="token_value")
    private String tokenValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    @Column(name = "create_at", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date createAt;

    public Token(Usuario usuario, String tokenValue){
        this.tokenValue = tokenValue;
        this.usuario = usuario;
    }

    @PrePersist
    public void prePersist() {
        createAt = new Date();
    }

}
