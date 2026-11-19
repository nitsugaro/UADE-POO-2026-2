package pruebas;

import modelo.Cliente;
import persistencia.*;
import servicio.LibreriaServicio;
import java.nio.file.*;
import java.util.concurrent.*;

public final class PruebasClientesAlta {
    private static int verificaciones;
    private static void exigir(boolean b) { if (!b) throw new AssertionError("Control " + verificaciones); verificaciones++; }
    private static void rechaza(Runnable r, String mensaje) {
        try { r.run(); } catch (IllegalArgumentException | IllegalStateException e) { exigir(e.getMessage().contains(mensaje)); return; }
        throw new AssertionError("Se esperaba rechazo");
    }
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("tpo-altas-clientes-");
        Path db = dir.resolve("prueba.db");
        try {
            LibreriaServicio s = new LibreriaServicio(db);
            Cliente c = s.crearCliente("Alta prueba", "00088771");
            exigir(c.isActivo() && s.socios().stream().noneMatch(x -> x.getId().equals(c.getId())));
            exigir(new ClienteDAO(db).buscarPorId(c.getId()).orElseThrow().getDni().equals("00088771"));
            var borrador = s.nuevaVenta(s.empleadoPredeterminado());
            borrador.cambiarCliente(c);
            rechaza(() -> s.crearCliente("No crear", c.getDni()), "Buscá");
            exigir(borrador.getCliente() == c && borrador.getId() == null && c.isActivo());
            s.cambiarActividadCliente(c, false);
            rechaza(() -> s.crearCliente("No crear", c.getDni()), "Reactiv");
            rechaza(() -> s.asociar("", c.getDni()), "Reactiv");
            exigir(!c.isActivo() && s.socios().stream().noneMatch(x -> x.getId().equals(c.getId())));
            s.cambiarActividadCliente(c, true);
            exigir(s.asociar("", c.getDni()).getId().equals(c.getId()));
            rechaza(() -> s.asociar("", c.getDni()), "ya está registrado como socio");
            Cliente nuevoSocio = s.asociar("Socio nuevo", "00088772");
            exigir(s.socios().stream().anyMatch(x -> x.getId().equals(nuevoSocio.getId())));
            rechaza(() -> s.asociar("", "00088773"), "Nombre");
            exigir(new ClienteDAO(db).listar().stream().noneMatch(x -> x.getDni().equals("00088773")));
            // Datos heredados, sin pasar por la nueva regla de altas.
            try (var con = ConexionSQLite.abrir(db); var st = con.prepareStatement("INSERT INTO clientes(nombre,dni) VALUES ('Histórico',?)")) {
                st.setString(1, c.getDni()); st.executeUpdate();
            }
            int sociosAntes = s.socios().size();
            rechaza(() -> s.asociar("", c.getDni()), "varias fichas");
            exigir(s.socios().size() == sociosAntes && new ClienteDAO(db).listar().stream().filter(x -> x.getDni().equals(c.getDni())).count() == 2);
            ExecutorService pool = Executors.newFixedThreadPool(2);
            CountDownLatch inicio = new CountDownLatch(1);
            try {
                Callable<Boolean> alta = () -> { inicio.await(); try { new ClienteDAO(db).guardar(new Cliente(null, "Concurrente", "00088774")); return true; } catch (IllegalArgumentException e) { return false; } };
                Future<Boolean> a = pool.submit(alta), b = pool.submit(alta);
                inicio.countDown();
                exigir(a.get(15, TimeUnit.SECONDS) != b.get(15, TimeUnit.SECONDS));
                exigir(new ClienteDAO(db).listar().stream().filter(x -> x.getDni().equals("00088774")).count() == 1);
            } finally { pool.shutdownNow(); pool.awaitTermination(15, TimeUnit.SECONDS); }
            LibreriaServicio r = new LibreriaServicio(db);
            exigir(r.socios().stream().anyMatch(x -> x.getId().equals(c.getId())) && r.socios().stream().anyMatch(x -> x.getId().equals(nuevoSocio.getId())));
            System.out.println("OK: " + verificaciones + " verificaciones de alta de clientes");
        } finally { Files.deleteIfExists(db); Files.delete(dir); }
    }
}
