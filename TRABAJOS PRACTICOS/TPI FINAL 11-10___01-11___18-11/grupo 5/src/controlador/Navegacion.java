package controlador;

import modelo.Empleado;
import modelo.Venta;
import servicio.LibreriaServicio;
import vista.VentanaPrincipal;

public final class Navegacion {
    final VentanaPrincipal ventana;
    final LibreriaServicio servicio;
    private Empleado empleado;

    public Navegacion(VentanaPrincipal ventana, LibreriaServicio servicio) {
        this.ventana = ventana;
        this.servicio = servicio;
    }

    public void mostrarInicio() {
        new InicioControlador(this, empleado).mostrar();
    }

    void mostrarHistorial() {
        new HistorialVentasControlador(this).mostrar();
    }

    void mostrarCatalogo() {
        new CatalogoControlador(this).mostrar();
    }

    void mostrarDetalle(Venta venta) {
        new HistorialVentasControlador(this).mostrarDetalle(venta);
    }

    void mostrarDevolucion(Venta venta) {
        new DevolucionControlador(this, venta).mostrar();
    }

    void mostrarClientes(Venta venta, Runnable volver) {
        new ClienteControlador(this, venta, volver).mostrar();
    }

    void mostrarPago(Venta venta, Runnable volver) {
        new PagoControlador(this, venta, volver).mostrar();
    }

    void seleccionarEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    void nuevaVenta() {
        if (empleado == null) {
            mostrarInicio();
            return;
        }
        Venta venta = servicio.nuevaVenta(empleado);
        new VentaControlador(this, venta).mostrar();
    }
}
