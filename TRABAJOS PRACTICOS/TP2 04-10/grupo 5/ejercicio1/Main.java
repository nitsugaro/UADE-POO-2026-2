package tp2.ejercicio1;

public class Main {

    public static void main(String[] args) {
        SistemaCompras sistema = new SistemaCompras();
        Proveedor proveedor1 = new Proveedor(
                "30111222",
                "Proveedor SA"
        );
        sistema.altaProveedor(proveedor1);
        Producto producto1 = new Producto(
                101,
                "Teclado",
                25000
        );
        Producto producto2 = new Producto(
                102,
                "Mouse",
                15000
        );
        sistema.altaProducto(producto1);
        sistema.altaProducto(producto2);
        sistema.generarOrden(
                1,
                "02/10/2026",
                "30111222",
                101,
                3
        );
        sistema.generarOrden(
                2,
                "02/10/2026",
                "30111222",
                102,
                2
        );
        sistema.consultarOrdenesProveedor("30111222");
    }
}