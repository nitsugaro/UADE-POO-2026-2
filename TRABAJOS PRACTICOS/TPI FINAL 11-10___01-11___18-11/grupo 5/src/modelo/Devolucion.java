package modelo;

import excepciones.DevolucionNoPermitidaException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Registro inmutable de una devolución total. */
public final class Devolucion {
    private final Long id;
    private final Venta ventaOriginal;
    private final LocalDateTime fecha;
    private final Empleado empleadoResponsable;
    private final String motivo;
    private final BigDecimal importeReintegrado;
    private final MedioPago medioReintegro;

    Devolucion(Long id, Venta ventaOriginal, LocalDateTime fecha, Empleado responsable, String motivo) {
        this(id, ventaOriginal, fecha, responsable, motivo, true);
    }

    public static Devolucion rehidratar(long id, Venta ventaOriginal, LocalDateTime fecha,
            Empleado responsable, String motivo) {
        return new Devolucion(id, ventaOriginal, fecha, responsable, motivo, false);
    }

    private Devolucion(Long id, Venta ventaOriginal, LocalDateTime fecha, Empleado responsable,
            String motivo, boolean nueva) {
        this.id = Validaciones.id(id);
        this.ventaOriginal = Objects.requireNonNull(ventaOriginal, "Venta obligatoria");
        this.fecha = Objects.requireNonNull(fecha, "Fecha obligatoria");
        this.empleadoResponsable = Objects.requireNonNull(responsable, "Responsable obligatorio");
        EstadoVenta estadoEsperado = nueva ? EstadoVenta.CONFIRMADA : EstadoVenta.DEVUELTA;
        if (ventaOriginal.getEstado() != estadoEsperado || ventaOriginal.getPago() == null) {
            throw new DevolucionNoPermitidaException("Estado o pago inválido para la devolución");
        }
        this.motivo = Validaciones.texto(motivo, "Motivo");
        this.importeReintegrado = ventaOriginal.getPago().getImportePagado();
        this.medioReintegro = ventaOriginal.getPago().getMedioPago();
    }

    public Long getId() {
        return id;
    }

    public Venta getVentaOriginal() {
        return ventaOriginal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public Empleado getEmpleadoResponsable() {
        return empleadoResponsable;
    }

    public String getMotivo() {
        return motivo;
    }

    public BigDecimal getImporteReintegrado() {
        return importeReintegrado;
    }

    public MedioPago getMedioReintegro() {
        return medioReintegro;
    }
}
