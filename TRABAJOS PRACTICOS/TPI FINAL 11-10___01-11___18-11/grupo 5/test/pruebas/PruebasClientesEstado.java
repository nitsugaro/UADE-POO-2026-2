package pruebas;

import modelo.*;
import persistencia.*;
import servicio.LibreriaServicio;
import java.nio.file.*;
import java.time.LocalDateTime;

public final class PruebasClientesEstado {
    private static int verificaciones;
    private static void exigir(boolean b) { if (!b) throw new AssertionError("Control " + verificaciones); verificaciones++; }
    private static void rechaza(Runnable r) {
        try { r.run(); } catch (IllegalStateException | IllegalArgumentException e) { verificaciones++; return; }
        throw new AssertionError("Se esperaba rechazo");
    }
    private static Cliente cliente(LibreriaServicio s, long id) {
        return s.clientes().stream().filter(c -> c.getId() == id).findFirst().orElseThrow();
    }
    private static Pago pago(Venta v) { return Pago.enEfectivo(null, LocalDateTime.now(), v.calcularTotal(), v.calcularTotal()); }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("tpo-cliente-estado-");
        Path db = dir.resolve("prueba.db");
        try {
            LibreriaServicio s = new LibreriaServicio(db);
            Cliente c = s.crearCliente("Estado prueba", "000987654");
            long id = c.getId();
            s.asociar("", c.getDni());
            Libro l = s.libros().get(0);
            Venta previa = s.nuevaVenta(s.empleadoPredeterminado());
            previa.cambiarCliente(c); previa.agregarLibro(l, 1); s.confirmar(previa, pago(previa));
            s.prestar(c.getDni(), l);
            int stock = l.getStock();
            // Recrea exactamente el esquema anterior conservando ventas, socios y préstamos.
            try (var con = ConexionSQLite.abrir(db); var st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE clientes DROP COLUMN activo");
            }
            s = new LibreriaServicio(db);
            c = cliente(s, id); l = s.libros().get(0);
            exigir(c.isActivo() && c.getDni().equals("000987654"));
            exigir(s.ventas().get(0).getCliente().getId() == id && s.socios().stream().anyMatch(x -> x.getId() == id));
            exigir(s.prestamos().size() == 1 && l.getStock() == stock);
            Venta borrador = s.nuevaVenta(s.empleadoPredeterminado());
            borrador.cambiarCliente(c); borrador.agregarLibro(l, 1);
            LibreriaServicio otra = new LibreriaServicio(db);
            otra.cambiarActividadCliente(cliente(otra, id), false);
            final LibreriaServicio antigua = s;
            rechaza(() -> antigua.confirmar(borrador, pago(borrador))); // objeto aún activo, SQL rechaza
            exigir(borrador.getId() == null && borrador.getEstado() == EstadoVenta.EN_PREPARACION && l.getStock() == stock);
            LibreriaServicio r = new LibreriaServicio(db);
            Cliente inactivo = cliente(r, id);
            Libro libro = r.libros().get(0);
            exigir(!inactivo.isActivo());
            rechaza(() -> r.prestar(inactivo.getDni(), libro));
            rechaza(() -> r.asociar("", inactivo.getDni()));
            Venta v = r.nuevaVenta(r.empleadoPredeterminado());
            v.cambiarCliente(inactivo); v.agregarLibro(libro, 1);
            rechaza(() -> r.confirmar(v, pago(v)));
            exigir(libro.getStock() == stock && r.prestamos().size() == 1);
            LibreriaServicio sinCambios = new LibreriaServicio(db);
            exigir(sinCambios.ventas().size() == 1 && sinCambios.prestamos().size() == 1);
            Prestamo prestamo = r.prestamos().get(0);
            r.devolverPrestamo(prestamo);
            exigir(libro.getStock() == stock + 1);
            rechaza(() -> r.devolverPrestamo(prestamo));
            exigir(libro.getStock() == stock + 1);
            r.cambiarActividadCliente(inactivo, true);
            exigir(inactivo.getId() == id && r.socios().stream().anyMatch(x -> x.getId() == id && x.isActivo()));
            r.prestar(inactivo.getDni(), libro);
            exigir(libro.getStock() == stock && r.prestamos().size() == 2);
            r.cambiarActividadCliente(inactivo, false);
            rechaza(() -> r.confirmar(v, pago(v))); // referencia actualizada en misma sesión
            v.cambiarCliente(null);
            r.confirmar(v, pago(v)); // efectivo sin cliente continúa permitido
            LibreriaServicio fin = new LibreriaServicio(db);
            exigir(!cliente(fin, id).isActivo() && fin.prestamos().size() == 2 && fin.ventas().size() == 2);
            exigir(fin.prestamos().stream().filter(p -> p.devueltoEn() != null).count() == 1);
            fin.cambiarActividadCliente(cliente(fin, id), true);
            exigir(cliente(new LibreriaServicio(db), id).isActivo());
            System.out.println("OK: " + verificaciones + " verificaciones de estado de clientes");
        } finally { Files.deleteIfExists(db); Files.delete(dir); }
    }
}
