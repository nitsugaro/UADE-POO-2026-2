package controlador;

import modelo.Cliente;
import modelo.Libro;
import modelo.Prestamo;
import vista.PrestamoVista;

import javax.swing.RowFilter;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class PrestamoControlador {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Navegacion navegacion;
    private final PrestamoVista vista = new PrestamoVista();
    private List<Libro> disponibles;
    private List<Cliente> socios;
    private List<Prestamo> prestamos = List.of();
    private List<Prestamo> pendientes = List.of();

    PrestamoControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.prestar.setEnabled(false);
        vista.devolver.setEnabled(false);
        vista.libros.getSelectionModel().addListSelectionListener(evento -> habilitarPrestamo());
        vista.socios.getSelectionModel().addListSelectionListener(evento -> habilitarPrestamo());
        configurarBusqueda(vista.socios, vista.buscarSocio, vista.padron, 0, 1);
        configurarBusqueda(vista.porDevolver, vista.buscarDevolucion, vista.pendientes, 0, 4);
        vista.porDevolver.getSelectionModel().addListSelectionListener(evento ->
                vista.devolver.setEnabled(vista.porDevolver.getSelectedRow() >= 0));
        vista.prestar.addActionListener(evento -> operar(() -> {
            Libro libro = disponibles.get(EntradaFormulario.seleccionado(vista.libros));
            Cliente socio = socios.get(EntradaFormulario.seleccionado(vista.socios));
            navegacion.servicio.prestar(socio.getDni(), libro);
        }, "Préstamo registrado."));
        vista.devolver.addActionListener(evento -> operar(() -> {
            Prestamo prestamo = pendientes.get(EntradaFormulario.seleccionado(vista.porDevolver));
            navegacion.servicio.devolverPrestamo(prestamo);
        }, "Devolución registrada."));
        vista.volver.addActionListener(evento -> navegacion.mostrarInicio());
    }

    private void habilitarPrestamo() {
        vista.prestar.setEnabled(vista.libros.getSelectedRow() >= 0 && vista.socios.getSelectedRow() >= 0);
    }

    private void configurarBusqueda(JTable tabla, JTextField campo, DefaultTableModel modelo, int... columnas) {
        var orden = new TableRowSorter<>(modelo);
        tabla.setRowSorter(orden);
        campo.getDocument().addDocumentListener(new DocumentListener() {
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
                String busqueda = EntradaFormulario.normalizarBusqueda(campo.getText());
                tabla.clearSelection();
                orden.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> fila) {
                        for (int columna : columnas) {
                            if (EntradaFormulario.normalizarBusqueda(fila.getStringValue(columna)).contains(busqueda)) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
            }
        });
    }

    void mostrar() {
        try {
            socios = navegacion.servicio.socios().stream().filter(Cliente::isActivo).toList();
            vista.padron.setRowCount(0);
            for (Cliente socio : socios) {
                vista.padron.addRow(new Object[]{socio.getNombre(), socio.getDni()});
            }
            actualizar();
            navegacion.ventana.mostrar(vista, true);
        } catch (IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }

    private void operar(Runnable operacion, String mensaje) {
        try {
            operacion.run();
            actualizar();
            navegacion.ventana.informar(mensaje);
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException error) {
            navegacion.ventana.error(error);
        }
    }

    private void actualizar() {
        vista.libros.clearSelection();
        vista.prestamos.clearSelection();
        vista.porDevolver.clearSelection();
        disponibles = navegacion.servicio.libros().stream().filter(libro -> libro.hayDisponibilidad(1)).toList();
        prestamos = navegacion.servicio.prestamos();
        vista.catalogo.setRowCount(0);
        for (Libro libro : disponibles) {
            vista.catalogo.addRow(new Object[]{libro.getIsbn(), libro.getTitulo(), libro.getStock()});
        }
        pendientes = prestamos.stream().filter(prestamo -> prestamo.devueltoEn() == null).toList();
        Map<Long, String> isbnPorLibro = navegacion.servicio.libros().stream()
                .collect(Collectors.toMap(Libro::getId, Libro::getIsbn));
        vista.pendientes.setRowCount(0);
        for (Prestamo prestamo : pendientes) {
            vista.pendientes.addRow(new Object[]{prestamo.titulo(), prestamo.socio(), prestamo.dni(),
                prestamo.fecha().format(FECHA), isbnPorLibro.getOrDefault(prestamo.libroId(), "")});
        }
        vista.historial.setRowCount(0);
        for (Prestamo prestamo : prestamos) {
            String devolucion = prestamo.devueltoEn() == null ? "Pendiente" : prestamo.devueltoEn().format(FECHA);
            vista.historial.addRow(new Object[]{prestamo.titulo(), prestamo.socio(), prestamo.dni(),
                prestamo.fecha().format(FECHA), devolucion});
        }
    }
}
