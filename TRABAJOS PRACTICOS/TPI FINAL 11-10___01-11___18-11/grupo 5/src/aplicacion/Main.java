package aplicacion;

import controlador.Navegacion;
import servicio.LibreriaServicio;
import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            new Navegacion(ventana, new LibreriaServicio()).mostrarInicio();
            ventana.setVisible(true);
        });
    }
}
