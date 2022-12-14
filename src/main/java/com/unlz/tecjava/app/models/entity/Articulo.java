package com.unlz.tecjava.app.models.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
@Entity
@Table(name="articulos")
public class Articulo implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 40)
    @NotEmpty
    private String nombre;

    @Size(min = 0, max = 300)
    private String descripcion;

    @Min(100)
    @Max(99999999)
    @NotNull
    private Double precio;

    @Max(3000000)
    @NotNull
    private Integer stock;

    @Column(name = "create_at", nullable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date createAt;

    @Column(name = "update_at")
    @Temporal(TemporalType.DATE)
    private Date updateAt;

    private String foto;

    private static final long serialVersionUID = 1L;

    @PrePersist
    public void prePersist() {
        createAt = new Date();
        updateAt = this.createAt;
    }

    @PreUpdate
    public void preUpdate() {
        updateAt = new Date();
    }
}
