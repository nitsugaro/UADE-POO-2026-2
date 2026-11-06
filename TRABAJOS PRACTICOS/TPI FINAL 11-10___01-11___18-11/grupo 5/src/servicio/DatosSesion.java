package servicio;

import modelo.*;
import java.util.ArrayList;
import java.util.List;

// Se descarta al cerrar la aplicación.
final class DatosSesion {
    private final List<Libro> libros = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();
    private final List<Devolucion> devoluciones = new ArrayList<>();
    private long siguienteCliente = 1;
    private long siguienteVenta = 1;
    private long siguienteDevolucion = 1;

    List<Libro> libros() {
        return List.copyOf(libros);
    }

    List<Cliente> clientes() {
        return List.copyOf(clientes);
    }

    List<Empleado> empleados() {
        return List.copyOf(empleados);
    }

    List<Venta> ventas() {
        return List.copyOf(ventas);
    }

    List<Devolucion> devoluciones() {
        return List.copyOf(devoluciones);
    }

    void agregarLibro(Libro libro) {
        libros.add(libro);
    }

    void agregarEmpleado(Empleado empleado) {
        empleados.add(empleado);
    }

    void guardarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

    void guardarVenta(Venta venta) {
        ventas.add(venta);
    }

    void guardarDevolucion(Devolucion devolucion) {
        devoluciones.add(devolucion);
    }

    long siguienteCliente() {
        return siguienteCliente++;
    }

    long siguienteVenta() {
        return siguienteVenta++;
    }

    long siguienteDevolucion() {
        return siguienteDevolucion++;
    }
}
