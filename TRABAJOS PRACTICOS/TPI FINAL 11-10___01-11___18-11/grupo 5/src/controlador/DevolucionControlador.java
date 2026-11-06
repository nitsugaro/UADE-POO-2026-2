package controlador;

import modelo.*;
import excepciones.DevolucionNoPermitidaException;
import vista.DevolucionVista;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public final class DevolucionControlador {
    private final Navegacion navegacion;
    private final Venta venta;
    private final DevolucionVista vista = new DevolucionVista();
    private final List<Empleado> empleados = new ArrayList<>();

    public DevolucionControlador(Navegacion navegacion, Venta venta) {
        this.navegacion = navegacion;
        this.venta = venta;
        vista.volver.addActionListener(evento -> volverAlDetalle());
        vista.confirmar.addActionListener(evento -> confirmarDevolucion());
    }

    public void mostrar() {
        vista.resumen.setText("Venta #" + venta.getId() + " · Reintegro: $ " + venta.getPago().getImportePagado()
                + " · " + venta.getPago().getMedioPago());
        empleados.clear();
        vista.empleados.removeAllItems();
        for (Empleado empleado : navegacion.servicio.empleados()) {
            if (empleado.isActivo()) {
                empleados.add(empleado);
            }
        }
        for (Empleado empleado : empleados) {
            vista.empleados.addItem(empleado.getNombre());
        }
        navegacion.ventana.mostrar(vista, true);
    }

    private void volverAlDetalle() {
        navegacion.mostrarDetalle(venta);
    }

    private void confirmarDevolucion() {
        try {
            if (vista.empleados.getSelectedIndex() < 0) {
                throw new IllegalArgumentException("Seleccioná un empleado activo");
            }
            int respuesta = JOptionPane.showConfirmDialog(
                    navegacion.ventana, "¿Confirmar la devolución de todos los libros?", "Devolución total",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            navegacion.servicio.devolver(venta, empleados.get(vista.empleados.getSelectedIndex()), vista.motivo.getText(),
                    vista.aptos.isSelected());
            navegacion.mostrarDetalle(venta);
        } catch (IllegalArgumentException | ArithmeticException | DevolucionNoPermitidaException error) {
            navegacion.ventana.error(error);
        }
    }
}
