package vista;

import javax.swing.*;
import java.awt.*;

public final class ResumenVentaVista extends PanelVista {
    public final JTextArea detalle = new JTextArea();
    public final JButton inicio = new JButton("Inicio");
    public final JButton nueva = new JButton("Nueva venta");
    public final JButton devolver = new JButton("Devolver venta");

    public ResumenVentaVista() {
        super("Detalle de venta");
        detalle.setEditable(false);
        detalle.setMargin(new Insets(12, 12, 12, 12));
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        detalle.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(detalle), BorderLayout.CENTER);
        acciones.add(inicio);
        acciones.add(nueva);
        acciones.add(devolver);
        configurarTamano(760, 600);
    }
}
