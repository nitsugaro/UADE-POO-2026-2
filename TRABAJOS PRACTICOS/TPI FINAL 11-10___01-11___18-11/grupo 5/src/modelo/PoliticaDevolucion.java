package modelo;

import excepciones.DevolucionNoPermitidaException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class PoliticaDevolucion {
    /** Solo consulta: no cambia estados ni stock. Compara dias de calendario, no horas. */
    public void validar(Venta venta, LocalDateTime fecha, String motivo, boolean aptosParaReventa) {
        if (venta == null || fecha == null)
            throw new DevolucionNoPermitidaException("Venta y fecha obligatorias");
        if (venta.getEstado() != EstadoVenta.CONFIRMADA)
            throw new DevolucionNoPermitidaException("Solo se devuelve una venta confirmada no devuelta");
        LocalDate compra = venta.getFecha().toLocalDate();
        LocalDate devolucion = fecha.toLocalDate();
        if (devolucion.isBefore(compra) || devolucion.toEpochDay() - compra.toEpochDay() > 7)
            throw new DevolucionNoPermitidaException("Fecha fuera del plazo de devolucion");
        if (!aptosParaReventa)
            throw new DevolucionNoPermitidaException("Todos los libros deben estar sin uso y aptos para reventa");
        if (motivo == null || motivo.trim().isEmpty())
            throw new DevolucionNoPermitidaException("El motivo es obligatorio");
    }
}
