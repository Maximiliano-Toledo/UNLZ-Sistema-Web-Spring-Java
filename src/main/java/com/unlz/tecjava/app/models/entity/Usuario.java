package com.unlz.tecjava.app.models.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.unlz.tecjava.app.security.token.Token;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name="usuarios", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@NoArgsConstructor
public class Usuario implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 2, max = 14)
    @Column(name = "nombre")
    private String nombre;

    @NotEmpty
    @Size(min = 2, max = 14)
    @Column(name = "apellido")
    private String apellido;

    @Email
    @NotEmpty
    @Size(min = 2, max = 255)
    @Column(name = "email")
    private String email;

    @NotEmpty
    @Size(min = 2, max = 94)
    @Column(name = "password")
    private String password;

    @Column(name = "create_at", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date createAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.DATE)
    private Date updateAt;

    private boolean enabled;

    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "usuarios_roles",
        joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id", referencedColumnName =  "id")
    )
    private Collection<Rol> roles;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Factura> facturas;

    @OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;

    @PrePersist
    public void prePersist() {
        createAt = new Date();
        updateAt = this.createAt;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = new Date();
    }

    public Usuario(String nombre, String apellido, String email,
            String password, List<Rol> roles) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.facturas = new ArrayList<>();
    }

    public void addFactura(Factura factura){
        this.facturas.add(factura);
    }

    public void addToken(Token token) {
        this.tokens.add(token);
    }

    public boolean isEnabled(){
        return this.enabled;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }

}
