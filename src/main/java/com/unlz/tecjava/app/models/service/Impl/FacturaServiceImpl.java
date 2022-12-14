package com.unlz.tecjava.app.models.service.Impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.unlz.tecjava.app.models.dao.IFacturaDao;
import com.unlz.tecjava.app.models.entity.Factura;
import com.unlz.tecjava.app.models.service.IFacturaService;

@Service
public class FacturaServiceImpl implements IFacturaService{

    @Autowired
    private IFacturaDao facturaDao;

    @Override
    public void save(Factura factura) {
        facturaDao.save(factura);
    }

    @Transactional(readOnly = true)
    @Override
    public Factura findById(Long id) {
        return facturaDao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        facturaDao.deleteById(id);
    }


    @Transactional(readOnly = true)
    @Override
    public Factura fetchFacturaByIdWithUsuarioWithItemFacturaWithArticulo(Long id) {
        return facturaDao.fetchByIdWithUsuarioWithItemFacturaWithArticulo(id);
    }

}

