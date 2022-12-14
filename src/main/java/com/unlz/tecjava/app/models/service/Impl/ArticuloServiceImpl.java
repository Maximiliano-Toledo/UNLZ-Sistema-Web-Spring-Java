package com.unlz.tecjava.app.models.service.Impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unlz.tecjava.app.models.dao.IArticuloDao;
import com.unlz.tecjava.app.models.entity.Articulo;
import com.unlz.tecjava.app.models.service.IArticuloService;

@Service
public class ArticuloServiceImpl implements IArticuloService{

    @Autowired
    private IArticuloDao articuloDao;

    @Transactional(readOnly = true)
    @Override
    public List<Articulo> findAll() {
        return (List<Articulo>) articuloDao.findAll();
    }

    @Transactional
    @Override
    public void save(Articulo articulo) {
        articuloDao.save(articulo);

    }

    @Transactional(readOnly = true)
    @Override
    public Articulo findById(Long id) {
        return articuloDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        articuloDao.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Articulo> findAll(Pageable page) {
        return articuloDao.findAll(page);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Articulo> findByNombre(String term) {
        return articuloDao.findByNombre(term);
    }

    @Override
    public void hola(Set<Articulo> articulosSet) {
        // TODO Auto-generated method stub

    }

}
