package pruebas;

import modelo.*;
import servicio.LibreriaServicio;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class PruebasServicio {
    private static int verificaciones;
    private static void verificar(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("Verificación " + (verificaciones + 1));
        }
        verificaciones++;
    }

    private static void rechaza(Runnable operacion) {
        try {
            operacion.run();
        } catch (RuntimeException esperado) {
            verificaciones++;
            return;
        }
        throw new AssertionError("Se esperaba rechazo");
    }

    public static void main(String[] args) {
        LibreriaServicio servicio = new LibreriaServicio();
        Empleado empleado = servicio.empleados().get(0);
        Libro libro = servicio.libros().get(0);
        int inicial = libro.getStock();
        Venta venta = servicio.nuevaVenta(empleado);
        venta.agregarLibro(libro, 2);
        venta.aplicarDescuento("HOTSALE");
        Pago pago = Pago.enEfectivo(null, LocalDateTime.now(), venta.calcularTotal(), new BigDecimal("50000"));
        servicio.confirmar(venta, pago);
        verificar(venta.getEstado() == EstadoVenta.CONFIRMADA);
        verificar(libro.getStock() == inicial - 2);
        rechaza(() -> servicio.confirmar(venta, pago));
        verificar(libro.getStock() == inicial - 2);
        rechaza(() -> servicio.devolver(venta, empleado, "", true));
        verificar(venta.getEstado() == EstadoVenta.CONFIRMADA && libro.getStock() == inicial - 2);
        Devolucion devolucion = servicio.devolver(venta, empleado, "Compra equivocada", true);
        verificar(libro.getStock() == inicial && venta.getEstado() == EstadoVenta.DEVUELTA);
        verificar(devolucion.getImporteReintegrado().compareTo(pago.getImportePagado()) == 0);
        rechaza(() -> servicio.devolver(venta, empleado, "Otra vez", true));
        verificar(libro.getStock() == inicial && servicio.devoluciones().size() == 1);
        Venta insuficiente = servicio.nuevaVenta(empleado);
        insuficiente.agregarLibro(libro, inicial + 1);
        rechaza(() -> servicio.confirmar(insuficiente, Pago.enEfectivo(null, LocalDateTime.now(),
                insuficiente.calcularTotal(), insuficiente.calcularTotal())));
        verificar(libro.getStock() == inicial && insuficiente.getEstado() == EstadoVenta.EN_PREPARACION);
        insuficiente.cancelar();
        verificar(libro.getStock() == inicial);
        Venta tarjeta = servicio.nuevaVenta(empleado);
        tarjeta.agregarLibro(libro, 1);
        Pago pagoTarjeta = Pago.conTarjeta(null, LocalDateTime.now(), tarjeta.calcularTotal(), "DEMO");
        rechaza(() -> servicio.confirmar(tarjeta, pagoTarjeta));
        verificar(libro.getStock() == inicial);
        tarjeta.cambiarCliente(servicio.crearCliente("Ana", "00123456"));
        servicio.confirmar(tarjeta, pagoTarjeta);
        verificar(libro.getStock() == inicial - 1);
        libro.aumentarStock(Integer.MAX_VALUE - libro.getStock());
        rechaza(() -> servicio.devolver(tarjeta, empleado, "Prueba de límite", true));
        verificar(tarjeta.getEstado() == EstadoVenta.CONFIRMADA && libro.getStock() == Integer.MAX_VALUE);
        LibreriaServicio catalogo = new LibreriaServicio();
        Libro editable = catalogo.libros().get(0);
        catalogo.cambiarPrecio(editable, new BigDecimal("123.45"));
        verificar(editable.getPrecioVenta().compareTo(new BigDecimal("123.45")) == 0);
        catalogo.ingresarStock(editable, 3);
        verificar(editable.getStock() == 13);
        catalogo.cambiarActividad(editable, false);
        verificar(!editable.isActivo());
        catalogo.cambiarActividad(editable, true);
        verificar(editable.isActivo());
        rechaza(() -> catalogo.cambiarPrecio(editable, BigDecimal.ZERO));
        verificar(editable.getPrecioVenta().compareTo(new BigDecimal("123.45")) == 0);
        rechaza(() -> catalogo.ingresarStock(libro, 1));
        verificar(servicio.clientes().size() == 1 && catalogo.clientes().isEmpty());

        Venta borrador = catalogo.nuevaVenta(catalogo.empleados().get(0));
        borrador.agregarLibro(editable, 1);
        BigDecimal vuelto = Pago.calcularVuelto(borrador.calcularTotal(), new BigDecimal("200.00"));
        verificar(vuelto.compareTo(new BigDecimal("76.55")) == 0);
        verificar(borrador.getPago() == null && borrador.getEstado() == EstadoVenta.EN_PREPARACION);
        verificar(editable.getStock() == 13);
        rechaza(() -> Pago.calcularVuelto(borrador.calcularTotal(), new BigDecimal("100")));
        verificar(Pago.calcularVuelto(new BigDecimal("10"), new BigDecimal("10.005"))
                .compareTo(new BigDecimal("0.01")) == 0);

        System.out.println("OK: " + verificaciones + " verificaciones del servicio");
    }
}
