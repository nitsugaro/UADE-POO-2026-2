package vista;

import javax.swing.*;
import java.awt.*;

public final class ClienteVista extends PanelVista {
    public final JTabbedPane opciones = new JTabbedPane();
    public final JTextField buscar = new JTextField(28);
    public final DefaultListModel<String> resultados = new DefaultListModel<>();
    public final JList<String> clientes = new JList<>(resultados);
    public final JTextField nombre = new JTextField(28);
    public final JTextField dni = new JTextField(28);
    public final JLabel estadoBusqueda = new JLabel(" ");
    public final JLabel error = new JLabel(" ");
    public final JButton seleccionar = new JButton("Agregar seleccionado a la venta");
    public final JButton crear = new JButton("Crear cliente y agregar a la venta");
    public final JButton volver = new JButton("Volver sin cambios");

    public ClienteVista() {
        super("Cliente de la venta");

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.add(new JLabel("Elegí un cliente existente o registrá uno nuevo para esta venta."),
                BorderLayout.NORTH);

        JPanel existentes = new JPanel(new BorderLayout(0, 10));
        existentes.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel filtro = new JPanel(new BorderLayout(0, 6));
        JLabel etiquetaBuscar = new JLabel("Buscar por nombre o DNI");
        etiquetaBuscar.setLabelFor(buscar);
        filtro.add(etiquetaBuscar, BorderLayout.NORTH);
        filtro.add(buscar, BorderLayout.CENTER);
        existentes.add(filtro, BorderLayout.NORTH);
        clientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientes.setVisibleRowCount(5);
        clientes.setFixedCellHeight(28);
        existentes.add(new JScrollPane(clientes), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(0, 8));
        pie.add(estadoBusqueda, BorderLayout.NORTH);
        pie.add(seleccionar, BorderLayout.SOUTH);
        existentes.add(pie, BorderLayout.SOUTH);

        JPanel nuevo = new JPanel(new BorderLayout());
        nuevo.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        FormularioPanel formulario = new FormularioPanel();
        formulario.agregarCampo("Nombre completo *", nombre);
        formulario.agregarCampo("DNI *", dni);
        formulario.agregar(new JLabel("Solo números, sin puntos ni espacios. Ejemplo: 30123456"));
        formulario.agregar(new JLabel("* Campos obligatorios"));
        formulario.agregar(error);
        formulario.agregar(barra(crear));
        error.setForeground(new Color(170, 30, 30));
        nuevo.add(formulario.alineadoArriba(), BorderLayout.CENTER);
        opciones.addTab("Buscar cliente", existentes);
        opciones.addTab("Crear cliente", nuevo);
        contenido.add(opciones, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);
        acciones.add(volver);
        configurarTamano(660, 480);
    }
}
