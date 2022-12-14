package com.unlz.tecjava.app.models.entity;

import java.io.Serializable;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="facturas_items")
public class ItemFactura implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer cantidad;

    private Double precio;

    @Column(name = "articulo_nombre")
    private String articulo;

    private static final long serialVersionUID = 1L;

    public Double calcularImporte(){

        return cantidad.doubleValue() * this.precio;
    }

}
