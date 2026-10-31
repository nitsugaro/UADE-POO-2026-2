package modelo;

import java.math.BigDecimal;
import java.util.Objects;

/** Inmutable: solo Venta crea detalles y reemplaza cantidades. */
public final class DetalleVenta {
    private final Libro libro;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private final String titulo;
    private final String isbn;

    DetalleVenta(Libro libro, int cantidad) {
        this(Objects.requireNonNull(libro, "Libro obligatorio"), cantidad, libro.getPrecioVenta(), libro.getTitulo(), libro.getIsbn());
    }

    private DetalleVenta(Libro libro, int cantidad, BigDecimal precio, String titulo, String isbn) {
        this.libro = libro;
        this.cantidad = Validaciones.cantidad(cantidad);
        this.precioUnitario = Validaciones.precio(precio);
        this.titulo = titulo;
        this.isbn = isbn;
    }

    DetalleVenta conCantidad(int cantidad) {
        return new DetalleVenta(libro, cantidad, precioUnitario, titulo, isbn);
    }

    public BigDecimal calcularSubtotal() { return precioUnitario.multiply(BigDecimal.valueOf(cantidad)); }
    public Libro getLibro() { return libro; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public String getTitulo() { return titulo; }
    public String getIsbn() { return isbn; }
}
