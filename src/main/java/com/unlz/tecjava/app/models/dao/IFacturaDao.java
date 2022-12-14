package com.unlz.tecjava.app.models.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unlz.tecjava.app.models.entity.Factura;

@Repository
public interface IFacturaDao extends JpaRepository<Factura, Long>{

    @Query("select f from Factura f join fetch f.usuario u join fetch f.items where f.id=?1")
    Factura fetchByIdWithUsuarioWithItemFacturaWithArticulo(Long id);


}
