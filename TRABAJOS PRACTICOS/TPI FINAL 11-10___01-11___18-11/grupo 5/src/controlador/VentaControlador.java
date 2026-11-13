package controlador;

import modelo.*;
import vista.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public final class VentaControlador {
    private final Navegacion navegacion;
    private final Venta venta;
    private final VentaVista vista = new VentaVista();
    private final List<Libro> visibles = new ArrayList<>();

    public VentaControlador(Navegacion navegacion, Venta venta) {
        this.navegacion = navegacion;
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
        vista.agregar.addActionListener(evento -> agregarLibro());
        vista.modificar.addActionListener(evento -> modificarCantidad());
        vista.quitar.addActionListener(evento -> quitarLibro());
        vista.aplicar.addActionListener(evento -> aplicarDescuento());
        vista.quitarDescuento.addActionListener(evento -> quitarDescuento());
        vista.sinCliente.addActionListener(evento -> quitarCliente());
        vista.cliente.addActionListener(evento -> seleccionarCliente(false));
        vista.nuevoCliente.addActionListener(evento -> seleccionarCliente(true));
        vista.catalogo.getSelectionModel().addListSelectionListener(evento -> actualizarAccionesCatalogo());
        vista.carrito.getSelectionModel().addListSelectionListener(evento -> actualizarAccionesCarrito());
        vista.pagar.addActionListener(evento -> mostrarPago());
        vista.cancelar.addActionListener(evento -> cancelarVenta());
    }

    private void agregarLibro() {
        try {
            int fila = EntradaFormulario.seleccionado(vista.catalogo);
            Libro libro = visibles.get(fila);
            int cantidad = cantidad(vista.cantidad);
            if (cantidad > stockDisponible(libro)) {
                throw new IllegalArgumentException("Stock disponible para agregar: " + stockDisponible(libro));
            }
            venta.agregarLibro(libro, cantidad);
            actualizarVenta();
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            navegacion.ventana.error(error);
        }
    }

    private void modificarCantidad() {
        try {
            Libro libro = detalle().getLibro();
            int cantidad = cantidad(vista.cantidadCarrito);
            if (!libro.hayDisponibilidad(cantidad)) {
                throw new IllegalArgumentException("Stock disponible: " + libro.getStock());
            }
            venta.modificarCantidad(libro, cantidad);
            actualizarVenta();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }

    private void quitarLibro() {
        try {
            venta.quitarLibro(detalle().getLibro());
            actualizarVenta();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }

    private void aplicarDescuento() {
        try {
            venta.aplicarDescuento(vista.codigo.getText());
            actualizarVenta();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }

    private void quitarDescuento() {
        venta.quitarDescuento();
        actualizarVenta();
    }

    private void quitarCliente() {
        venta.cambiarCliente(null);
        actualizarVenta();
    }

    private void seleccionarCliente(boolean nuevo) {
        navegacion.mostrarClientes(venta, this::mostrar, nuevo);
    }

    private void cancelarVenta() {
        int respuesta = JOptionPane.showConfirmDialog(
                navegacion.ventana, "¿Cancelar esta venta?", "Cancelar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                navegacion.servicio.cancelar(venta);
                navegacion.mostrarInicio();
            } catch (IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }

    private int cantidad(JSpinner campo) {
        try {
            campo.commitEdit();
        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException("Ingresá una cantidad entera válida");
        }
        return (Integer) campo.getValue();
    }

    private DetalleVenta detalle() {
        return venta.getDetalles().get(EntradaFormulario.seleccionado(vista.carrito));
    }

    private void actualizarVenta() {
        venta.quitarPago();
        actualizar();
    }

    public void mostrar() {
        actualizar();
        navegacion.ventana.mostrar(vista, true);
    }

    private void catalogo() {
        String filtro = vista.buscar.getText().trim().toLowerCase(Locale.ROOT);
        vista.catalogo.clearSelection();
        visibles.clear();
        vista.catalogoModelo.setRowCount(0);
        for (Libro libro : navegacion.servicio.libros()) {
            boolean coincideTitulo = libro.getTitulo().toLowerCase(Locale.ROOT).contains(filtro);
            boolean coincideIsbn = libro.getIsbn().toLowerCase(Locale.ROOT).contains(filtro);

            if (libro.isActivo() && (coincideTitulo || coincideIsbn)) {
                visibles.add(libro);
                vista.catalogoModelo.addRow(new Object[]{
                    libro.getIsbn(), libro.getTitulo(), stockDisponible(libro), libro.getPrecioVenta()
                });
            }
        }
    }

    private int stockDisponible(Libro libro) {
        return venta.getDetalles().stream().filter(item -> item.getLibro() == libro).findFirst()
                .map(item -> libro.getStock() - item.getCantidad()).orElse(libro.getStock());
    }

    private void actualizarAccionesCatalogo() {
        int fila = vista.catalogo.getSelectedRow();
        vista.agregar.setEnabled(fila >= 0
                && stockDisponible(visibles.get(vista.catalogo.convertRowIndexToModel(fila))) > 0);
    }

    private void actualizarAccionesCarrito() {
        boolean seleccionado = vista.carrito.getSelectedRow() >= 0;
        vista.modificar.setEnabled(seleccionado);
        vista.quitar.setEnabled(seleccionado);
        vista.cantidadCarrito.setEnabled(seleccionado);
        if (seleccionado) {
            vista.cantidadCarrito.setValue(detalle().getCantidad());
        }
    }

    private void actualizar() {
        catalogo();
        vista.carrito.clearSelection();
        vista.carritoModelo.setRowCount(0);
        for (DetalleVenta item : venta.getDetalles()) {
            vista.carritoModelo.addRow(new Object[]{
                item.getTitulo(), item.getCantidad(), item.getPrecioUnitario(), item.calcularSubtotal()
            });
        }
        Cliente cliente = venta.getCliente();
        if (cliente == null) {
            vista.clienteActual.setText("Sin cliente asignado");
        } else {
            vista.clienteActual.setText(cliente.getNombre() + " · DNI " + cliente.getDni());
        }
        vista.sinCliente.setEnabled(cliente != null);
        actualizarAccionesCatalogo();
        actualizarAccionesCarrito();
        vista.totales.setText("Subtotal: $ " + venta.calcularSubtotal() + "    Descuento: $ "
                + venta.calcularDescuento() + "    Total: $ " + venta.calcularTotal());
    }

    private void mostrarPago() {
        if (venta.getDetalles().isEmpty()) {
            navegacion.ventana.informar("Agregá al menos un libro antes de pasar al pago.");
            return;
        }
        navegacion.mostrarPago(venta, this::mostrar);
    }
}
