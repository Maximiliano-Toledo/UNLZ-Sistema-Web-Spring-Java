package com.unlz.tecjava.app.models.service;

import com.unlz.tecjava.app.models.entity.Factura;

public interface IFacturaService {

    void save(Factura factura);

    Factura findById(Long id);

    void delete(Long id);

    Factura fetchFacturaByIdWithUsuarioWithItemFacturaWithArticulo(Long id);

}
