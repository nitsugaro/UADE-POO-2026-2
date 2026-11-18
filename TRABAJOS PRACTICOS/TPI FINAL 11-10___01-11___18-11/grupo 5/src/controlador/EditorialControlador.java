package controlador;

import modelo.Editorial;
import vista.EditorialVista;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.List;

public final class EditorialControlador {
    private final Navegacion navegacion;
    private final EditorialVista vista = new EditorialVista();
    private List<Editorial> editoriales = List.of();

    public EditorialControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.nuevo.addActionListener(e -> editar(null));
        vista.editar.addActionListener(e -> editar(seleccionada()));
        vista.eliminar.addActionListener(e -> eliminar());
        vista.volver.addActionListener(e -> navegacion.mostrarInicio());
        vista.editoriales.getSelectionModel().addListSelectionListener(e -> actualizarBotones());
        actualizarBotones();
    }

    private Editorial seleccionada() {
        return editoriales.get(EntradaFormulario.seleccionado(vista.editoriales));
    }

    private void actualizarBotones() {
        boolean seleccion = vista.editoriales.getSelectedRow() >= 0;
        vista.editar.setEnabled(seleccion);
        vista.eliminar.setEnabled(seleccion);
    }

    private void actualizar() {
        editoriales = navegacion.servicio.editoriales();
        vista.modelo.setRowCount(0);
        for (Editorial editorial : editoriales) {
            vista.modelo.addRow(new Object[]{editorial.getId(), editorial.getNombre()});
        }
        actualizarBotones();
    }

    public void mostrar() {
        try {
            actualizar();
            navegacion.ventana.mostrar(vista, true);
        } catch (IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }

    private void editar(Editorial editorial) {
        JTextField nombre = new JTextField(editorial == null ? "" : editorial.getNombre(), 28);
        while (JOptionPane.showConfirmDialog(navegacion.ventana, nombre,
                editorial == null ? "Nueva editorial" : "Modificar editorial",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                navegacion.servicio.guardarEditorial(editorial, nombre.getText());
                actualizar();
                return;
            } catch (IllegalArgumentException | IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }

    private void eliminar() {
        try {
            Editorial editorial = seleccionada();
            navegacion.servicio.validarBajaEditorial(editorial);
            int respuesta = JOptionPane.showConfirmDialog(navegacion.ventana,
                    "¿Eliminar la editorial «" + editorial.getNombre() + "»?", "Eliminar editorial",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
            navegacion.servicio.eliminarEditorial(editorial);
            actualizar();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }
}
