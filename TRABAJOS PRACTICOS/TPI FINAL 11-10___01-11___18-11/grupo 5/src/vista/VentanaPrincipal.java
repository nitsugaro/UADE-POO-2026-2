package vista;

import javax.swing.*;
import java.awt.*;

public final class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        super("Librería — El Grupo 5");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void mostrar(JPanel panel) {
        boolean cambiarTamano = getContentPane().getClass() != panel.getClass();
        setContentPane(panel);

        if (cambiarTamano || !isVisible()) {
            ajustarTamano(panel);
        }
        revalidate();
        repaint();
    }

    private void ajustarTamano(JPanel panel) {
        setExtendedState(JFrame.NORMAL);

        setMinimumSize(new Dimension(0, 0));
        pack();
        Insets bordes = getInsets();
        Dimension minimo = panel.getMinimumSize();
        setMinimumSize(new Dimension(
                minimo.width + bordes.left + bordes.right,
                minimo.height + bordes.top + bordes.bottom));

        setLocationRelativeTo(null);
    }

    public void error(Exception error) {
        String mensaje = error.getMessage();
        if (mensaje == null) {
            mensaje = "No se pudo completar la operación";
        }

        JOptionPane.showMessageDialog(this, mensaje, "Operación no realizada", JOptionPane.ERROR_MESSAGE);
    }

    public void informar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}
