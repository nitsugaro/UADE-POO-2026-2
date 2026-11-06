package controlador;

import modelo.*;
import vista.*;
import java.util.List;

public final class HistorialVentasControlador {
    private final InicioControlador app;

    public HistorialVentasControlador(InicioControlador app) {
        this.app = app;
    }

    public void mostrar() {
        HistorialVentasVista vista = new HistorialVentasVista();
        List<Venta> ventas = app.servicio.ventas();
        for (Venta venta : ventas) {
            String cliente = "Sin cliente";
            if (venta.getCliente() != null) {
                cliente = venta.getCliente().getNombre();
            }

            vista.modelo.addRow(new Object[]{
                venta.getId(),
                venta.getFecha(),
                cliente,
                venta.getEstado(),
                venta.calcularTotal()
            });
        }

        vista.detalle.addActionListener(e -> app.ejecutar(() -> {
            int fila = InicioControlador.seleccionado(vista.ventas);
            Venta venta = ventas.get(fila);
            mostrarDetalle(venta);
        }));
        vista.volver.addActionListener(e -> app.mostrar());
        app.ventana.mostrar(vista);
    }

    public void mostrarDetalle(Venta venta) {
        ResumenVentaVista vista = new ResumenVentaVista();
        StringBuilder texto = new StringBuilder();
        texto.append("Venta #").append(venta.getId()).append(" · ").append(venta.getEstado());
        texto.append("\nFecha: ").append(venta.getFecha());
        texto.append("\nEmpleado: ").append(venta.getEmpleado().getNombre());

        String cliente = "Sin cliente";
        if (venta.getCliente() != null) {
            cliente = venta.getCliente().getNombre();
        }
        texto.append("\nCliente: ").append(cliente).append("\n\n");

        for (DetalleVenta item : venta.getDetalles()) {
            texto.append(item.getTitulo()).append(" · ISBN ").append(item.getIsbn());
            texto.append("\n  ").append(item.getCantidad());
            texto.append(" x $ ").append(item.getPrecioUnitario());
            texto.append(" = $ ").append(item.calcularSubtotal()).append("\n");
        }

        texto.append("\nSubtotal: $ ").append(venta.calcularSubtotal());
        texto.append("\nDescuento: $ ").append(venta.calcularDescuento());
        texto.append("\nTotal: $ ").append(venta.calcularTotal());

        if (venta.getPago() != null) {
            texto.append("\nPago: ").append(venta.getPago().getMedioPago());
            texto.append("\nVuelto: $ ").append(venta.getPago().calcularVuelto());
        }

        for (Devolucion devolucion : app.servicio.devoluciones()) {
            if (devolucion.getVentaOriginal() == venta) {
                texto.append("\n\nDevolución: ").append(devolucion.getFecha());
                texto.append("\nResponsable: ").append(devolucion.getEmpleadoResponsable().getNombre());
                texto.append("\nMotivo: ").append(devolucion.getMotivo());
                texto.append("\nReintegro: $ ").append(devolucion.getImporteReintegrado());
                texto.append(" · ").append(devolucion.getMedioReintegro());
            }
        }

        vista.detalle.setText(texto.toString());
        vista.detalle.setCaretPosition(0);
        vista.devolver.setEnabled(venta.getEstado() == EstadoVenta.CONFIRMADA);
        vista.devolver.addActionListener(e -> new DevolucionControlador(app, venta).mostrar());
        vista.inicio.addActionListener(e -> app.mostrar());
        vista.nueva.addActionListener(e -> app.ejecutar(app::nuevaVenta));
        app.ventana.mostrar(vista);
    }
}
