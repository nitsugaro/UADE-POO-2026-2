package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class VentaVista extends PanelVista {
    public final JTextField buscar = new JTextField(22);
    public final DefaultTableModel catalogoModelo = tabla("ISBN", "Título", "Stock disponible", "Precio");
    public final JTable catalogo = new JTable(catalogoModelo);
    public final DefaultTableModel carritoModelo = tabla("Título", "Cantidad", "Precio unitario", "Subtotal");
    public final JTable carrito = new JTable(carritoModelo);
    public final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    public final JSpinner cantidadCarrito = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    public final JButton agregar = new JButton("Agregar a la venta");
    public final JButton modificar = new JButton("Cambiar cantidad");
    public final JButton quitar = new JButton("Quitar");
    public final JButton cliente = new JButton("Buscar cliente");
    public final JButton nuevoCliente = new JButton("Nuevo cliente");
    public final JButton sinCliente = new JButton("Quitar cliente");
    public final JTextField codigo = new JTextField(10);
    public final JButton aplicar = new JButton("Aplicar descuento");
    public final JButton quitarDescuento = new JButton("Quitar descuento");
    public final JButton pagar = new JButton("Ir al pago");
    public final JButton cancelar = new JButton("Cancelar venta");
    public final JLabel totales = new JLabel();
    public final JLabel clienteActual = new JLabel("Sin cliente");

    public VentaVista() {
        super("Nueva venta");
        configurar(catalogo);
        configurar(carrito);

        catalogo.getColumnModel().getColumn(1).setPreferredWidth(380);
        carrito.getColumnModel().getColumn(0).setPreferredWidth(400);

        JLabel etiquetaBuscar = new JLabel("Buscar por título o ISBN");
        etiquetaBuscar.setLabelFor(buscar);
        JLabel etiquetaCantidad = new JLabel("Cantidad a agregar");
        etiquetaCantidad.setLabelFor(cantidad);
        cantidad.setPreferredSize(new Dimension(80, cantidad.getPreferredSize().height));
        JPanel inventario = new JPanel(new BorderLayout(0, 4));
        inventario.add(barra(etiquetaBuscar, buscar, etiquetaCantidad, cantidad, agregar), BorderLayout.NORTH);
        inventario.add(new JScrollPane(catalogo), BorderLayout.CENTER);
        add(seccion("Catálogo", inventario), BorderLayout.CENTER);

        JScrollPane listaCarrito = new JScrollPane(carrito);
        listaCarrito.setPreferredSize(new Dimension(0, 108));
        JLabel etiquetaCarrito = new JLabel("Cantidad");
        etiquetaCarrito.setLabelFor(cantidadCarrito);
        cantidadCarrito.setPreferredSize(new Dimension(80, cantidadCarrito.getPreferredSize().height));
        JPanel librosElegidos = new JPanel(new BorderLayout());
        librosElegidos.add(listaCarrito, BorderLayout.CENTER);
        librosElegidos.add(barra(etiquetaCarrito, cantidadCarrito, modificar, quitar), BorderLayout.SOUTH);

        JPanel resumen = new JPanel(new GridLayout(0, 1));
        resumen.add(seccion("Cliente de la venta", barra(clienteActual, cliente, nuevoCliente, sinCliente)));
        resumen.add(barra(new JLabel("Código"), codigo, aplicar, quitarDescuento));

        JPanel pie = new JPanel(new BorderLayout(0, 4));
        pie.add(seccion("Libros elegidos", librosElegidos), BorderLayout.CENTER);
        pie.add(resumen, BorderLayout.SOUTH);
        acciones.setLayout(new BorderLayout());
        acciones.add(pie, BorderLayout.CENTER);
        JPanel cierre = new JPanel(new BorderLayout());
        cierre.add(barra(pagar, cancelar), BorderLayout.WEST);
        cierre.add(totales, BorderLayout.EAST);
        acciones.add(cierre, BorderLayout.SOUTH);
        configurarTamano(1050, 780);
    }
}
