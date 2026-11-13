package aplicacion;

import controlador.Navegacion;
import servicio.LibreriaServicio;
import vista.VentanaPrincipal;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibreriaServicio servicio;
            try {
                servicio = new LibreriaServicio();
            } catch (RuntimeException error) {
                error.printStackTrace();
                JOptionPane.showMessageDialog(null, "No se pudo cargar la base SQLite de la librería.",
                        "Error al iniciar", JOptionPane.ERROR_MESSAGE);
                return;
            }
            VentanaPrincipal ventana = new VentanaPrincipal(servicio::hayVentaEnPreparacion);
            new Navegacion(ventana, servicio).mostrarInicio();
            ventana.setVisible(true);
        });
    }
}
