package com.unlz.tecjava.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

/*
    Esta clase cumple la funcion de calcular parametros por ejemplo
    desde donde hasta donde tiene que partir en el listado de ya sea
    de usuarios, articulos, etc.
    Y a medida que se va avanzando, se va  moviendo hacia los siguientes
    registros, de forma dinamica, o si vamos retrocediendo, vayan
    desapareciendo las ultimas paginas y vaya avanzando hacia las primeras
    paginas, y siempre mostrar un numero acotado de paginas y no todo
    el paginador completo. (mostrar de 10 en 10, de 5 en 5, etc.)
*/

@Getter
public class PageRender<T> {

    private String url;
    private Page<T> page;
    private int totalPaginas;
    private int numElementosPorPagina;
    private int paginaActual;
    private List<PageItem> paginas;

    public PageRender() {

    }

    public PageRender(String url, Page<T> page) {
        this.url = url;
        this.page = page;
        this.paginas = new ArrayList<>();

        numElementosPorPagina = page.getSize();
        totalPaginas = page.getTotalPages();
        paginaActual = page.getNumber() + 1;

        int desde;
        int hasta;
        if (totalPaginas <= numElementosPorPagina) {
            desde = 1;
            hasta = totalPaginas;
        } else {
            if (paginaActual <= numElementosPorPagina / 2) {
                desde = 1;
                hasta = numElementosPorPagina;
            } else if (paginaActual >= totalPaginas - numElementosPorPagina / 2) {
                desde = totalPaginas - numElementosPorPagina + 1;
                hasta = numElementosPorPagina;
            } else {
                desde = paginaActual - numElementosPorPagina / 2;
                hasta = numElementosPorPagina;
            }
        }

        // llenar la lista PageItem
        for (int i = 0; i < hasta; i++) {
            paginas.add(new PageItem(desde + i, paginaActual == desde + i));
        }
    }

    public boolean isFirst() {
        return page.isFirst();
    }

    public boolean isLast() {
        return page.isLast();
    }

    public boolean isHasNext() {
        return page.hasNext();
    }

    public boolean isHasPrevious() {
        return page.hasPrevious();
    }

}
