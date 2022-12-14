package com.unlz.tecjava.app.models.service.Impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import com.unlz.tecjava.app.exception.OutOfStockArticuloException;
import com.unlz.tecjava.app.models.dao.IArticuloDao;
import com.unlz.tecjava.app.models.dao.IFacturaDao;
import com.unlz.tecjava.app.models.dao.IUsuarioDao;
import com.unlz.tecjava.app.models.entity.Articulo;
import com.unlz.tecjava.app.models.entity.Factura;
import com.unlz.tecjava.app.models.entity.ItemFactura;
import com.unlz.tecjava.app.models.entity.Usuario;
import com.unlz.tecjava.app.models.service.ICarroService;


@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
public class CarroServiceImpl implements ICarroService{

    @Autowired
    private IArticuloDao articuloDao;

    @Autowired
    private IFacturaDao facturaDao;

    @Autowired
    private IUsuarioDao usuarioDao;

    private Map<Articulo, Integer> articulos = new HashMap<>();
    private int elementos = 0;

    public int getElementos() {
        return elementos;
    }

    public void setElementos(int elementos) {
        this.elementos = elementos;
    }

    @Override
    public void addArticulo(Articulo articulo) throws OutOfStockArticuloException{
        if(articulos.containsKey(articulo)){
            if (articulo.getStock() > articulos.get(articulo)){
                articulos.replace(articulo, articulos.get(articulo) + 1);
                elementos++;
            }else{
                throw new OutOfStockArticuloException("No hay mas stock del articulo");
            }
        } else {
            articulos.put(articulo, 1);
            elementos++;
        }
    }

    @Override
    public void removeArticulo(Articulo articulo) {
        if (articulos.containsKey(articulo)) {
            if(articulos.get(articulo) > 1) {
                articulos.replace(articulo, articulos.get(articulo) - 1);
                elementos--;
            } else if (articulos.get(articulo) == 1) {
                articulos.remove(articulo);
                elementos--;
            }
        }
    }

    @Override
    public Map<Articulo, Integer> getUnmodifiablemapArticulosCarro() {
        return Collections.unmodifiableMap(articulos);
    }


    @Override
    public void pagar() throws OutOfStockArticuloException, NullPointerException{

        StringBuilder builder = new StringBuilder();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String emailUser = auth.getName();
        Usuario usuario = usuarioDao.findByEmail(emailUser);

        Factura factura = new Factura();
        factura.setDescripcion("Factura Cliente -/" + UUID.randomUUID().toString());
        factura.setObservacion(emailUser);
        factura.setUsuario(usuario);

        Articulo articulo = null;

        for(Map.Entry<Articulo, Integer> entry : articulos.entrySet()){

            articulo = articuloDao.findById(entry.getKey().getId()).orElse(null);

            if (articulo == null){
                this.clearArticulos();
                throw new NullPointerException("Uno de los articulos del carro no existe en la base de datos.");
            }

            if (articulo.getStock() < entry.getValue()){

                builder.append("La cantidad solicitada de '")
                        .append(entry.getValue().toString())
                        .append("' del articulo '")
                        .append(articulo.getNombre())
                        .append("' del carro supera el stock disponible (Stock Disponible: '")
                        .append(articulo.getStock())
                        .append("'').");

                builder.setLength(0);

                this.changeQuantityArticulo(articulo, articulo.getStock());
                throw new OutOfStockArticuloException("La cantidad solicitada de " + entry.getValue().toString() + " del articulo " + articulo.getNombre() + " del carro supera el stock disponible (Stock disponible: '"+ articulo.getStock()+"').");
            }

            // Cambia el Stock del articulo entidad.
            entry.getKey().setStock(articulo.getStock() - entry.getValue());
            articuloDao.save(entry.getKey());

            ItemFactura linea = new ItemFactura();
            linea.setCantidad(entry.getValue());
            linea.setPrecio(entry.getKey().getPrecio());
            linea.setArticulo(entry.getKey().getNombre());
            factura.addItemFactura(linea);
        }

        this.elementos = 0;
        facturaDao.save(factura);
        articulos.clear();
    }


    @Override
    public void clearArticulos() {
        this.elementos = 0;
        articulos.clear();
    }

    @Override
    public Map<Articulo, Integer> getArticulosCarro() {
        return articulos;
    }

    @Override
    public void changeQuantityArticulo(Articulo articulo, Integer cantidad) {
        this.elementos += cantidad;
        articulos.replace(articulo, cantidad);
    }

    @Override
    public void completeDeleteFromCarro(Articulo articulo) {
        elementos -= articulos.get(articulo).intValue();
        articulos.remove(articulo);

    }

}
