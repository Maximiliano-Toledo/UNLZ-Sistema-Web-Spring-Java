package com.unlz.tecjava.app.models.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.unlz.tecjava.app.models.entity.Usuario;

@Repository
public interface IUsuarioDao extends PagingAndSortingRepository<Usuario, Long>{

    @Query("select u from Usuario u left join fetch u.facturas f where u.id=?1")
    Usuario fetchByIdWithFacturas(Long id);

    @Query("select u from Usuario u left join fetch u.facturas f where u.email='?1'")
    Usuario fetchByEmailWithFacturas(String email);

    Usuario findByEmail(String email);

}
