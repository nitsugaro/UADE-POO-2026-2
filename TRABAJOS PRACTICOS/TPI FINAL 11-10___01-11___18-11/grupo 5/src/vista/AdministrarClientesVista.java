package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public final class AdministrarClientesVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("Nombre", "DNI", "Estado");
    public final JTable clientes = new JTable(modelo);
    public final JButton baja = new JButton("Dar de baja");
    public final JButton reactivar = new JButton("Reactivar");
    public final JButton editar = new JButton("Editar cliente");
    public final JButton nuevo = new JButton("Nuevo cliente");
    public final JButton volver = new JButton("Inicio");
    public AdministrarClientesVista() {
        super("Administrar clientes");
        configurar(clientes);
        add(new JScrollPane(clientes), BorderLayout.CENTER);
        acciones.add(volver);
        acciones.add(nuevo);
        acciones.add(baja);
        acciones.add(reactivar);
        acciones.add(editar);
        configurarTamano(700, 420);
    }
}
