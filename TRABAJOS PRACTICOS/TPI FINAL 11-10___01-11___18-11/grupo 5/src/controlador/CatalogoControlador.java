package controlador;

import modelo.*;
import vista.CatalogoVista;

public final class CatalogoControlador {
    private final Navegacion navegacion;
    private final CatalogoVista vista = new CatalogoVista();

    public CatalogoControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.cambiar.addActionListener(evento -> cambiarPrecio());
        vista.stock.addActionListener(evento -> ingresarStock());
        vista.actividad.addActionListener(evento -> cambiarActividad());
        vista.volver.addActionListener(evento -> navegacion.mostrarInicio());
    }

    private void cambiarPrecio() {
        try {
            Libro libro = seleccionado();
            navegacion.servicio.cambiarPrecio(libro, EntradaFormulario.dinero(vista.precio.getText()));
            actualizar();
        } catch (IllegalArgumentException error) {
            navegacion.ventana.error(error);
        }
    }

    private void ingresarStock() {
        try {
            vista.cantidad.commitEdit();
            int cantidad = (Integer) vista.cantidad.getValue();
            navegacion.servicio.ingresarStock(seleccionado(), cantidad);
            actualizar();
        } catch (java.text.ParseException error) {
            navegacion.ventana.informar("Ingresá una cantidad entera válida.");
        } catch (IllegalArgumentException | ArithmeticException error) {
            navegacion.ventana.error(error);
        }
    }

    private void cambiarActividad() {
        try {
            Libro libro = seleccionado();
            navegacion.servicio.cambiarActividad(libro, !libro.isActivo());
            actualizar();
        } catch (IllegalArgumentException error) {
            navegacion.ventana.error(error);
        }
    }

    private Libro seleccionado() {
        return navegacion.servicio.libros().get(EntradaFormulario.seleccionado(vista.libros));
    }

    private void actualizar() {
        vista.modelo.setRowCount(0);
        for (Libro libro : navegacion.servicio.libros()) {
            String autores = nombresDeAutores(libro);
            String estado = libro.isActivo() ? "Sí" : "No";

            vista.modelo.addRow(new Object[]{
                libro.getIsbn(),
                libro.getTitulo(),
                autores,
                libro.getEditorial().getNombre(),
                libro.getCategoria().getNombre(),
                libro.getPrecioVenta(),
                libro.getStock(),
                estado
            });
        }
    }

    private String nombresDeAutores(Libro libro) {
        StringBuilder nombres = new StringBuilder();

        for (Autor autor : libro.getAutores()) {
            if (nombres.length() > 0) {
                nombres.append(", ");
            }
            nombres.append(autor.getNombre());
        }

        return nombres.toString();
    }

    public void mostrar() {
        actualizar();
        navegacion.ventana.mostrar(vista, true);
    }
}
