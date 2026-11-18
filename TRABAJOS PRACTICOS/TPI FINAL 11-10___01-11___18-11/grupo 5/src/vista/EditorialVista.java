package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public final class EditorialVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("ID", "Nombre");
    public final JTable editoriales = new JTable(modelo);
    public final JButton nuevo = new JButton("Nueva editorial");
    public final JButton editar = new JButton("Modificar");
    public final JButton eliminar = new JButton("Eliminar");
    public final JButton volver = new JButton("Inicio");

    public EditorialVista() {
        super("Administrar editoriales");
        configurar(editoriales);
        add(new JScrollPane(editoriales), BorderLayout.CENTER);
        acciones.add(volver);
        acciones.add(nuevo);
        acciones.add(editar);
        acciones.add(eliminar);
        configurarTamano(700, 420);
    }
}
