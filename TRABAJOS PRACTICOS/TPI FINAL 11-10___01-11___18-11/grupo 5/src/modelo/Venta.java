package modelo;

import excepciones.StockInsuficienteException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Venta {
    private final Long id;
    private final LocalDateTime fecha;
    private final Empleado empleado;
    private Cliente cliente;
    private final List<DetalleVenta> detalles = new ArrayList<>();
    private Descuento descuento;
    private Pago pago;
    private EstadoVenta estado = EstadoVenta.EN_PREPARACION;

    /** fecha es la fecha de compra utilizada por la politica de devolucion. */
    public Venta(Long id, LocalDateTime fecha, Empleado empleado, Cliente cliente) {
        this.id = Validaciones.id(id);
        this.fecha = Objects.requireNonNull(fecha, "Fecha obligatoria");
        this.empleado = Objects.requireNonNull(empleado, "Empleado obligatorio");
        this.cliente = cliente;
    }

    private void exigirPreparacion() {
        if (estado != EstadoVenta.EN_PREPARACION)
            throw new IllegalStateException("La venta no esta en preparacion");
    }

    private int buscarLibro(Libro libro) {
        Objects.requireNonNull(libro, "Libro obligatorio");
        for (int i = 0; i < detalles.size(); i++) {
            Libro existente = detalles.get(i).getLibro();
            if (existente == libro || (existente.getId() != null && existente.getId().equals(libro.getId())))
                return i;
        }
        return -1;
    }

    public void agregarLibro(Libro libro, int cantidad) {
        exigirPreparacion();
        Validaciones.cantidad(cantidad);
        int indice = buscarLibro(libro);
        if (indice < 0) detalles.add(new DetalleVenta(libro, cantidad));
        else {
            DetalleVenta detalle = detalles.get(indice);
            detalles.set(indice, detalle.conCantidad(Math.addExact(detalle.getCantidad(), cantidad)));
        }
    }

    public void modificarCantidad(Libro libro, int cantidad) {
        exigirPreparacion();
        Validaciones.cantidad(cantidad);
        int indice = buscarLibro(libro);
        if (indice < 0) throw new IllegalArgumentException("El libro no esta en la venta");
        detalles.set(indice, detalles.get(indice).conCantidad(cantidad));
    }

    public void quitarLibro(Libro libro) {
        exigirPreparacion();
        int indice = buscarLibro(libro);
        if (indice < 0) throw new IllegalArgumentException("El libro no esta en la venta");
        detalles.remove(indice);
    }

    public void cambiarCliente(Cliente cliente) {
        exigirPreparacion();
        this.cliente = cliente;
    }

    public void aplicarDescuento(String codigo) {
        exigirPreparacion();
        if (descuento != null) throw new IllegalStateException("La venta ya tiene descuento");
        descuento = Descuento.desdeCodigo(codigo);
    }

    public void quitarDescuento() {
        exigirPreparacion();
        descuento = null;
    }

    public void registrarPago(Pago pago) {
        exigirPreparacion();
        this.pago = Objects.requireNonNull(pago, "Pago obligatorio");
    }

    public void quitarPago() {
        exigirPreparacion();
        pago = null;
    }

    public BigDecimal calcularSubtotal() {
        BigDecimal subtotal = new BigDecimal("0.00");
        for (DetalleVenta detalle : detalles) subtotal = subtotal.add(detalle.calcularSubtotal());
        return subtotal;
    }

    public BigDecimal calcularDescuento() {
        return descuento == null ? new BigDecimal("0.00") : descuento.calcularImporte(calcularSubtotal());
    }

    public BigDecimal calcularTotal() { return calcularSubtotal().subtract(calcularDescuento()); }

    public void cancelar() {
        exigirPreparacion();
        estado = EstadoVenta.CANCELADA;
    }

    /**
     * Valida y cambia el estado; NO descuenta stock.
     * El futuro controlador/persistencia debera coordinar estado y stock en una transaccion.
     */
    public void confirmar() {
        exigirPreparacion();
        if (detalles.isEmpty()) throw new IllegalStateException("La venta no tiene detalles");
        if (!empleado.isActivo()) throw new IllegalStateException("Empleado inactivo");
        for (DetalleVenta detalle : detalles) {
            Libro libro = detalle.getLibro();
            if (!libro.isActivo()) throw new IllegalStateException("Libro inactivo");
            if (!libro.hayDisponibilidad(detalle.getCantidad()))
                throw new StockInsuficienteException("Stock insuficiente para " + detalle.getTitulo());
        }
        if (pago == null) throw new IllegalStateException("Falta el pago");
        if (pago.getMedioPago() == MedioPago.TARJETA && cliente == null)
            throw new IllegalStateException("El pago con tarjeta requiere cliente");
        if (pago.getImportePagado().compareTo(calcularTotal()) != 0)
            throw new IllegalStateException("El pago no coincide con el total");
        estado = EstadoVenta.CONFIRMADA;
    }

    /**
     * Completa una devolucion total. NO repone stock.
     * La consulta de PoliticaDevolucion nunca modifica esta venta.
     */
    public Devolucion completarDevolucion(Long idDevolucion, LocalDateTime fechaDevolucion, Empleado responsable, String motivo, boolean aptosParaReventa) {
        new PoliticaDevolucion().validar(this, fechaDevolucion, motivo, aptosParaReventa);
        Devolucion devolucion = new Devolucion(idDevolucion, this, fechaDevolucion, responsable, motivo);
        estado = EstadoVenta.DEVUELTA;
        return devolucion;
    }

    public Long getId() { return id; }
    public LocalDateTime getFecha() { return fecha; }
    public Empleado getEmpleado() { return empleado; }
    public Cliente getCliente() { return cliente; }
    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(new ArrayList<>(detalles));
    }
    public Descuento getDescuento() { return descuento; }
    public Pago getPago() { return pago; }
    public EstadoVenta getEstado() { return estado; }
}
