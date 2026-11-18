package pruebas;

import modelo.*;
import persistencia.LibreriaDAO;
import servicio.LibreriaServicio;
import java.nio.file.*;
import java.math.BigDecimal;
import java.util.List;

public final class PruebasCategorias {
    private static int verificaciones;
    private static void exigir(boolean condicion) {
        if (!condicion) throw new AssertionError("Verificación " + (verificaciones + 1));
        verificaciones++;
    }
    private static void rechaza(Runnable accion) {
        try { accion.run(); }
        catch (IllegalArgumentException | IllegalStateException esperado) { verificaciones++; return; }
        throw new AssertionError("Se esperaba rechazo");
    }
    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("tpo-categorias-");
        Path archivo = directorio.resolve("prueba.db");
        try {
            LibreriaServicio servicio = new LibreriaServicio(archivo);
            // La precarga ya incluye Novela. Liberar el nombre únicamente en esta base temporal.
            for (Categoria ejemplo : servicio.categorias()) {
                if (ejemplo.getNombre().equals("Novela")) {
                    servicio.guardarCategoria(ejemplo, "Novela de precarga (prueba)");
                }
            }
            Categoria categoria = servicio.guardarCategoria(null, " Novlea ");
            long id = categoria.getId();
            exigir(servicio.categorias().stream().anyMatch(c -> c.getId() == id));
            rechaza(() -> servicio.guardarCategoria(null, "Novlea"));
            rechaza(() -> servicio.guardarCategoria(null, "  "));
            servicio.guardarLibro(null, "CAT-PRUEBA", "Libro categorías", List.of("Autor"),
                    "Editorial", "Novlea", BigDecimal.TEN, 3, true);
            Libro libro = servicio.libros().stream().filter(l -> l.getIsbn().equals("CAT-PRUEBA")).findFirst().orElseThrow();
            exigir(libro.getCategoria().getId() == id);
            servicio.guardarCategoria(categoria, "Novela");
            exigir(categoria.getId() == id && libro.getCategoria().getNombre().equals("Novela"));
            exigir(new LibreriaServicio(archivo).libros().stream().anyMatch(l -> l.getIsbn().equals("CAT-PRUEBA")
                    && l.getCategoria().getId() == id && l.getCategoria().getNombre().equals("Novela")));
            rechaza(() -> servicio.eliminarCategoria(categoria));
            servicio.cambiarActividad(libro, false);
            // Verifica también el límite DAO, sin pasar por validación de Swing/servicio.
            try { new LibreriaDAO(archivo).eliminarCategoria(categoria); throw new AssertionError("Baja de usada"); }
            catch (IllegalStateException esperado) { exigir(esperado.getMessage().contains("Reasignalos")); }
            exigir(new LibreriaServicio(archivo).libros().stream().anyMatch(l -> l.getIsbn().equals("CAT-PRUEBA")
                    && !l.isActivo() && l.getCategoria().getId() == id));
            Categoria otra = servicio.guardarCategoria(null, "Otra categoría prueba");
            rechaza(() -> servicio.guardarCategoria(categoria, otra.getNombre()));
            servicio.guardarLibro(libro, libro.getIsbn(), libro.getTitulo(), List.of("Autor"), "Editorial",
                    otra.getNombre(), BigDecimal.TEN, 3, false);
            servicio.validarBajaCategoria(categoria);
            // Cancelar en UI no llama eliminarCategoria: esta consulta prueba que validar no escribe.
            exigir(servicio.categorias().stream().anyMatch(c -> c.getId() == id));
            servicio.eliminarCategoria(categoria);
            LibreriaServicio reabierto = new LibreriaServicio(archivo);
            exigir(reabierto.categorias().stream().noneMatch(c -> c.getId() == id));
            exigir(reabierto.libros().stream().anyMatch(l -> l.getIsbn().equals("CAT-PRUEBA")
                    && l.getCategoria().getId().equals(otra.getId())));
            // Una referencia incorporada después de la prevalidación también impide el DELETE.
            Categoria tardia = servicio.guardarCategoria(null, "Referencia tardía");
            servicio.validarBajaCategoria(tardia);
            servicio.guardarLibro(null, "CAT-TARDIA", "Tardío", List.of("Autor"), "Editorial",
                    tardia.getNombre(), BigDecimal.ONE, 0, false);
            rechaza(() -> servicio.eliminarCategoria(tardia));
            System.out.println("OK: " + verificaciones + " verificaciones de categorías");
        } finally {
            Files.deleteIfExists(archivo);
            Files.delete(directorio);
        }
    }
}
