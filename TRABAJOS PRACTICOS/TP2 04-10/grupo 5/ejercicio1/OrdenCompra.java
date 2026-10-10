package tp2.ejercicio1;

public class OrdenCompra {

    private int numero;
    private String fecha;
    private int cantidad;

    private Proveedor proveedor;
    private Producto producto;

    public OrdenCompra(int numero, String fecha, int cantidad, Proveedor proveedor, Producto producto) {
        this.numero = numero;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.proveedor = proveedor;
        this.producto = producto;
    }

    public double calcularTotal() {
        return cantidad * producto.getPrecioUnitario();
    }

    public int getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public Producto getProducto() {
        return producto;
    }
}