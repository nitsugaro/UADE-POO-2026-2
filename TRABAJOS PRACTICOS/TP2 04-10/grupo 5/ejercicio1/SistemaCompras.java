package tp2.ejercicio1;

import java.util.ArrayList;

public class SistemaCompras {
    private ArrayList<Producto> productos;
    private ArrayList<Proveedor> proveedores;
    private ArrayList<OrdenCompra> ordenes;

    public SistemaCompras() {
        productos = new ArrayList<>();
        proveedores = new ArrayList<>();
        ordenes = new ArrayList<>();
    }

    public void altaProducto(Producto producto) {
        if (buscarProducto(producto.getCodigo()) == null) {
            productos.add(producto);
        } else {
            System.out.println("El producto ya existe.");
        }
    }

    public void altaProveedor(Proveedor proveedor) {
        if (buscarProveedor(proveedor.getDni()) == null) {
            proveedores.add(proveedor);
        } else {
            System.out.println("El proveedor ya existe.");
        }
    }

    public Producto buscarProducto(int codigo) {
        for (Producto producto : productos) {
            if (producto.getCodigo() == codigo) {
                return producto;
            }
        }
        return null;
    }

    public Proveedor buscarProveedor(String dni) {
        for (Proveedor proveedor : proveedores) {
            if (proveedor.getDni().equals(dni)) {
                return proveedor;
            }
        }
        return null;
    }

    public void generarOrden(int numero, String fecha, String dniProveedor, int codigoProducto, int cantidad) {
        Proveedor proveedor = buscarProveedor(dniProveedor);
        Producto producto = buscarProducto(codigoProducto);
        if (proveedor == null) {
            System.out.println("Proveedor inexistente.");
            return;
        }
        if (producto == null) {
            System.out.println("Producto inexistente.");
            return;
        }
        OrdenCompra orden = new OrdenCompra(numero, fecha, cantidad, proveedor,  producto);

        ordenes.add(orden);

        System.out.println("Orden de compra generada correctamente.");
    }

    public double calcularTotalProveedor(String dni) {
        double total = 0;
        for (OrdenCompra orden : ordenes) {
            if (orden.getProveedor().getDni().equals(dni)) {
                total += orden.calcularTotal();
            }
        }
        return total;
    }

    public void consultarOrdenesProveedor(String dni) {
        Proveedor proveedor = buscarProveedor(dni);
        if (proveedor == null) {
            System.out.println("Proveedor inexistente.");
            return;
        }
        System.out.println("Proveedor: " + proveedor.getNombre());
        for (OrdenCompra orden : ordenes) {
            if (orden.getProveedor().getDni().equals(dni)) {
                System.out.println(
                        "Orden: " + orden.getNumero() + " | Producto: " + orden.getProducto().getDescripcion() + " | Cantidad: " + orden.getCantidad() + " | Total: $" + orden.calcularTotal());
            }
        }
        System.out.println("Total de las órdenes: $" + calcularTotalProveedor(dni)
        );
    }
}