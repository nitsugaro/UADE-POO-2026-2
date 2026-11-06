package controlador;

import modelo.*;
import vista.CatalogoVista;

public final class CatalogoControlador {
    private final InicioControlador app;
    private final CatalogoVista vista = new CatalogoVista();

    public CatalogoControlador(InicioControlador app) {
        this.app = app;
        vista.cambiar.addActionListener(e -> app.ejecutar(() -> {
            seleccionado().cambiarPrecio(InicioControlador.dinero(vista.precio.getText()));
            actualizar();
        }));
        vista.stock.addActionListener(e -> app.ejecutar(() -> {
            try {
                vista.cantidad.commitEdit();
            } catch (java.text.ParseException error) {
                throw new IllegalArgumentException("Ingresá una cantidad entera válida");
            }
            seleccionado().aumentarStock((Integer) vista.cantidad.getValue());
            actualizar();
        }));
        vista.actividad.addActionListener(e -> app.ejecutar(() -> {
            Libro libro = seleccionado();
            if (libro.isActivo()) {
                libro.desactivar();
            } else {
                libro.activar();
            }
            actualizar();
        }));
        vista.volver.addActionListener(e -> app.mostrar());
    }

    private Libro seleccionado() {
        return app.servicio.libros().get(InicioControlador.seleccionado(vista.libros));
    }

    private void actualizar() {
        vista.modelo.setRowCount(0);
        for (Libro libro : app.servicio.libros()) {
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
        app.ventana.mostrar(vista);
    }
}
