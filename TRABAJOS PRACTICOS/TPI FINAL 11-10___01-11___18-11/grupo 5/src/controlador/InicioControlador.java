package controlador;

import modelo.Cliente;
import vista.FormularioPanel;
import vista.InicioVista;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

public final class InicioControlador {
    private final Navegacion navegacion;
    private final InicioVista vista = new InicioVista();

    public InicioControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.venta.addActionListener(evento -> navegacion.nuevaVenta());
        vista.historial.addActionListener(evento -> navegacion.mostrarHistorial());
        vista.catalogo.addActionListener(evento -> navegacion.mostrarCatalogo());
        vista.prestamos.addActionListener(evento -> new PrestamoControlador(navegacion).mostrar());
        vista.socio.addActionListener(evento -> asociar());
    }

    public void mostrar() {
        navegacion.ventana.mostrar(vista, true);
    }

    private void asociar() {
        JTextField nombre = new JTextField(24);
        JTextField dni = new JTextField(12);
        FormularioPanel formulario = new FormularioPanel();
        formulario.agregarCampo("DNI", dni);
        formulario.agregarCampo("Nombre y apellido (si todavía no sos cliente)", nombre);
        while (JOptionPane.showConfirmDialog(navegacion.ventana, formulario, "Hacerse socio",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                Cliente socio = navegacion.servicio.asociar(nombre.getText(), dni.getText());
                navegacion.ventana.informar("Socio registrado: " + socio.getNombre());
                return;
            } catch (IllegalArgumentException | IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }
}
