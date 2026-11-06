package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Reglas comunes del modelo; todos los importes se normalizan con HALF_UP. */
final class Validaciones {
    private Validaciones() {
    }

    static Long id(Long id) {
        if (id != null && id <= 0) {
            throw new IllegalArgumentException("El id debe ser positivo o null");
        }
        return id;
    }

    static String texto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        return valor.trim();
    }

    static int cantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        return cantidad;
    }

    static BigDecimal dinero(BigDecimal valor) {
        Objects.requireNonNull(valor, "El importe es obligatorio");
        if (valor.signum() < 0) {
            throw new IllegalArgumentException("El importe no puede ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    static BigDecimal precio(BigDecimal valor) {
        BigDecimal precio = dinero(valor);
        if (precio.signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero");
        }
        return precio;
    }
}
