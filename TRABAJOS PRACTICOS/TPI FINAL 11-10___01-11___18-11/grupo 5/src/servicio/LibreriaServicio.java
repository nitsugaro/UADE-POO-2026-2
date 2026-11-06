package servicio;

import modelo.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

// Coordina las operaciones de la librería. Se usa desde el hilo de Swing.
public final class LibreriaServicio {
    private final DatosSesion datos;

    public LibreriaServicio() {
        datos = DatosEjemplo.crear();
    }

    public List<Libro> libros() {
        return datos.libros();
    }

    public List<Cliente> clientes() {
        return datos.clientes();
    }

    public List<Empleado> empleados() {
        return datos.empleados();
    }

    public List<Venta> ventas() {
        return datos.ventas();
    }

    public List<Devolucion> devoluciones() {
        return datos.devoluciones();
    }

    public Cliente crearCliente(String nombre, String dni) {
        Cliente cliente = new Cliente(datos.siguienteCliente(), nombre, dni);
        datos.guardarCliente(cliente);
        return cliente;
    }

    public Venta nuevaVenta(Empleado empleado) {
        if (empleado == null || !empleado.isActivo()) {
            throw new IllegalArgumentException("Seleccioná un empleado activo");
        }
        Venta venta = new Venta(datos.siguienteVenta(), LocalDateTime.now(), empleado, null);
        datos.guardarVenta(venta);
        return venta;
    }

    public void confirmar(Venta venta, Pago pago) {
        exigirVenta(venta);
        venta.registrarPago(pago);
        venta.confirmar();
        // Valida toda la venta antes de modificar stock.
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.getLibro().disminuirStock(detalle.getCantidad());
        }
    }

    public Devolucion devolver(Venta venta, Empleado responsable, String motivo, boolean aptos) {
        exigirVenta(venta);
        // Verificar desbordamiento antes de cambiar estado o stock.
        for (DetalleVenta detalle : venta.getDetalles()) {
            Math.addExact(detalle.getLibro().getStock(), detalle.getCantidad());
        }
        Devolucion devolucion = venta.completarDevolucion(datos.siguienteDevolucion(),
                LocalDateTime.now(), responsable, motivo, aptos);
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.getLibro().aumentarStock(detalle.getCantidad());
        }
        datos.guardarDevolucion(devolucion);
        return devolucion;
    }

    public void cambiarPrecio(Libro libro, BigDecimal precio) {
        exigirLibro(libro);
        libro.cambiarPrecio(precio);
    }

    public void ingresarStock(Libro libro, int cantidad) {
        exigirLibro(libro);
        libro.aumentarStock(cantidad);
    }

    public void cambiarActividad(Libro libro, boolean activo) {
        exigirLibro(libro);
        if (activo) {
            libro.activar();
        } else {
            libro.desactivar();
        }
    }

    private void exigirLibro(Libro libro) {
        if (!datos.libros().contains(libro)) {
            throw new IllegalArgumentException("El libro no pertenece al catálogo");
        }
    }

    private void exigirVenta(Venta venta) {
        if (!datos.ventas().contains(venta)) {
            throw new IllegalArgumentException("La venta no pertenece a esta sesión");
        }
    }
}
