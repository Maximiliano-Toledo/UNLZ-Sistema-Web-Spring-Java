package com.unlz.tecjava.app.models.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.unlz.tecjava.app.models.entity.Articulo;

@Repository
public interface IArticuloDao extends PagingAndSortingRepository<Articulo,Long>{

    @Query("select a from Articulo a where a.nombre like %?1%")
    List<Articulo> findByNombre(String term);

    List<Articulo> findByNombreLikeIgnoreCase(String term);
}
