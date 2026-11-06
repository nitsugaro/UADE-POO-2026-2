package vista;

import javax.swing.*;
import java.awt.*;

public final class VentanaPrincipal extends JFrame {
    public VentanaPrincipal() {
        super("Librería — El Grupo 5");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                confirmarCierre();
            }
        });
    }

    private void confirmarCierre() {
        int respuesta = JOptionPane.showConfirmDialog(
                this, "Los datos de la sesión se perderán. ¿Cerrar?", "Cerrar librería",
                JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    public void mostrar(JPanel panel, boolean ajustarTamano) {
        setContentPane(panel);

        if (ajustarTamano || !isVisible()) {
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
