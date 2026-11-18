package pruebas;

import vista.*;
import javax.swing.*;
import java.awt.*;

/** Comprueba el problema de campos estirados en todas las pantallas, sin escritorio. */
public final class PruebasVistas {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            PanelVista[] vistas = {
                new InicioVista(), new ClienteVista(), new PagoVista(),
                new DevolucionVista(), new VentaVista(), new CatalogoVista(),
                new HistorialVentasVista(), new ResumenVentaVista(), new PrestamoVista(), new CategoriaVista(),
                new EditorialVista()
            };

            for (PanelVista vista : vistas) {
                Dimension base = vista.getPreferredSize();
                comprobar(vista, base);
                comprobar(vista, new Dimension(base.width + 300, base.height + 200));

                if (vista instanceof ClienteVista) {
                    ClienteVista cliente = (ClienteVista) vista;
                    cliente.opciones.setSelectedIndex(1);
                    comprobar(vista, base);
                }
                if (vista instanceof PagoVista) {
                    PagoVista pago = (PagoVista) vista;
                    pago.medio.setSelectedIndex(1);
                    pago.mostrarMedioSeleccionado();
                    comprobar(vista, base);
                }
                if (vista instanceof PrestamoVista prestamo) {
                    prestamo.opciones.setSelectedIndex(1);
                    comprobar(vista, base);
                }
            }
            LibroFormulario libro = new LibroFormulario();
            comprobar(libro, libro.getPreferredSize());
            System.out.println("OK: 11 vistas en tamaño inicial y ampliado; pestañas y formulario de libros");
        });
    }

    private static void comprobar(JPanel vista, Dimension tamano) {
        vista.setSize(tamano);
        distribuir(vista);
        validarCampos(vista);
        if (vista instanceof VentaVista venta
                && venta.catalogo.getParent().getHeight() <= venta.carrito.getParent().getHeight()) {
            throw new AssertionError("El catálogo debe tener más espacio visible que el carrito");
        }
    }

    private static void distribuir(Container contenedor) {
        contenedor.doLayout();
        for (Component componente : contenedor.getComponents()) {
            if (componente instanceof Container) {
                distribuir((Container) componente);
            }
        }
    }

    private static void validarCampos(Container contenedor) {
        for (Component componente : contenedor.getComponents()) {
            if (!componente.isVisible()) {
                continue;
            }
            if (componente instanceof JTextField || componente instanceof JComboBox) {
                if (componente.getHeight() > componente.getPreferredSize().height + 8) {
                    throw new AssertionError("Campo estirado en " + contenedor.getClass().getSimpleName());
                }
                if (componente.getHeight() <= 0 || componente.getWidth() <= 0) {
                    throw new AssertionError("Campo sin espacio visible");
                }
            }
            if (componente instanceof Container) {
                validarCampos((Container) componente);
            }
        }
    }
}
