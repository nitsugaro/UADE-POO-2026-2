package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Utilidades visuales compartidas, sin reglas del negocio. */
public class PanelVista extends JPanel {
    protected final JPanel acciones = barra();

    public PanelVista(String titulo) {
        super(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        JLabel encabezado = new JLabel(titulo);
        encabezado.setFont(encabezado.getFont().deriveFont(Font.BOLD, 22f));
        add(encabezado, BorderLayout.NORTH);
        add(acciones, BorderLayout.SOUTH);
        configurarTamano(1000, 680);
    }

    protected void configurarTamano(int ancho, int alto) {
        setPreferredSize(new Dimension(ancho, alto));
        setMinimumSize(new Dimension(ancho, alto));
    }

    public static JPanel barra(JComponent... componentes) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        for (JComponent componente : componentes) {
            panel.add(componente);
        }
        return panel;
    }

    public static JPanel seccion(String titulo, JComponent contenido) {

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        panel.add(contenido, BorderLayout.CENTER);
        return panel;
    }

    public static DefaultTableModel tabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    public static void configurar(JTable tabla) {
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
    }
}
