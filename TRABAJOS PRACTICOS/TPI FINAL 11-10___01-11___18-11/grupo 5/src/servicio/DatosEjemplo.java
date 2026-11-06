package servicio;

import modelo.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

final class DatosEjemplo {
    private DatosEjemplo() {
    }

    static DatosSesion crear() {
        DatosSesion datos = new DatosSesion();
        datos.agregarEmpleado(new Empleado(1L, "E001", "Empleado de ejemplo", true));
        Autor autor = new Autor(1L, "Autor de ejemplo");
        Editorial editorial = new Editorial(1L, "Editorial de ejemplo");
        Categoria categoria = new Categoria(1L, "Programación");
        datos.agregarLibro(new Libro(1L, "DEMO-001", "Introducción a Java", new ArrayList<>(List.of(autor)), editorial,
                categoria, new BigDecimal("25000"), 10, true));
        datos.agregarLibro(new Libro(2L, "DEMO-002", "Programación orientada a objetos", new ArrayList<>(List.of(autor)),
                editorial, categoria, new BigDecimal("32000"), 5, true));
        return datos;
    }
}
