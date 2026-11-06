package controlador;

import modelo.*;
import vista.DevolucionVista;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public final class DevolucionControlador {
    private final InicioControlador app;
    private final Venta venta;

    public DevolucionControlador(InicioControlador app, Venta venta) {
        this.app = app;
        this.venta = venta;
    }

    public void mostrar() {
        DevolucionVista vista = new DevolucionVista();
        vista.resumen.setText("Venta #" + venta.getId() + " · Reintegro: $ " + venta.getPago().getImportePagado()
                + " · " + venta.getPago().getMedioPago());
        List<Empleado> empleados = new ArrayList<>();
        for (Empleado empleado : app.servicio.empleados()) {
            if (empleado.isActivo()) {
                empleados.add(empleado);
            }
        }
        for (Empleado empleado : empleados) {
            vista.empleados.addItem(empleado.getNombre());
        }
        vista.volver.addActionListener(e -> new HistorialVentasControlador(app).mostrarDetalle(venta));
        vista.confirmar.addActionListener(e -> app.ejecutar(() -> {
            if (vista.empleados.getSelectedIndex() < 0) {
                throw new IllegalArgumentException("Seleccioná un empleado activo");
            }
            int respuesta = JOptionPane.showConfirmDialog(
                    app.ventana, "¿Confirmar la devolución de todos los libros?", "Devolución total",
                    JOptionPane.YES_NO_OPTION);

            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
            app.servicio.devolver(venta, empleados.get(vista.empleados.getSelectedIndex()), vista.motivo.getText(),
                    vista.aptos.isSelected());
            new HistorialVentasControlador(app).mostrarDetalle(venta);
        }));
        app.ventana.mostrar(vista);
    }
}
