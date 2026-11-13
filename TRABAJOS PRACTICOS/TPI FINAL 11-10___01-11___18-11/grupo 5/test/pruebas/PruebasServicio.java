package pruebas;

import modelo.*;
import persistencia.ClienteDAO;
import persistencia.ConexionSQLite;
import servicio.LibreriaServicio;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Statement;

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

    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("tpo-servicio-");
        Path archivo = directorio.resolve("servicio.db");
        Path catalogo = directorio.resolve("catalogo.db");
        Path anterior = directorio.resolve("anterior.db");
        Path prestamos = directorio.resolve("prestamos.db");
        try {
            probarReglas(archivo, catalogo);
            probarPersistencia(anterior);
            probarSociosPrestamosYEdicion(prestamos);
        } finally {
            Files.deleteIfExists(archivo);
            Files.deleteIfExists(catalogo);
            Files.deleteIfExists(anterior);
            Files.deleteIfExists(prestamos);
            Files.delete(directorio);
        }
        System.out.println("OK: " + verificaciones + " verificaciones del servicio");
    }

    private static void probarReglas(Path archivo, Path archivoCatalogo) {
        LibreriaServicio servicio = new LibreriaServicio(archivo);
        Empleado empleado = servicio.empleados().get(0);
        Libro libro = servicio.libros().get(0);
        int inicial = libro.getStock();
        verificar(!servicio.hayVentaEnPreparacion());
        Venta venta = servicio.nuevaVenta(empleado);
        verificar(servicio.hayVentaEnPreparacion());
        venta.agregarLibro(libro, 2);
        venta.aplicarDescuento("HOTSALE");
        Pago pago = Pago.enEfectivo(123L, LocalDateTime.now(), venta.calcularTotal(), new BigDecimal("50000"));
        servicio.confirmar(venta, pago);
        verificar(venta.getEstado() == EstadoVenta.CONFIRMADA);
        verificar(!servicio.hayVentaEnPreparacion());
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
        Pago recuperado = new LibreriaServicio(archivo).ventas().get(0).getPago();
        verificar(recuperado.getId().equals(pago.getId()) && recuperado.calcularVuelto().equals(pago.calcularVuelto()));
        Venta insuficiente = servicio.nuevaVenta(empleado);
        insuficiente.agregarLibro(libro, inicial + 1);
        rechaza(() -> servicio.confirmar(insuficiente, Pago.enEfectivo(null, LocalDateTime.now(),
                insuficiente.calcularTotal(), insuficiente.calcularTotal())));
        verificar(libro.getStock() == inicial && insuficiente.getEstado() == EstadoVenta.EN_PREPARACION);
        servicio.cancelar(insuficiente);
        verificar(!servicio.hayVentaEnPreparacion());
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
        LibreriaServicio catalogo = new LibreriaServicio(archivoCatalogo);
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
        verificar(servicio.clientes().size() == 14 && catalogo.clientes().size() == 13);
        verificar(catalogo.clientes().get(0).getNombre().equals("Ana Belén Rojas"));
        verificar(catalogo.clientes().get(0).getDni().equals("32714682"));
        verificar(catalogo.libros().stream().allMatch(item -> item.getIsbn().matches("[0-9]{13}")));
        verificar(catalogo.libros().size() == 56 && catalogo.libros().get(5).getStock() == 0);

        Venta borrador = catalogo.nuevaVenta(catalogo.empleados().get(0));
        borrador.agregarLibro(editable, 1);
        BigDecimal vuelto = Pago.calcularVuelto(borrador.calcularTotal(), new BigDecimal("200.00"));
        verificar(vuelto.compareTo(new BigDecimal("76.55")) == 0);
        verificar(borrador.getPago() == null && borrador.getEstado() == EstadoVenta.EN_PREPARACION);
        verificar(editable.getStock() == 13);
        rechaza(() -> Pago.calcularVuelto(borrador.calcularTotal(), new BigDecimal("100")));
        verificar(Pago.calcularVuelto(new BigDecimal("10"), new BigDecimal("10.005"))
                .compareTo(new BigDecimal("0.01")) == 0);
    }

    private static void probarPersistencia(Path archivo) throws Exception {
        // Simula un archivo de la versión anterior, que solo tenía clientes.
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("CREATE TABLE clientes (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL, dni TEXT NOT NULL)");
        }
        Cliente existente = new ClienteDAO(archivo).guardar(new Cliente(null, "Cliente previo", "00123456"));
        LibreriaServicio servicio = new LibreriaServicio(archivo);
        verificar(servicio.clientes().get(0).getId().equals(existente.getId()));
        verificar(servicio.clientes().get(0).getDni().equals("00123456"));

        Empleado empleado = servicio.empleados().get(0);
        Libro primero = servicio.libros().get(0);
        Libro segundo = servicio.libros().get(1);
        verificar(primero.getStock() == 10);
        servicio.ingresarStock(primero, 3);
        servicio.cambiarPrecio(primero, new BigDecimal("123.45"));
        Venta venta = servicio.nuevaVenta(empleado);
        venta.cambiarCliente(servicio.clientes().get(0));
        venta.agregarLibro(primero, 2);
        venta.agregarLibro(segundo, 1);
        venta.aplicarDescuento("HOTSALE");
        Pago pago = Pago.conTarjeta(null, LocalDateTime.now(), venta.calcularTotal(), "REF");
        LibreriaServicio otraSesion = new LibreriaServicio(archivo);
        otraSesion.ingresarStock(otraSesion.libros().get(1), 1);

        // Falla el segundo cambio de stock. El primero y todas las filas de la venta se revierten.
        try {
            servicio.confirmar(venta, pago);
            throw new AssertionError("Se esperaba un conflicto de stock");
        } catch (IllegalStateException esperado) {
            verificar(esperado.getMessage().contains("Los datos cambiaron"));
            verificar(venta.getEstado() == EstadoVenta.EN_PREPARACION && venta.getPago() == null && venta.getId() == null);
        }
        LibreriaServicio reabierto = new LibreriaServicio(archivo);
        verificar(reabierto.ventas().isEmpty());
        verificar(reabierto.libros().get(0).getStock() == 13 && reabierto.libros().get(1).getStock() == 6);

        Libro libro = reabierto.libros().get(0);
        Empleado vendedor = reabierto.empleados().get(0);
        Cliente cliente = reabierto.crearCliente("Cliente nuevo", "00042");
        Venta confirmada = reabierto.nuevaVenta(vendedor);
        confirmada.cambiarCliente(cliente);
        confirmada.agregarLibro(libro, 2);
        confirmada.aplicarDescuento("HOTSALE");
        BigDecimal total = confirmada.calcularTotal();
        reabierto.confirmar(confirmada, Pago.conTarjeta(null, LocalDateTime.now(), total, "REF"));
        Venta cancelada = reabierto.nuevaVenta(vendedor);
        cancelada.agregarLibro(libro, 1);
        reabierto.cancelar(cancelada);
        reabierto.cambiarPrecio(libro, new BigDecimal("200.00"));
        reabierto.cambiarActividad(libro, false);

        // Simula una base de la versión que usaba códigos y nombres de demostración.
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("""
                    UPDATE clientes SET nombre = 'Ana Demo', dni = '00000001' WHERE dni = '32714682'
                    """);
            sentencia.executeUpdate("DELETE FROM inicializaciones WHERE nombre = 'catalogo'");
            sentencia.executeUpdate("UPDATE libros SET isbn = 'DEMO-001' WHERE titulo = 'Introducción a Java'");
            sentencia.executeUpdate("""
                    UPDATE detalles_venta SET isbn = 'DEMO-001' WHERE titulo = 'Introducción a Java'
                    """);
        }

        LibreriaServicio historial = new LibreriaServicio(archivo);
        verificar(historial.libros().size() == 56 && historial.empleados().size() == 1);
        verificar(historial.clientes().size() == 15);
        verificar(historial.clientes().get(1).getId().equals(reabierto.clientes().get(1).getId()));
        verificar(historial.clientes().get(1).getNombre().equals("Ana Belén Rojas"));
        verificar(historial.clientes().stream().anyMatch(c -> c.getDni().equals("00042")));
        verificar(historial.ventas().size() == 2 && historial.ventas().get(1).getEstado() == EstadoVenta.CANCELADA);
        Venta recuperada = historial.ventas().get(0);
        verificar(recuperada.getEstado() == EstadoVenta.CONFIRMADA && recuperada.getId().equals(confirmada.getId()));
        verificar(recuperada.getCliente().getId().equals(cliente.getId()));
        verificar(recuperada.getPago().getReferenciaAutorizacion().equals("REF"));
        verificar(recuperada.calcularTotal().equals(total));
        verificar(historial.libros().get(0).getId().equals(libro.getId()));
        verificar(recuperada.getDetalles().get(0).getIsbn().equals(libro.getIsbn()));
        verificar(!historial.libros().get(0).isActivo() && historial.libros().get(0).getStock() == 11);
        verificar(historial.libros().get(0).getPrecioVenta().equals(new BigDecimal("200.00")));
        historial.devolver(recuperada, historial.empleados().get(0), "Motivo", true);
        rechaza(() -> reabierto.devolver(confirmada, vendedor, "Otra devolución", true));

        LibreriaServicio finalizado = new LibreriaServicio(archivo);
        verificar(finalizado.libros().get(0).getStock() == 13 && finalizado.devoluciones().size() == 1);
        Devolucion devolucion = finalizado.devoluciones().get(0);
        verificar(devolucion.getVentaOriginal() == finalizado.ventas().get(0));
        verificar(devolucion.getVentaOriginal().getEstado() == EstadoVenta.DEVUELTA);
        verificar(devolucion.getMotivo().equals("Motivo") && devolucion.getImporteReintegrado().equals(total));
    }
    private static void probarSociosPrestamosYEdicion(Path archivo) {
        LibreriaServicio servicio = new LibreriaServicio(archivo);
        Cliente existente = servicio.clientes().get(0);
        verificar(servicio.asociar("", existente.getDni()).getId().equals(existente.getId()));
        rechaza(() -> servicio.asociar("Otro nombre", existente.getDni()));
        Cliente nuevo = servicio.asociar("Socia de prueba", "00042");
        verificar(servicio.clientes().size() == 14 && nuevo.getDni().equals("00042"));

        Libro ejemplo = servicio.libros().get(0);
        Long ejemploId = ejemplo.getId();
        servicio.guardarLibro(ejemplo, "9780000000001", "Java actualizado", List.of("Autora"),
                "Editorial nueva", "Informática", new BigDecimal("24000"), 5, true);
        verificar(ejemplo.getTitulo().equals("Java actualizado") && ejemplo.getStock() == 5);
        rechaza(() -> servicio.guardarLibro(null, ejemplo.getIsbn(), "Duplicado", List.of("Autora"),
                "Editorial", "Categoría", BigDecimal.TEN, 1, true));
        servicio.guardarLibro(null, "9780000000002", "Libro nuevo", List.of("Ana", "Bruno"),
                "Editorial nueva", "Informática", BigDecimal.TEN, 2, true);
        Libro libro = servicio.libros().get(56);
        rechaza(() -> servicio.prestar("111", libro));
        verificar(libro.getStock() == 2 && servicio.prestamos().isEmpty());
        servicio.prestar(nuevo.getDni(), libro);
        verificar(libro.getStock() == 1 && servicio.prestamos().size() == 1);

        Venta venta = servicio.nuevaVenta(servicio.empleadoPredeterminado());
        venta.agregarLibro(libro, 1);
        servicio.confirmar(venta, Pago.enEfectivo(null, LocalDateTime.now(), venta.calcularTotal(), BigDecimal.TEN));
        verificar(libro.getStock() == 0);
        rechaza(() -> servicio.prestar(nuevo.getDni(), libro));

        LibreriaServicio reabierto = new LibreriaServicio(archivo);
        Libro recuperado = reabierto.libros().get(56);
        verificar(reabierto.libros().size() == 57 && reabierto.libros().get(0).getId().equals(ejemploId));
        verificar(reabierto.libros().get(0).getIsbn().equals("9780000000001"));
        verificar(recuperado.getStock() == 0 && recuperado.getAutores().size() == 2);
        Prestamo prestamo = reabierto.prestamos().get(0);
        verificar(prestamo.devueltoEn() == null && prestamo.dni().equals(nuevo.getDni()));
        reabierto.devolverPrestamo(prestamo);
        rechaza(() -> reabierto.devolverPrestamo(prestamo));
        verificar(recuperado.getStock() == 1);
        reabierto.guardarLibro(recuperado, "9780000000003", "Título editado", List.of("Ana"),
                "Editorial editada", "Categoría editada", BigDecimal.ONE, 1, false);
        LibreriaServicio finalizado = new LibreriaServicio(archivo);
        verificar(finalizado.libros().get(56).getStock() == 1);
        verificar(finalizado.prestamos().get(0).devueltoEn() != null);
        verificar(finalizado.ventas().get(0).getDetalles().get(0).getTitulo().equals("Libro nuevo"));
        verificar(!finalizado.libros().get(56).isActivo());
    }
}
