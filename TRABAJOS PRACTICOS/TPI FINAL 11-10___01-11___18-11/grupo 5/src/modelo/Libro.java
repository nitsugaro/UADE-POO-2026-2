package modelo;

import excepciones.StockInsuficienteException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class Libro {
    private final Long id;
    private final String isbn;
    private final String titulo;
    private final List<Autor> autores;
    private final Editorial editorial;
    private final Categoria categoria;
    private BigDecimal precioVenta;
    private int stock;
    private boolean activo;

    public Libro(Long id, String isbn, String titulo, List<Autor> autores, Editorial editorial, Categoria categoria, BigDecimal precioVenta, int stock, boolean activo) {
        this.id = Validaciones.id(id);
        this.isbn = Validaciones.texto(isbn, "ISBN");
        this.titulo = Validaciones.texto(titulo, "Titulo");
        Objects.requireNonNull(autores, "Autores obligatorios");
        if (autores.isEmpty() || autores.contains(null))
            throw new IllegalArgumentException("Se requiere al menos un autor y ninguno nulo");
        this.autores = new ArrayList<>(autores);
        this.editorial = Objects.requireNonNull(editorial, "Editorial obligatoria");
        this.categoria = Objects.requireNonNull(categoria, "Categoria obligatoria");
        this.precioVenta = Validaciones.precio(precioVenta);
        if (stock < 0) throw new IllegalArgumentException("Stock negativo");
        this.stock = stock;
        this.activo = activo;
    }

    public boolean hayDisponibilidad(int cantidad) {
        Validaciones.cantidad(cantidad);
        return activo && stock >= cantidad;
    }

    public void aumentarStock(int cantidad) {
        stock = Math.addExact(stock, Validaciones.cantidad(cantidad));
    }

    public void disminuirStock(int cantidad) {
        Validaciones.cantidad(cantidad);
        if (stock < cantidad) throw new StockInsuficienteException("Stock insuficiente para " + titulo);
        stock -= cantidad;
    }

    public void cambiarPrecio(BigDecimal precio) { precioVenta = Validaciones.precio(precio); }
    public void activar() { activo = true; }
    public void desactivar() { activo = false; }
    public Long getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public List<Autor> getAutores() { return Collections.unmodifiableList(new ArrayList<>(autores)); }
    public Editorial getEditorial() { return editorial; }
    public Categoria getCategoria() { return categoria; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public int getStock() { return stock; }
    public boolean isActivo() { return activo; }
}
