package modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Pago aprobado e inmutable; no procesa tarjetas ni almacena datos bancarios. */
public final class Pago {
    private final Long id;
    private final LocalDateTime fecha;
    private final MedioPago medioPago;
    private final BigDecimal importePagado;
    private final BigDecimal montoRecibido;
    private final String referenciaAutorizacion;

    private Pago(Long id, LocalDateTime fecha, MedioPago medio, BigDecimal importe, BigDecimal recibido,
            String referencia) {
        this.id = Validaciones.id(id);
        this.fecha = Objects.requireNonNull(fecha, "Fecha obligatoria");
        this.medioPago = Objects.requireNonNull(medio, "Medio obligatorio");
        this.importePagado = Validaciones.precio(importe);
        if (medio == MedioPago.EFECTIVO) {
            this.montoRecibido = Validaciones.dinero(recibido);
            if (montoRecibido.compareTo(importePagado) < 0) {
                throw new IllegalArgumentException("Monto recibido insuficiente");
            }
            this.referenciaAutorizacion = null;
        } else {
            this.montoRecibido = null;
            this.referenciaAutorizacion = Validaciones.texto(referencia, "Referencia ficticia");
        }
    }

    public static Pago enEfectivo(Long id, LocalDateTime fecha, BigDecimal importe, BigDecimal recibido) {
        return new Pago(id, fecha, MedioPago.EFECTIVO, importe, recibido, null);
    }

    public static Pago conTarjeta(Long id, LocalDateTime fecha, BigDecimal importe, String referencia) {
        return new Pago(id, fecha, MedioPago.TARJETA, importe, null, referencia);
    }

    public static BigDecimal calcularVuelto(BigDecimal total, BigDecimal recibido) {
        BigDecimal importe = Validaciones.precio(total);
        BigDecimal monto = Validaciones.dinero(recibido);
        if (monto.compareTo(importe) < 0) {
            throw new IllegalArgumentException("Monto recibido insuficiente");
        }
        return monto.subtract(importe);
    }

    public BigDecimal calcularVuelto() {
        if (medioPago == MedioPago.EFECTIVO) {
            return montoRecibido.subtract(importePagado);
        }
        return new BigDecimal("0.00");
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public BigDecimal getImportePagado() {
        return importePagado;
    }

    public BigDecimal getMontoRecibido() {
        return montoRecibido;
    }

    public String getReferenciaAutorizacion() {
        return referenciaAutorizacion;
    }
}
