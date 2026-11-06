package servicio;

import modelo.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/** Almacen en memoria de una sesion. Todas las operaciones se ejecutan en el EDT. */
public final class VentaServicio {
    private final List<Libro> libros = new ArrayList<>();
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Empleado> empleados = new ArrayList<>();
    private final List<Venta> ventas = new ArrayList<>();
    private final List<Devolucion> devoluciones = new ArrayList<>();
    private long siguienteVenta = 1;

    public VentaServicio() {
        empleados.add(new Empleado(1L, "E001", "Empleado de ejemplo", true));
        Autor autor = new Autor(1L, "Autor de ejemplo");
        Editorial editorial = new Editorial(1L, "Editorial de ejemplo");
        Categoria categoria = new Categoria(1L, "Programación");
        libros.add(new Libro(1L, "DEMO-001", "Introducción a Java", new ArrayList<>(List.of(autor)), editorial,
                categoria, new BigDecimal("25000"), 10, true));
        libros.add(new Libro(2L, "DEMO-002", "Programación orientada a objetos", new ArrayList<>(List.of(autor)),
                editorial, categoria, new BigDecimal("32000"), 5, true));
    }

    public List<Libro> libros() {
        return List.copyOf(libros);
    }

    public List<Cliente> clientes() {
        return List.copyOf(clientes);
    }

    public List<Empleado> empleados() {
        return List.copyOf(empleados);
    }

    public List<Venta> ventas() {
        return List.copyOf(ventas);
    }

    public List<Devolucion> devoluciones() {
        return List.copyOf(devoluciones);
    }

    public Cliente crearCliente(String nombre, String dni) {
        Cliente cliente = new Cliente((long) clientes.size() + 1, nombre, dni);
        clientes.add(cliente);
        return cliente;
    }

    public Venta nuevaVenta(Empleado empleado) {
        if (empleado == null || !empleado.isActivo()) {
            throw new IllegalArgumentException("Seleccioná un empleado activo");
        }
        Venta venta = new Venta(siguienteVenta++, LocalDateTime.now(), empleado, null);
        ventas.add(venta);
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
        Devolucion devolucion = venta.completarDevolucion((long) devoluciones.size() + 1,
                LocalDateTime.now(), responsable, motivo, aptos);
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.getLibro().aumentarStock(detalle.getCantidad());
        }
        devoluciones.add(devolucion);
        return devolucion;
    }

    private void exigirVenta(Venta venta) {
        if (!ventas.contains(venta)) {
            throw new IllegalArgumentException("La venta no pertenece a esta sesión");
        }
    }
}
