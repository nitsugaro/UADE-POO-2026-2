package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class CatalogoVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("ISBN", "Título", "Autores", "Editorial", "Categoría", "Precio",
            "Stock", "Activo");
    public final JTable libros = new JTable(modelo);
    public final JTextField buscar = new JTextField(24);
    public final JButton nuevo = new JButton("Nuevo libro");
    public final JButton editar = new JButton("Editar libro");
    public final JButton volver = new JButton("Inicio");

    public CatalogoVista() {
        super("Administrar libros");
        configurar(libros);
        libros.getColumnModel().getColumn(1).setPreferredWidth(210);

        JLabel etiqueta = new JLabel("Buscar por título, autor o ISBN");
        etiqueta.setLabelFor(buscar);
        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.add(barra(etiqueta, buscar), BorderLayout.NORTH);
        contenido.add(new JScrollPane(libros), BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);

        acciones.add(volver);
        acciones.add(nuevo);
        acciones.add(editar);
    }
}
