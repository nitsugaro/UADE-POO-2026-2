package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class VentaVista extends PanelVista {
    public final JTextField buscar = new JTextField(22);
    public final DefaultTableModel catalogoModelo = tabla("ISBN", "Título", "Precio", "Stock");
    public final JTable catalogo = new JTable(catalogoModelo);
    public final DefaultTableModel carritoModelo = tabla("Título", "Cantidad", "Precio unitario", "Subtotal");
    public final JTable carrito = new JTable(carritoModelo);
    public final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    public final JButton agregar = new JButton("Agregar");
    public final JButton modificar = new JButton("Cambiar cantidad");
    public final JButton quitar = new JButton("Quitar");
    public final JButton cliente = new JButton("Seleccionar / crear cliente");
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

        JPanel cuerpo = new JPanel(new BorderLayout(8, 8));

        JPanel busqueda = barra();
        busqueda.add(new JLabel("Buscar título / ISBN"));
        busqueda.add(buscar);
        busqueda.add(new JLabel("Cantidad"));
        cantidad.setPreferredSize(new Dimension(90, cantidad.getPreferredSize().height));
        busqueda.add(cantidad);
        busqueda.add(agregar);
        cuerpo.add(busqueda, BorderLayout.NORTH);

        JSplitPane tablas = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                seccion("Catálogo · seleccioná un libro para agregar", new JScrollPane(catalogo)),
                seccion("Libros de la venta", new JScrollPane(carrito)));
        tablas.setResizeWeight(0.5);
        tablas.setDividerLocation(180);
        cuerpo.add(tablas, BorderLayout.CENTER);

        FormularioPanel opciones = new FormularioPanel();

        JPanel linea = barra();
        linea.add(modificar);
        linea.add(quitar);
        opciones.agregar(linea);

        JPanel personas = barra();
        personas.add(clienteActual);
        personas.add(cliente);
        personas.add(sinCliente);
        opciones.agregar(personas);

        JPanel descuento = barra();
        descuento.add(new JLabel("Código"));
        descuento.add(codigo);
        descuento.add(aplicar);
        descuento.add(quitarDescuento);
        opciones.agregar(descuento);
        opciones.agregar(totales);
        cuerpo.add(opciones.alineadoArriba(), BorderLayout.SOUTH);
        add(cuerpo, BorderLayout.CENTER);
        acciones.add(pagar);
        acciones.add(cancelar);
        configurarTamano(1050, 780);
    }
}
