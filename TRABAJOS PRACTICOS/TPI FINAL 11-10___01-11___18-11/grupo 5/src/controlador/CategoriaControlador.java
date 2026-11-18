package controlador;

import modelo.Categoria;
import vista.CategoriaVista;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.util.List;

public final class CategoriaControlador {
    private final Navegacion navegacion;
    private final CategoriaVista vista = new CategoriaVista();
    private List<Categoria> categorias = List.of();

    public CategoriaControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.nuevo.addActionListener(e -> editar(null));
        vista.editar.addActionListener(e -> editar(seleccionada()));
        vista.eliminar.addActionListener(e -> eliminar());
        vista.volver.addActionListener(e -> navegacion.mostrarInicio());
        vista.categorias.getSelectionModel().addListSelectionListener(e -> actualizarBotones());
        actualizarBotones();
    }

    private Categoria seleccionada() {
        return categorias.get(EntradaFormulario.seleccionado(vista.categorias));
    }

    private void actualizarBotones() {
        boolean seleccion = vista.categorias.getSelectedRow() >= 0;
        vista.editar.setEnabled(seleccion);
        vista.eliminar.setEnabled(seleccion);
    }

    private void actualizar() {
        categorias = navegacion.servicio.categorias();
        vista.modelo.setRowCount(0);
        for (Categoria categoria : categorias) {
            vista.modelo.addRow(new Object[]{categoria.getId(), categoria.getNombre()});
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

    private void editar(Categoria categoria) {
        JTextField nombre = new JTextField(categoria == null ? "" : categoria.getNombre(), 28);
        while (JOptionPane.showConfirmDialog(navegacion.ventana, nombre,
                categoria == null ? "Nueva categoría" : "Modificar categoría",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                navegacion.servicio.guardarCategoria(categoria, nombre.getText());
                actualizar();
                return;
            } catch (IllegalArgumentException | IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }

    private void eliminar() {
        try {
            Categoria categoria = seleccionada();
            navegacion.servicio.validarBajaCategoria(categoria);
            int respuesta = JOptionPane.showConfirmDialog(navegacion.ventana,
                    "¿Eliminar la categoría «" + categoria.getNombre() + "»?", "Eliminar categoría",
                    JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
            navegacion.servicio.eliminarCategoria(categoria);
            actualizar();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }
}
