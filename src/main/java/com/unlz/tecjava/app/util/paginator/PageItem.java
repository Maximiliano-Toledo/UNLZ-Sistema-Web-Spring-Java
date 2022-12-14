package com.unlz.tecjava.app.util.paginator;

import lombok.Data;

/*
    Esta clase cumple la funcion de representar cada
    una de las paginas, con su numero de pagina y con
    un atributo para indicar si es o no la pagina actual.
*/

@Data
public class PageItem {
    private final int numero;
    private final boolean actual;

    public PageItem(int numero, boolean actual) {
        this.numero = numero;
        this.actual = actual;
    }

}
