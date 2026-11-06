package controlador;

import modelo.Empleado;
import vista.InicioVista;

public final class InicioControlador {
    private final Navegacion navegacion;
    private final InicioVista vista = new InicioVista();

    public InicioControlador(Navegacion navegacion, Empleado seleccionado) {
        this.navegacion = navegacion;

        for (Empleado empleado : navegacion.servicio.empleados()) {
            vista.empleados.addItem(empleado.getLegajo() + " · " + empleado.getNombre());
        }
        if (seleccionado != null) {
            vista.empleados.setSelectedIndex(navegacion.servicio.empleados().indexOf(seleccionado));
        }

        vista.venta.addActionListener(evento -> iniciarVenta());
        vista.historial.addActionListener(evento -> navegacion.mostrarHistorial());
        vista.catalogo.addActionListener(evento -> navegacion.mostrarCatalogo());
    }

    public void mostrar() {
        navegacion.ventana.mostrar(vista, true);
    }

    private void iniciarVenta() {
        try {
            int indice = vista.empleados.getSelectedIndex();
            if (indice < 0) {
                throw new IllegalArgumentException("Seleccioná un empleado");
            }
            Empleado empleado = navegacion.servicio.empleados().get(indice);
            navegacion.seleccionarEmpleado(empleado);
            navegacion.nuevaVenta();
        } catch (IllegalArgumentException error) {
            navegacion.ventana.error(error);
        }
    }
}
