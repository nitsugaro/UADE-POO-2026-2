package modelo;

import excepciones.DevolucionNoPermitidaException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/** Registro inmutable, creado exclusivamente por Venta.completarDevolucion. */
public final class Devolucion {
    private final Long id;
    private final Venta ventaOriginal;
    private final LocalDateTime fecha;
    private final Empleado empleadoResponsable;
    private final String motivo;
    private final BigDecimal importeReintegrado;
    private final MedioPago medioReintegro;

    Devolucion(Long id, Venta ventaOriginal, LocalDateTime fecha, Empleado responsable, String motivo) {
        this.id = Validaciones.id(id);
        this.ventaOriginal = Objects.requireNonNull(ventaOriginal, "Venta obligatoria");
        this.fecha = Objects.requireNonNull(fecha, "Fecha obligatoria");
        if (responsable == null || !responsable.isActivo()) {
            throw new DevolucionNoPermitidaException("Se requiere un empleado responsable activo");
        }
        if (ventaOriginal.getEstado() != EstadoVenta.CONFIRMADA || ventaOriginal.getPago() == null) {
            throw new DevolucionNoPermitidaException("La venta debe estar confirmada y pagada");
        }
        this.empleadoResponsable = responsable;
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
