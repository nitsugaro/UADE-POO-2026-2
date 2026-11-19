package controlador;

import modelo.Autor;
import modelo.Categoria;
import modelo.Editorial;
import modelo.Libro;
import vista.CatalogoVista;
import vista.LibroFormulario;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CatalogoControlador {
    private final Navegacion navegacion;
    private final CatalogoVista vista = new CatalogoVista();

    public CatalogoControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.nuevo.addActionListener(evento -> editar(null));
        vista.editar.addActionListener(evento -> editar(
                navegacion.servicio.libros().get(EntradaFormulario.seleccionado(vista.libros))));
        vista.editar.setEnabled(false);
        vista.libros.getSelectionModel().addListSelectionListener(evento ->
                vista.editar.setEnabled(vista.libros.getSelectedRow() >= 0));
        vista.volver.addActionListener(evento -> navegacion.mostrarInicio());
        configurarBusqueda();
    }

    private void configurarBusqueda() {
        var orden = new TableRowSorter<>(vista.modelo);
        vista.libros.setRowSorter(orden);
        vista.buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                filtrar();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                filtrar();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                filtrar();
            }

            private void filtrar() {
                orden.setRowFilter(RowFilter.regexFilter("(?iu)" + Pattern.quote(vista.buscar.getText().trim()),
                        0, 1, 2));
            }
        });
    }

    private void editar(Libro libro) {
        LibroFormulario formulario = new LibroFormulario();
        try {
            cargarOpciones(formulario.editorial, navegacion.servicio.editoriales().stream()
                    .map(Editorial::getNombre).toList());
            cargarOpciones(formulario.categoria, navegacion.servicio.categorias().stream()
                    .map(Categoria::getNombre).toList());
            cargarOpciones(formulario.autores, navegacion.servicio.autores().stream()
                    .map(Autor::getNombre).toList());
        } catch (IllegalStateException error) {
            navegacion.ventana.error(error);
            return;
        }
        if (libro != null) {
            formulario.isbn.setText(libro.getIsbn());
            formulario.titulo.setText(libro.getTitulo());
            formulario.autores.setSelectedItem(nombresDeAutores(libro, "; "));
            formulario.editorial.setSelectedItem(libro.getEditorial().getNombre());
            formulario.categoria.setSelectedItem(libro.getCategoria().getNombre());
            formulario.precio.setText(libro.getPrecioVenta().toPlainString());
            formulario.stock.setValue(libro.getStock());
            formulario.activo.setSelected(libro.isActivo());
        }
        String titulo = libro == null ? "Nuevo libro" : "Editar libro";
        while (JOptionPane.showConfirmDialog(navegacion.ventana, formulario, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION) {
            try {
                formulario.stock.commitEdit();
                navegacion.servicio.guardarLibro(libro, formulario.isbn.getText(), formulario.titulo.getText(),
                        Arrays.asList(texto(formulario.autores).split(";", -1)), texto(formulario.editorial),
                        texto(formulario.categoria), EntradaFormulario.dinero(formulario.precio.getText()),
                        (Integer) formulario.stock.getValue(), formulario.activo.isSelected());
                actualizar();
                return;
            } catch (ParseException error) {
                navegacion.ventana.informar("Ingresá un stock entero no negativo.");
            } catch (IllegalArgumentException | IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }

    private static void cargarOpciones(JComboBox<String> combo, List<String> opciones) {
        opciones.stream().distinct().forEach(combo::addItem);
        combo.setSelectedItem("");
    }

    private static String texto(JComboBox<String> combo) {
        // Leer el editor conserva lo escrito aunque aún no se haya presionado Enter.
        Object valor = combo.getEditor().getItem();
        return valor == null ? "" : valor.toString().trim();
    }

    private void actualizar() {
        vista.modelo.setRowCount(0);
        for (Libro libro : navegacion.servicio.libros()) {
            vista.modelo.addRow(new Object[]{
                libro.getIsbn(), libro.getTitulo(), nombresDeAutores(libro, ", "),
                libro.getEditorial().getNombre(), libro.getCategoria().getNombre(),
                libro.getPrecioVenta(), libro.getStock(), libro.isActivo() ? "Sí" : "No"
            });
        }
    }

    private String nombresDeAutores(Libro libro, String separador) {
        return libro.getAutores().stream().map(Autor::getNombre).collect(Collectors.joining(separador));
    }

    public void mostrar() {
        actualizar();
        navegacion.ventana.mostrar(vista, true);
    }
}
