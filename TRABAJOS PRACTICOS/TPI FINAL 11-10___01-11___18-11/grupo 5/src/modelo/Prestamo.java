package modelo;

import java.time.LocalDateTime;

public record Prestamo(long id, long libroId, String titulo, String socio, String dni,
                       LocalDateTime fecha, LocalDateTime devueltoEn) {
}
