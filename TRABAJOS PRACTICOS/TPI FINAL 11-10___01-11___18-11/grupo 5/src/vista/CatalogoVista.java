package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class CatalogoVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("ISBN", "Título", "Autores", "Editorial", "Categoría", "Precio",
            "Stock", "Activo");
    public final JTable libros = new JTable(modelo);
    public final JTextField precio = new JTextField(8);
    public final JSpinner cantidad = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
    public final JButton cambiar = new JButton("Cambiar precio");
    public final JButton stock = new JButton("Ingresar stock");
    public final JButton actividad = new JButton("Activar / desactivar");
    public final JButton volver = new JButton("Inicio");

    public CatalogoVista() {
        super("Catálogo");
        configurar(libros);

        JPanel contenido = new JPanel(new BorderLayout(0, 12));
        contenido.add(seccion("Libros disponibles", new JScrollPane(libros)), BorderLayout.CENTER);

        FormularioPanel edicion = new FormularioPanel();
        edicion.agregar(new JLabel("Seleccioná un libro de la tabla para modificarlo."));
        edicion.agregar(barra(new JLabel("Precio ($)"), precio, cambiar));
        cantidad.setPreferredSize(new Dimension(100, cantidad.getPreferredSize().height));
        edicion.agregar(barra(new JLabel("Unidades a ingresar"), cantidad, stock));
        edicion.agregar(barra(actividad));
        contenido.add(seccion("Modificar libro seleccionado", edicion.alineadoArriba()), BorderLayout.SOUTH);
        add(contenido, BorderLayout.CENTER);
        acciones.add(volver);
    }
}
