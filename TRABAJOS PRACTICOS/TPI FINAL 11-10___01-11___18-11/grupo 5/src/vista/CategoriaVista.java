package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public final class CategoriaVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("ID", "Nombre");
    public final JTable categorias = new JTable(modelo);
    public final JButton nuevo = new JButton("Nueva categoría");
    public final JButton editar = new JButton("Modificar");
    public final JButton eliminar = new JButton("Eliminar");
    public final JButton volver = new JButton("Inicio");

    public CategoriaVista() {
        super("Administrar categorías");
        configurar(categorias);
        add(new JScrollPane(categorias), BorderLayout.CENTER);
        acciones.add(volver);
        acciones.add(nuevo);
        acciones.add(editar);
        acciones.add(eliminar);
        configurarTamano(700, 420);
    }
}
