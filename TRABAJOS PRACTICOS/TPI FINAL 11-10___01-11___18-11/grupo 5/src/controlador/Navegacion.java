package controlador;

import modelo.Venta;
import servicio.LibreriaServicio;
import vista.VentanaPrincipal;

public final class Navegacion {
    final VentanaPrincipal ventana;
    final LibreriaServicio servicio;

    public Navegacion(VentanaPrincipal ventana, LibreriaServicio servicio) {
        this.ventana = ventana;
        this.servicio = servicio;
    }

    public void mostrarInicio() {
        new InicioControlador(this).mostrar();
    }

    void mostrarHistorial() {
        new HistorialVentasControlador(this).mostrar();
    }

    void mostrarCatalogo() {
        new CatalogoControlador(this).mostrar();
    }

    void mostrarCategorias() {
        new CategoriaControlador(this).mostrar();
    }

    void mostrarEditoriales() {
        new EditorialControlador(this).mostrar();
    }

    void mostrarAdministracionClientes() {
        new AdministrarClientesControlador(this).mostrar();
    }

    void mostrarDetalle(Venta venta) {
        new HistorialVentasControlador(this).mostrarDetalle(venta);
    }

    void mostrarDevolucion(Venta venta) {
        new DevolucionControlador(this, venta).mostrar();
    }

    void mostrarClientes(Venta venta, Runnable volver, boolean nuevo) {
        new ClienteControlador(this, venta, volver).mostrar(nuevo);
    }

    void mostrarPago(Venta venta, Runnable volver) {
        new PagoControlador(this, venta, volver).mostrar();
    }

    void nuevaVenta() {
        try {
            Venta venta = servicio.nuevaVenta(servicio.empleadoPredeterminado());
            new VentaControlador(this, venta).mostrar();
        } catch (IllegalArgumentException | IllegalStateException error) {
            ventana.error(error);
        }
    }
}
