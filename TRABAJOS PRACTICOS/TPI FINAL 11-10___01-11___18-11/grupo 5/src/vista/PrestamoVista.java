package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public final class PrestamoVista extends PanelVista {
    public final DefaultTableModel catalogo = tabla("ISBN", "Título", "Disponibles");
    public final DefaultTableModel historial = tabla("Libro", "Socio", "DNI", "Fecha", "Devuelto");
    public final DefaultTableModel padron = tabla("Nombre y apellido", "DNI");
    public final JTable socios = new JTable(padron);
    public final JTable libros = new JTable(catalogo);
    public final JTable prestamos = new JTable(historial);
    public final JTextField buscarSocio = new JTextField(18);
    public final JButton prestar = new JButton("Registrar préstamo");
    public final JButton devolver = new JButton("Registrar devolución");
    public final JButton volver = new JButton("Inicio");
    public final JTabbedPane opciones = new JTabbedPane();

    public PrestamoVista() {
        super("Préstamos y devoluciones");
        configurar(libros);
        configurar(prestamos);

        configurar(socios);
        socios.getColumnModel().getColumn(0).setPreferredWidth(210);
        libros.getColumnModel().getColumn(1).setPreferredWidth(210);

        JLabel etiqueta = new JLabel("Nombre, apellido o DNI");
        etiqueta.setLabelFor(buscarSocio);
        FormularioPanel busqueda = new FormularioPanel();
        busqueda.agregar(etiqueta);
        busqueda.agregar(buscarSocio);
        JPanel seleccionSocio = new JPanel(new BorderLayout());
        seleccionSocio.add(busqueda, BorderLayout.NORTH);
        seleccionSocio.add(new JScrollPane(socios), BorderLayout.CENTER);

        JPanel seleccion = new JPanel(new GridLayout(1, 2, 12, 0));
        seleccion.add(seccion("Seleccionar socio", seleccionSocio));
        seleccion.add(seccion("Seleccionar libro", new JScrollPane(libros)));
        JPanel solicitud = new JPanel(new BorderLayout(0, 8));
        solicitud.add(seleccion, BorderLayout.CENTER);
        solicitud.add(barra(prestar), BorderLayout.SOUTH);

        JPanel movimientos = new JPanel(new BorderLayout(0, 8));
        movimientos.add(new JScrollPane(prestamos), BorderLayout.CENTER);
        movimientos.add(barra(devolver), BorderLayout.SOUTH);

        opciones.addTab("Nuevo préstamo", solicitud);
        opciones.addTab("Historial de préstamos", movimientos);
        add(opciones, BorderLayout.CENTER);
        acciones.add(volver);
    }
}
