package controlador;

import modelo.*;
import excepciones.DevolucionNoPermitidaException;
import vista.DevolucionVista;
import javax.swing.JOptionPane;

public final class DevolucionControlador {
    private final Navegacion navegacion;
    private final Venta venta;
    private final DevolucionVista vista = new DevolucionVista();

    public DevolucionControlador(Navegacion navegacion, Venta venta) {
        this.navegacion = navegacion;
        this.venta = venta;
        vista.volver.addActionListener(evento -> volverAlDetalle());
        vista.confirmar.addActionListener(evento -> confirmarDevolucion());
    }

    public void mostrar() {
        vista.resumen.setText("Venta #" + venta.getId() + " · Reintegro: $ " + venta.getPago().getImportePagado()
                + " · " + venta.getPago().getMedioPago());
        navegacion.ventana.mostrar(vista, true);
    }

    private void volverAlDetalle() {
        navegacion.mostrarDetalle(venta);
    }

    private void confirmarDevolucion() {
        try {
            int respuesta = JOptionPane.showConfirmDialog(
                    navegacion.ventana, "¿Confirmar la devolución de todos los libros?", "Devolución total",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            navegacion.servicio.devolver(venta, navegacion.servicio.empleadoPredeterminado(), vista.motivo.getText(),
                    vista.aptos.isSelected());
            navegacion.mostrarDetalle(venta);
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException | DevolucionNoPermitidaException error) {
            navegacion.ventana.error(error);
        }
    }
}
