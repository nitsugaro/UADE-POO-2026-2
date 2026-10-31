package pruebas;

import excepciones.DevolucionNoPermitidaException;
import excepciones.StockInsuficienteException;
import modelo.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Pruebas sin dependencias; los controles funcionan sin habilitar -ea. */
public final class PruebasModelo {
    private static int verificaciones;
    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 10, 10, 23, 30);

    private static BigDecimal dinero(String valor) { return new BigDecimal(valor); }
    private static Empleado empleado() { return new Empleado(null, "E1", "Ana", true); }
    private static Libro libro(String precio, int stock) {
        return new Libro(null, "978-test", "POO", Arrays.asList(new Autor(null, "Autor")),
                new Editorial(null, "Editorial"), new Categoria(null, "Programacion"),
                dinero(precio), stock, true);
    }
    private static Venta venta(Libro libro) {
        Venta venta = new Venta(null, FECHA, empleado(), null);
        venta.agregarLibro(libro, 1);
        return venta;
    }
    private static void pagar(Venta venta) {
        venta.registrarPago(Pago.enEfectivo(null, FECHA, venta.calcularTotal(), venta.calcularTotal()));
    }
    private static Venta confirmada() {
        Venta venta = venta(libro("100", 10));
        venta.aplicarDescuento("HOTSALE");
        pagar(venta);
        venta.confirmar();
        return venta;
    }
    private static void comprobar(boolean condicion) {
        if (!condicion) throw new AssertionError("Fallo en verificacion " + (verificaciones + 1));
        verificaciones++;
    }
    private static void importe(String esperado, BigDecimal real) {
        comprobar(dinero(esperado).compareTo(real) == 0 && real.scale() == 2);
    }
    private static void falla(Class<? extends Throwable> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (Throwable error) {
            if (!tipo.isInstance(error)) throw new AssertionError("Excepcion inesperada", error);
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba " + tipo.getSimpleName());
    }

    public static void main(String[] args) {
        totalesEHistoria();
        cantidadesYStock();
        pagos();
        estados();
        devoluciones();
        validacionesYColecciones();
        System.out.println("OK: " + verificaciones + " verificaciones del modelo");
    }

    private static void totalesEHistoria() {
        Libro libro = libro("10.05", 10);
        Venta venta = venta(libro);
        venta.aplicarDescuento("HOTSALE");
        importe("10.05", venta.calcularSubtotal());
        importe("1.01", venta.calcularDescuento());
        importe("9.04", venta.calcularTotal());
        falla(IllegalStateException.class, () -> venta.aplicarDescuento("HOTSALE"));
        falla(IllegalArgumentException.class, () -> Descuento.desdeCodigo("OTRO"));
        libro.cambiarPrecio(dinero("20.125"));
        importe("20.13", libro.getPrecioVenta());
        venta.agregarLibro(libro, 1);
        comprobar(venta.getDetalles().size() == 1);
        importe("10.05", venta.getDetalles().get(0).getPrecioUnitario());
        importe("20.10", venta.calcularSubtotal());
        comprobar(venta.getDetalles().get(0).getTitulo().equals("POO"));
        comprobar(venta.getDetalles().get(0).getIsbn().equals("978-test"));
        venta.quitarDescuento();
        importe("0.00", venta.calcularDescuento());
    }

    private static void cantidadesYStock() {
        Libro libro = libro("10", 2);
        Venta venta = venta(libro);
        venta.agregarLibro(libro, 2);
        comprobar(venta.getDetalles().get(0).getCantidad() == 3);
        pagar(venta);
        falla(StockInsuficienteException.class, venta::confirmar);
        comprobar(venta.getEstado() == EstadoVenta.EN_PREPARACION);
        venta.modificarCantidad(libro, 2);
        pagar(venta);
        venta.confirmar();
        comprobar(libro.getStock() == 2);
        falla(StockInsuficienteException.class, () -> libro.disminuirStock(3));
        comprobar(libro.getStock() == 2);
        libro.disminuirStock(2);
        comprobar(!libro.hayDisponibilidad(1));
        libro.aumentarStock(3);
        comprobar(libro.hayDisponibilidad(3));
        falla(IllegalArgumentException.class, () -> libro.aumentarStock(0));
        falla(IllegalArgumentException.class, () -> libro.disminuirStock(-1));
        falla(IllegalArgumentException.class, () -> libro.hayDisponibilidad(0));
        Libro maximo = libro("1", Integer.MAX_VALUE);
        falla(ArithmeticException.class, () -> maximo.aumentarStock(1));
        comprobar(maximo.getStock() == Integer.MAX_VALUE);
    }

    private static void pagos() {
        Pago efectivo = Pago.enEfectivo(null, FECHA, dinero("90"), dinero("100"));
        importe("10", efectivo.calcularVuelto());
        importe("90", efectivo.getImportePagado());
        falla(IllegalArgumentException.class,
                () -> Pago.enEfectivo(null, FECHA, dinero("90"), dinero("89.99")));
        Venta venta = venta(libro("90", 10));
        venta.registrarPago(efectivo);
        venta.confirmar();
        comprobar(venta.getCliente() == null);
        Venta tarjeta = venta(libro("90", 10));
        tarjeta.registrarPago(Pago.conTarjeta(null, FECHA, dinero("90.000"), "SIM-001"));
        falla(IllegalStateException.class, tarjeta::confirmar);
        tarjeta.cambiarCliente(new Cliente(null, "Jose", "00123456"));
        tarjeta.confirmar();
        comprobar(tarjeta.getCliente().getDni().equals("00123456"));
        importe("0", tarjeta.getPago().calcularVuelto());
        Devolucion dev = tarjeta.completarDevolucion(null, FECHA, empleado(), "No lo necesita", true);
        comprobar(dev.getMedioReintegro() == MedioPago.TARJETA);
        importe("90", dev.getImporteReintegrado());
        Venta incorrecta = venta(libro("10", 10));
        incorrecta.registrarPago(efectivo);
        falla(IllegalStateException.class, incorrecta::confirmar);
        falla(IllegalArgumentException.class,
                () -> Pago.conTarjeta(null, FECHA, dinero("10"), " "));
    }

    private static void estados() {
        Venta venta = confirmada();
        Libro libro = venta.getDetalles().get(0).getLibro();
        falla(IllegalStateException.class, venta::confirmar);
        falla(IllegalStateException.class, () -> venta.agregarLibro(libro, 1));
        falla(IllegalStateException.class, () -> venta.modificarCantidad(libro, 2));
        falla(IllegalStateException.class, () -> venta.quitarLibro(libro));
        falla(IllegalStateException.class, () -> venta.cambiarCliente(null));
        falla(IllegalStateException.class, () -> venta.aplicarDescuento("HOTSALE"));
        falla(IllegalStateException.class, venta::quitarDescuento);
        falla(IllegalStateException.class, () -> venta.registrarPago(venta.getPago()));
        falla(IllegalStateException.class, venta::quitarPago);
        falla(IllegalStateException.class, venta::cancelar);
        Venta cancelada = venta(libro);
        cancelada.cancelar();
        comprobar(cancelada.getEstado() == EstadoVenta.CANCELADA && libro.getStock() == 10);
        falla(IllegalStateException.class, cancelada::confirmar);
        falla(IllegalStateException.class, cancelada::cancelar);
        Venta vacia = new Venta(null, FECHA, empleado(), null);
        falla(IllegalStateException.class, vacia::confirmar);
        Venta sinPago = venta(libro);
        falla(IllegalStateException.class, sinPago::confirmar);
        pagar(sinPago);
        sinPago.getEmpleado().desactivar();
        falla(IllegalStateException.class, sinPago::confirmar);
        sinPago.getEmpleado().activar();
        libro.desactivar();
        falla(IllegalStateException.class, sinPago::confirmar);
        libro.activar();
        sinPago.quitarLibro(libro);
        comprobar(sinPago.getDetalles().isEmpty());
    }

    private static void devoluciones() {
        PoliticaDevolucion politica = new PoliticaDevolucion();
        Venta venta = confirmada();
        politica.validar(venta, FECHA.plusDays(3), "Motivo", true);
        comprobar(venta.getEstado() == EstadoVenta.CONFIRMADA);
        politica.validar(venta, FECHA.withHour(0), "Mismo dia calendario", true);
        comprobar(venta.getEstado() == EstadoVenta.CONFIRMADA);
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(venta, FECHA.minusDays(1), "Motivo", true));
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(venta, FECHA.plusDays(8).withHour(0), "Motivo", true));
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(venta, FECHA, " ", true));
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(venta, FECHA, "Motivo", false));
        falla(DevolucionNoPermitidaException.class,
                () -> venta.completarDevolucion(null, FECHA, null, "Motivo", true));
        falla(IllegalArgumentException.class,
                () -> venta.completarDevolucion(-1L, FECHA, empleado(), "Motivo", true));
        comprobar(venta.getEstado() == EstadoVenta.CONFIRMADA);
        Devolucion devolucion = venta.completarDevolucion(null,
                FECHA.plusDays(7).withHour(23).withMinute(59), empleado(), "Motivo", true);
        comprobar(venta.getEstado() == EstadoVenta.DEVUELTA);
        importe("90", devolucion.getImporteReintegrado());
        comprobar(devolucion.getMedioReintegro() == MedioPago.EFECTIVO);
        comprobar(devolucion.getVentaOriginal() == venta);
        comprobar(venta.getDetalles().get(0).getLibro().getStock() == 10);
        falla(DevolucionNoPermitidaException.class,
                () -> venta.completarDevolucion(null, FECHA.plusDays(7), empleado(), "Motivo", true));
        Venta preparada = venta(libro("1", 1));
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(preparada, FECHA, "Motivo", true));
        preparada.cancelar();
        falla(DevolucionNoPermitidaException.class,
                () -> politica.validar(preparada, FECHA, "Motivo", true));
        Venta dentro = confirmada();
        dentro.completarDevolucion(null, FECHA.plusDays(2), empleado(), "Motivo", true);
        comprobar(dentro.getEstado() == EstadoVenta.DEVUELTA);
    }

    private static void validacionesYColecciones() {
        falla(IllegalArgumentException.class, () -> new Autor(0L, "Autor"));
        falla(IllegalArgumentException.class, () -> new Cliente(null, "", "123"));
        falla(IllegalArgumentException.class, () -> new Cliente(null, "Nombre", "abc"));
        falla(IllegalArgumentException.class, () -> libro("0.004", 1));
        falla(IllegalArgumentException.class, () -> libro("1", -1));
        Libro libro = libro("1", 10);
        Venta venta = venta(libro);
        List<DetalleVenta> copia = venta.getDetalles();
        falla(UnsupportedOperationException.class, copia::clear);
        falla(UnsupportedOperationException.class, () -> libro.getAutores().clear());
        venta.modificarCantidad(libro, 3);
        comprobar(copia.get(0).getCantidad() == 1);
        falla(IllegalArgumentException.class, () -> venta.modificarCantidad(libro, 0));
        falla(IllegalArgumentException.class, () -> venta.agregarLibro(libro, -1));
        List<Autor> autores = new ArrayList<>(libro.getAutores());
        Libro otro = new Libro(1L, "A", "A", autores, libro.getEditorial(), libro.getCategoria(),
                dinero("1"), 10, true);
        autores.clear();
        comprobar(otro.getAutores().size() == 1);
        falla(IllegalArgumentException.class, () -> new Libro(null, "A", "A", autores,
                libro.getEditorial(), libro.getCategoria(), dinero("1"), 0, true));
        Libro mismaIdentidad = new Libro(1L, "A", "A", otro.getAutores(), otro.getEditorial(),
                otro.getCategoria(), dinero("5"), 10, true);
        Venta identidad = venta(otro);
        identidad.agregarLibro(mismaIdentidad, 1);
        comprobar(identidad.getDetalles().size() == 1);
        importe("2", identidad.calcularTotal());
        venta.agregarLibro(libro("1", 10), 1);
        comprobar(venta.getDetalles().size() == 2);
        comprobar(libro.getId() == null && otro.getId() == 1L);
    }
}
