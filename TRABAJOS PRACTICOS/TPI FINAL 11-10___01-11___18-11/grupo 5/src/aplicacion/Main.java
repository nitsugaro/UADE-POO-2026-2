package aplicacion;

import controlador.InicioControlador;
import servicio.VentaServicio;
import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

/** Aplicacion Swing sin dependencias externas; datos en memoria por sesion. */
public final class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            new InicioControlador(ventana, new VentaServicio()).mostrar();
            ventana.setVisible(true);
        });
    }
}
