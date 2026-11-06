package controlador;

import modelo.*;
import vista.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public final class VentaControlador {
    private final InicioControlador app;
    private final Venta venta;
    private final VentaVista vista = new VentaVista();
    private final List<Libro> visibles = new ArrayList<>();

    public VentaControlador(InicioControlador app, Venta venta) {
        this.app = app;
        this.venta = venta;
        vista.buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                catalogo();
            }

            public void removeUpdate(DocumentEvent e) {
                catalogo();
            }

            public void changedUpdate(DocumentEvent e) {
                catalogo();
            }
        });
        vista.agregar.addActionListener(e -> accion(() -> {
            int fila = InicioControlador.seleccionado(vista.catalogo);
            Libro libro = visibles.get(fila);
            venta.agregarLibro(libro, cantidad());
        }));
        vista.modificar.addActionListener(e -> accion(() -> venta.modificarCantidad(detalle().getLibro(), cantidad())));
        vista.quitar.addActionListener(e -> accion(() -> venta.quitarLibro(detalle().getLibro())));
        vista.aplicar.addActionListener(e -> accion(() -> venta.aplicarDescuento(vista.codigo.getText())));
        vista.quitarDescuento.addActionListener(e -> accion(venta::quitarDescuento));
        vista.sinCliente.addActionListener(e -> accion(() -> venta.cambiarCliente(null)));
        vista.cliente.addActionListener(e -> new ClienteControlador(app, venta, this::mostrar).mostrar());
        vista.pagar.addActionListener(e -> app.ejecutar(() -> {
            if (venta.getDetalles().isEmpty()) {
                throw new IllegalStateException("Agregá al menos un libro");
            }
            mostrarPago();
        }));
        vista.cancelar.addActionListener(e -> app.ejecutar(() -> {
            int respuesta = JOptionPane.showConfirmDialog(
                    app.ventana, "¿Cancelar esta venta?", "Cancelar",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                venta.cancelar();
                app.mostrar();
            }
        }));
    }

    private int cantidad() {
        try {
            vista.cantidad.commitEdit();
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Ingresá una cantidad entera válida");
        }
        return (Integer) vista.cantidad.getValue();
    }

    private DetalleVenta detalle() {
        return venta.getDetalles().get(InicioControlador.seleccionado(vista.carrito));
    }

    private void accion(Runnable accion) {
        app.ejecutar(() -> {
            accion.run();
            venta.quitarPago();
            actualizar();
        });
    }

    public void mostrar() {
        actualizar();
        app.ventana.mostrar(vista);
    }

    private void catalogo() {
        String filtro = vista.buscar.getText().trim().toLowerCase(Locale.ROOT);
        visibles.clear();
        vista.catalogoModelo.setRowCount(0);
        for (Libro libro : app.servicio.libros()) {
            boolean coincideTitulo = libro.getTitulo().toLowerCase(Locale.ROOT).contains(filtro);
            boolean coincideIsbn = libro.getIsbn().toLowerCase(Locale.ROOT).contains(filtro);

            if (libro.isActivo() && (coincideTitulo || coincideIsbn)) {
                visibles.add(libro);
                vista.catalogoModelo.addRow(new Object[]{
                    libro.getIsbn(), libro.getTitulo(), libro.getPrecioVenta(), libro.getStock()
                });
            }
        }
    }

    private void actualizar() {
        catalogo();
        vista.carritoModelo.setRowCount(0);
        for (DetalleVenta item : venta.getDetalles()) {
            vista.carritoModelo.addRow(new Object[]{
                item.getTitulo(), item.getCantidad(), item.getPrecioUnitario(), item.calcularSubtotal()
            });
        }
        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            vista.clienteActual.setText("Sin cliente");
        } else {
            vista.clienteActual.setText(cliente.getNombre() + " · DNI " + cliente.getDni());
        }
        vista.totales.setText("Subtotal: $ " + venta.calcularSubtotal() + "    Descuento: $ "
                + venta.calcularDescuento() + "    Total: $ " + venta.calcularTotal());
    }

    private void mostrarPago() {
        new PagoControlador(app, venta, this::mostrar).mostrar();
    }
}
