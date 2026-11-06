package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class HistorialVentasVista extends PanelVista {
    public final DefaultTableModel modelo = tabla("Venta", "Fecha", "Cliente", "Estado", "Total");
    public final JTable ventas = new JTable(modelo);
    public final JButton detalle = new JButton("Ver detalle");
    public final JButton volver = new JButton("Inicio");

    public HistorialVentasVista() {
        super("Historial de esta sesión");
        configurar(ventas);

        JPanel contenido = new JPanel(new BorderLayout(0, 8));
        contenido.add(new JLabel("Seleccioná una venta para consultar su detalle o iniciar una devolución."),
                BorderLayout.NORTH);
        contenido.add(new JScrollPane(ventas), BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);
        acciones.add(detalle);
        acciones.add(volver);
    }
}
