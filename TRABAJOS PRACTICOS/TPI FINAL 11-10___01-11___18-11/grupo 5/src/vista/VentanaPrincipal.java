package vista;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BooleanSupplier;

public final class VentanaPrincipal extends JFrame {
    public VentanaPrincipal(BooleanSupplier hayVentaEnPreparacion) {
        super("Librería — El Grupo 5");
        setIconImages(List.of(IconoLibro.crear(16), IconoLibro.crear(32), IconoLibro.crear(64)));
        if (Taskbar.isTaskbarSupported()) {
            Taskbar barra = Taskbar.getTaskbar();
            if (barra.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                barra.setIconImage(IconoLibro.crear(128));
            }
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evento) {
                if (hayVentaEnPreparacion.getAsBoolean()) {
                    confirmarCierre();
                } else {
                    dispose();
                }
            }
        });
    }

    private void confirmarCierre() {
        int respuesta = JOptionPane.showConfirmDialog(
                this, "Se descartará la venta en preparación. ¿Cerrar la librería?", "Cerrar librería",
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
