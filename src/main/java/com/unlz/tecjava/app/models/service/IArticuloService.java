package com.unlz.tecjava.app.models.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.unlz.tecjava.app.models.entity.Articulo;

public interface IArticuloService {

    List<Articulo> findAll();

    Page<Articulo> findAll(Pageable page);

    void save(Articulo articulo);

    Articulo findById(Long id);

    void delete(Long id);

    void hola(Set<Articulo> articulosSet);

    List<Articulo> findByNombre(String term);

}
