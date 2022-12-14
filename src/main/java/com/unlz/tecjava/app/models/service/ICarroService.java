package com.unlz.tecjava.app.models.service;

import java.util.Map;

import com.unlz.tecjava.app.exception.OutOfStockArticuloException;
import com.unlz.tecjava.app.models.entity.Articulo;

public interface ICarroService {
    void addArticulo(Articulo articulo) throws OutOfStockArticuloException;

    void changeQuantityArticulo(Articulo articulo, Integer cantidad);

    void removeArticulo(Articulo articulo);

    void completeDeleteFromCarro(Articulo articulo);

    Map<Articulo, Integer> getArticulosCarro();

    Map<Articulo, Integer> getUnmodifiablemapArticulosCarro();

    void pagar() throws OutOfStockArticuloException, NullPointerException;

    void clearArticulos();

    public int getElementos();

    public void setElementos(int elementos);

}
