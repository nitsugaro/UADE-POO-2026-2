package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Descuento {
    private final String codigo;
    private final BigDecimal porcentaje;

    private Descuento(String codigo, BigDecimal porcentaje) {
        this.codigo = codigo;
        this.porcentaje = porcentaje;
    }

    public static Descuento desdeCodigo(String codigo) {
        if (!"HOTSALE".equals(Validaciones.texto(codigo, "Codigo")))
            throw new IllegalArgumentException("Codigo de descuento invalido");
        return new Descuento("HOTSALE", new BigDecimal("10.00"));
    }

    public BigDecimal calcularImporte(BigDecimal subtotal) {
        return Validaciones.dinero(subtotal).multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    public String getCodigo() { return codigo; }
    public BigDecimal getPorcentaje() { return porcentaje; }
}
