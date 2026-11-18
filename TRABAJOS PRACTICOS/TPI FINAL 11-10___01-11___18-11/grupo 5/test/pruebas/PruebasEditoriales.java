package pruebas;

import modelo.*;
import persistencia.LibreriaDAO;
import servicio.LibreriaServicio;
import java.nio.file.*;
import java.math.BigDecimal;
import java.util.List;

public final class PruebasEditoriales {
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
    private static Libro libro(LibreriaServicio servicio, String isbn) {
        return servicio.libros().stream().filter(l -> l.getIsbn().equals(isbn)).findFirst().orElseThrow();
    }
    private static void asignar(LibreriaServicio servicio, Libro libro, Editorial editorial) {
        servicio.guardarLibro(libro, libro.getIsbn(), libro.getTitulo(), List.of("Autor prueba editorial"),
                editorial.getNombre(), libro.getCategoria().getNombre(), libro.getPrecioVenta(), libro.getStock(), libro.isActivo());
    }
    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("tpo-editoriales-");
        Path archivo = directorio.resolve("prueba.db");
        try {
            LibreriaServicio servicio = new LibreriaServicio(archivo);
            Editorial editorial = servicio.guardarEditorial(null, " Sello ABM prueba inicial ");
            long id = editorial.getId();
            exigir(servicio.editoriales().stream().anyMatch(e -> e.getId() == id));
            rechaza(() -> servicio.guardarEditorial(null, " "));
            rechaza(() -> servicio.guardarEditorial(null, "Sello ABM prueba inicial"));
            for (String isbn : List.of("ED-PRUEBA-1", "ED-PRUEBA-2")) {
                servicio.guardarLibro(null, isbn, isbn, List.of("Autor prueba editorial"),
                        editorial.getNombre(), "Categoría prueba editorial", BigDecimal.TEN, 3, true);
            }
            Libro primero = libro(servicio, "ED-PRUEBA-1");
            Libro segundo = libro(servicio, "ED-PRUEBA-2");
            exigir(primero.getEditorial().getId() == id && segundo.getEditorial().getId() == id);
            servicio.guardarEditorial(editorial, "Sello ABM prueba corregido");
            exigir(editorial.getId() == id);
            for (Libro l : List.of(primero, segundo)) {
                exigir(l.getEditorial().getId() == id && l.getEditorial().getNombre().equals(editorial.getNombre()));
            }
            LibreriaServicio reabierto = new LibreriaServicio(archivo);
            for (String isbn : List.of("ED-PRUEBA-1", "ED-PRUEBA-2")) {
                Libro l = libro(reabierto, isbn);
                exigir(l.getEditorial().getId() == id && l.getEditorial().getNombre().equals(editorial.getNombre()));
            }
            rechaza(() -> servicio.eliminarEditorial(editorial));
            exigir(libro(new LibreriaServicio(archivo), "ED-PRUEBA-1").getEditorial().getId() == id);
            Editorial otra = servicio.guardarEditorial(null, "Sello ABM prueba destino");
            rechaza(() -> servicio.guardarEditorial(editorial, otra.getNombre()));
            asignar(servicio, primero, otra);
            servicio.cambiarActividad(segundo, false);
            try { new LibreriaDAO(archivo).eliminarEditorial(editorial); throw new AssertionError("Baja con referencia inactiva"); }
            catch (IllegalStateException esperado) { exigir(esperado.getMessage().contains("Reasignalos")); }
            exigir(libro(new LibreriaServicio(archivo), "ED-PRUEBA-2").getEditorial().getId() == id);
            asignar(servicio, segundo, otra);
            servicio.validarBajaEditorial(editorial);
            // Cancelar/cerrar en el controlador no llama eliminar: validar no escribe.
            exigir(servicio.editoriales().stream().anyMatch(e -> e.getId() == id));
            servicio.eliminarEditorial(editorial);
            reabierto = new LibreriaServicio(archivo);
            exigir(reabierto.editoriales().stream().noneMatch(e -> e.getId() == id));
            exigir(libro(reabierto, "ED-PRUEBA-1").getEditorial().getId().equals(otra.getId()));
            exigir(libro(reabierto, "ED-PRUEBA-2").getEditorial().getId().equals(otra.getId()));
            // Otra sesión asigna un libro después de validar/abrir confirmación.
            Editorial tardia = servicio.guardarEditorial(null, "Sello ABM referencia tardía");
            servicio.validarBajaEditorial(tardia);
            LibreriaServicio otraSesion = new LibreriaServicio(archivo);
            otraSesion.guardarLibro(null, "ED-TARDIA", "Tardío", List.of("Autor prueba editorial"),
                    tardia.getNombre(), "Categoría prueba editorial", BigDecimal.ONE, 0, false);
            rechaza(() -> servicio.eliminarEditorial(tardia));
            exigir(new LibreriaServicio(archivo).editoriales().stream().anyMatch(e -> e.getId().equals(tardia.getId())));
            System.out.println("OK: " + verificaciones + " verificaciones de editoriales");
        } finally {
            Files.deleteIfExists(archivo);
            Files.delete(directorio);
        }
    }
}
