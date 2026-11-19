package pruebas;

import modelo.*;
import servicio.LibreriaServicio;
import persistencia.ClienteDAO;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.concurrent.*;

public final class PruebasClientesEdicion {
    private static int verificaciones;
    private static void exigir(boolean b) { if (!b) throw new AssertionError("Control " + verificaciones); verificaciones++; }
    private static void rechaza(Runnable r) {
        try { r.run(); } catch (IllegalArgumentException | IllegalStateException e) { verificaciones++; return; }
        throw new AssertionError("Se esperaba rechazo");
    }
    private static Cliente cliente(LibreriaServicio s, long id) { return s.clientes().stream().filter(c -> c.getId() == id).findFirst().orElseThrow(); }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("tpo-edicion-clientes-");
        Path db = dir.resolve("prueba.db");
        try {
            LibreriaServicio s = new LibreriaServicio(db);
            Cliente c = s.crearCliente("Original", "00999111");
            long id = c.getId();
            s.asociar("", c.getDni());
            Libro libro = s.libros().get(0);
            Venta venta = s.nuevaVenta(s.empleadoPredeterminado());
            venta.cambiarCliente(c); venta.agregarLibro(libro, 1);
            s.confirmar(venta, Pago.enEfectivo(null, LocalDateTime.now(), venta.calcularTotal(), venta.calcularTotal()));
            s.prestar(c.getDni(), libro);
            long prestamoId = s.prestamos().get(0).id();
            s.modificarCliente(c, "Corregido", "0000999222");
            exigir(c.getId() == id && c.getDni().equals("0000999222"));
            exigir(venta.getCliente().getNombre().equals("Corregido"));
            exigir(s.socios().stream().anyMatch(x -> x.getId() == id && x.getDni().equals(c.getDni())));
            exigir(s.prestamos().get(0).id() == prestamoId && s.prestamos().get(0).socio().equals("Corregido") && s.prestamos().get(0).dni().equals(c.getDni()));
            Cliente ocupado = s.crearCliente("Ocupado", "00999333");
            rechaza(() -> s.modificarCliente(c, "No guardar", ocupado.getDni()));
            exigir(c.getNombre().equals("Corregido") && c.getDni().equals("0000999222"));
            s.cambiarActividadCliente(ocupado, false);
            rechaza(() -> s.modificarCliente(c, "Tampoco guardar", ocupado.getDni()));
            Cliente persistido = new ClienteDAO(db).buscarPorId(id).orElseThrow();
            exigir(persistido.getNombre().equals("Corregido") && persistido.getDni().equals("0000999222"));
            rechaza(() -> s.modificarCliente(c, " ", "0000999222"));
            rechaza(() -> s.modificarCliente(c, "No guardar", "abc"));
            long duplicadoId;
            try (var con = persistencia.ConexionSQLite.abrir(db);
                    var st = con.prepareStatement("INSERT INTO clientes(nombre,dni) VALUES (?,?) RETURNING id")) {
                st.setString(1, "Duplicado histórico"); st.setString(2, c.getDni());
                try (var rs = st.executeQuery()) { rs.next(); duplicadoId = rs.getLong(1); }
            }
            LibreriaServicio historico = new LibreriaServicio(db);
            Cliente duplicado = cliente(historico, duplicadoId);
            historico.modificarCliente(duplicado, "Nombre corregido duplicado", duplicado.getDni());
            exigir(duplicado.getNombre().equals("Nombre corregido duplicado") && c.getNombre().equals("Corregido"));
            historico.modificarCliente(duplicado, duplicado.getNombre(), "0000999444");
            exigir(duplicado.getDni().equals("0000999444"));
            s.cambiarActividadCliente(c, false);
            s.modificarCliente(c, "Inactivo corregido", "0000999555");
            exigir(!c.isActivo() && c.getId() == id);
            LibreriaServicio reabierto = new LibreriaServicio(db);
            Cliente recuperado = cliente(reabierto, id);
            exigir(!recuperado.isActivo() && recuperado.getNombre().equals("Inactivo corregido") && recuperado.getDni().equals("0000999555"));
            exigir(reabierto.ventas().get(0).getCliente().getId() == id && reabierto.ventas().get(0).getCliente().getNombre().equals(recuperado.getNombre()));
            exigir(reabierto.socios().stream().anyMatch(x -> x.getId() == id && x.getDni().equals(recuperado.getDni())));
            exigir(reabierto.prestamos().get(0).id() == prestamoId && reabierto.prestamos().get(0).dni().equals(recuperado.getDni()));
            // Dos ediciones simultáneas de fichas distintas compiten por el mismo DNI libre.
            Cliente a = s.crearCliente("Concurrente A", "00999666");
            Cliente b = s.crearCliente("Concurrente B", "00999777");
            CountDownLatch inicio = new CountDownLatch(1);
            ExecutorService pool = Executors.newFixedThreadPool(2);
            try {
                Callable<Boolean> editarA = () -> { inicio.await(); try { new ClienteDAO(db).modificar(a, "Ganador A", "0000999888"); return true; } catch (IllegalArgumentException e) { return false; } };
                Callable<Boolean> editarB = () -> { inicio.await(); try { new ClienteDAO(db).modificar(b, "Ganador B", "0000999888"); return true; } catch (IllegalArgumentException e) { return false; } };
                Future<Boolean> fa = pool.submit(editarA), fb = pool.submit(editarB);
                inicio.countDown();
                boolean ganaA = fa.get(15, TimeUnit.SECONDS), ganaB = fb.get(15, TimeUnit.SECONDS);
                exigir(ganaA != ganaB);
                Cliente perdedor = new ClienteDAO(db).buscarPorId(ganaA ? b.getId() : a.getId()).orElseThrow();
                exigir(perdedor.getNombre().equals(ganaA ? b.getNombre() : a.getNombre()) && perdedor.getDni().equals(ganaA ? b.getDni() : a.getDni()));
                exigir(new ClienteDAO(db).listar().stream().filter(x -> x.getDni().equals("0000999888")).count() == 1);
            } finally { pool.shutdownNow(); pool.awaitTermination(15, TimeUnit.SECONDS); }
            System.out.println("OK: " + verificaciones + " verificaciones de edición de clientes");
        } finally { Files.deleteIfExists(db); Files.delete(dir); }
    }
}
