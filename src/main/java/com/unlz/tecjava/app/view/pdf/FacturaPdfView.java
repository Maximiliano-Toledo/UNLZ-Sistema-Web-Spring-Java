package com.unlz.tecjava.app.view.pdf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.unlz.tecjava.app.models.entity.Factura;
import com.unlz.tecjava.app.models.entity.ItemFactura;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView{

    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        Factura factura = (Factura) model.get("factura");
        PdfPTable tabla = new PdfPTable(1);
        tabla.setSpacingAfter(20);
        tabla.addCell("Datos del Cliente");
        tabla.addCell(factura.getUsuario().getNombre() + " " + factura.getUsuario().getApellido());
        tabla.addCell(factura.getUsuario().getEmail());

        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingAfter(20);
        tabla2.addCell("Datos de la Factura");
        tabla2.addCell("Folio:" + factura.getId());
        tabla2.addCell("Descripcion:" + factura.getDescripcion());
        tabla2.addCell("Fecha:" + factura.getCreateAt());

        document.add(tabla);
        document.add(tabla2);

        PdfPTable tabla3 = new PdfPTable(4);
        tabla3.addCell("Articulo");
        tabla3.addCell("Precio");
        tabla3.addCell("Cantidad");
        tabla3.addCell("Total");

        for (ItemFactura item : factura.getItems()) {
            tabla3.addCell(item.getArticulo());
            tabla3.addCell(item.getPrecio().toString());
            tabla3.addCell(item.getCantidad().toString());
            tabla3.addCell(item.calcularImporte().toString());
        }

        PdfPCell cell = new PdfPCell(new Phrase("Total: "));
        cell.setColspan(3);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        tabla3.addCell(cell);
        tabla3.addCell(factura.getTotal().toString());

        document.add(tabla3);
    }

}
