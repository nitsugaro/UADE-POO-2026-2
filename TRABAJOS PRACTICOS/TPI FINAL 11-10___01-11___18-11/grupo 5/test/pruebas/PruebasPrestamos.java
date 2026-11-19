package pruebas;

import excepciones.LimitePrestamosException;
import modelo.Libro;
import modelo.Prestamo;
import servicio.LibreriaServicio;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Regresión del límite, devoluciones y altas desde los combos, con bases temporales. */
public final class PruebasPrestamos {
    private static int verificaciones;

    private static void verificar(boolean condicion) {
        if (!condicion) throw new AssertionError("Verificación " + (verificaciones + 1));
        verificaciones++;
    }

    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("tpo-prestamos-");
        Path archivo = directorio.resolve("prueba.db");
        try {
            LibreriaServicio servicio = new LibreriaServicio(archivo);
            String dni = servicio.asociar("Socio de prueba", "99887766").getDni();
            Libro libro = servicio.libros().get(0);
            int stock = libro.getStock();
            servicio.prestar(dni, libro);
            servicio.prestar(dni, libro);
            verificar(libro.getStock() == stock - 2);
            // Otra sesión debe respetar los préstamos persistidos.
            LibreriaServicio otra = new LibreriaServicio(archivo);
            try {
                otra.prestar(dni, otra.libros().get(1));
                throw new AssertionError("Se permitió el tercer préstamo");
            } catch (LimitePrestamosException esperado) {
                verificar(esperado.getMessage().contains("dos préstamos activos"));
            }
            verificar(otra.prestamos().size() == 2 && libro.getStock() == stock - 2);
            Prestamo pendiente = servicio.prestamos().get(0);
            servicio.devolverPrestamo(pendiente);
            verificar(libro.getStock() == stock - 1);
            try {
                servicio.devolverPrestamo(pendiente);
                throw new AssertionError("Se permitió devolver dos veces");
            } catch (IllegalArgumentException esperado) {
                verificar(libro.getStock() == stock - 1);
            }
            servicio.prestar(dni, libro);
            verificar(servicio.prestamos().stream().filter(p -> p.devueltoEn() == null).count() == 2);
            LibreriaServicio reabierto = new LibreriaServicio(archivo);
            verificar(reabierto.libros().get(0).getStock() == stock - 2);
            verificar(reabierto.prestamos().stream().anyMatch(p -> p.id() == pendiente.id() && p.devueltoEn() != null));

            // Dos solicitudes simultáneas cuando queda un único cupo.
            servicio.devolverPrestamo(servicio.prestamos().stream().filter(p -> p.devueltoEn() == null).findFirst().orElseThrow());
            LibreriaServicio sesionA = new LibreriaServicio(archivo);
            LibreriaServicio sesionB = new LibreriaServicio(archivo);
            CountDownLatch inicio = new CountDownLatch(1);
            var ejecutor = Executors.newFixedThreadPool(2);
            try {
                var a = ejecutor.submit(() -> intentarPrestamo(sesionA, dni, 1, inicio));
                var b = ejecutor.submit(() -> intentarPrestamo(sesionB, dni, 2, inicio));
                inicio.countDown();
                verificar(a.get(10, TimeUnit.SECONDS) + b.get(10, TimeUnit.SECONDS) == 1);
            } finally {
                ejecutor.shutdownNow();
            }
            verificar(servicio.prestamos().stream().filter(p -> p.devueltoEn() == null).count() == 2);

            servicio.guardarLibro(null, "9780000099001", "Libro de prueba", List.of("Autor nuevo combo"),
                    "Editorial nueva combo", "Categoría nueva combo", BigDecimal.TEN, 3, true);
            servicio.guardarLibro(null, "9780000099002", "Otro libro", List.of("Autor nuevo combo", "Segundo autor combo"),
                    "Editorial nueva combo", "Categoría nueva combo", BigDecimal.TEN, 1, true);
            LibreriaServicio finalizado = new LibreriaServicio(archivo);
            verificar(finalizado.editoriales().stream().filter(e -> e.getNombre().equals("Editorial nueva combo")).count() == 1);
            verificar(finalizado.categorias().stream().filter(c -> c.getNombre().equals("Categoría nueva combo")).count() == 1);
            verificar(finalizado.autores().stream().filter(a -> a.getNombre().equals("Autor nuevo combo")).count() == 1);
            verificar(finalizado.libros().stream().filter(l -> l.getIsbn().equals("9780000099002"))
                    .findFirst().orElseThrow().getAutores().size() == 2);
            System.out.println("OK: " + verificaciones + " verificaciones de préstamos, devoluciones y opciones nuevas");
        } finally {
            Files.deleteIfExists(archivo);
            Files.deleteIfExists(directorio);
        }
    }

    private static int intentarPrestamo(LibreriaServicio servicio, String dni, int indice, CountDownLatch inicio)
            throws InterruptedException {
        inicio.await();
        try {
            servicio.prestar(dni, servicio.libros().get(indice));
            return 1;
        } catch (LimitePrestamosException esperado) {
            return 0;
        }
    }
}
