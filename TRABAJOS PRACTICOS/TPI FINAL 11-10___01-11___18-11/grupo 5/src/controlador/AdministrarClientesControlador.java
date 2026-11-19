package controlador;

import modelo.Cliente;
import vista.AdministrarClientesVista;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import vista.FormularioPanel;
import java.util.List;

public final class AdministrarClientesControlador {
    private final Navegacion navegacion;
    private final AdministrarClientesVista vista = new AdministrarClientesVista();
    private List<Cliente> clientes = List.of();
    public AdministrarClientesControlador(Navegacion navegacion) {
        this.navegacion = navegacion;
        vista.volver.addActionListener(e -> navegacion.mostrarInicio());
        vista.baja.addActionListener(e -> cambiar(false));
        vista.reactivar.addActionListener(e -> cambiar(true));
        vista.editar.addActionListener(e -> editar(seleccionado()));
        vista.nuevo.addActionListener(e -> editar(null));
        vista.clientes.getSelectionModel().addListSelectionListener(e -> botones());
    }
    private Cliente seleccionado() { return clientes.get(EntradaFormulario.seleccionado(vista.clientes)); }
    private void botones() {
        boolean seleccion = vista.clientes.getSelectedRow() >= 0;
        vista.editar.setEnabled(seleccion);
        vista.baja.setEnabled(seleccion && seleccionado().isActivo());
        vista.reactivar.setEnabled(seleccion && !seleccionado().isActivo());
    }
    public void mostrar() {
        clientes = navegacion.servicio.clientes();
        vista.modelo.setRowCount(0);
        for (Cliente c : clientes) vista.modelo.addRow(new Object[]{c.getNombre(), c.getDni(), c.isActivo() ? "Activo" : "Inactivo"});
        botones();
        navegacion.ventana.mostrar(vista, true);
    }
    private void editar(Cliente cliente) {
        JTextField nombre = new JTextField(cliente == null ? "" : cliente.getNombre(), 28);
        JTextField dni = new JTextField(cliente == null ? "" : cliente.getDni(), 28);
        FormularioPanel formulario = new FormularioPanel();
        formulario.agregarCampo("Nombre y apellido", nombre);
        formulario.agregarCampo("DNI", dni);
        while (JOptionPane.showConfirmDialog(navegacion.ventana, formulario, cliente == null ? "Nuevo cliente" : "Editar cliente",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                if (cliente == null) navegacion.servicio.crearCliente(nombre.getText(), dni.getText());
                else navegacion.servicio.modificarCliente(cliente, nombre.getText(), dni.getText());
                mostrar();
                return;
            } catch (IllegalArgumentException | IllegalStateException error) {
                navegacion.ventana.error(error);
            }
        }
    }

    private void cambiar(boolean activo) {
        try {
            Cliente cliente = seleccionado();
            int respuesta = JOptionPane.showConfirmDialog(navegacion.ventana,
                    (activo ? "¿Reactivar a " : "¿Dar de baja a ") + cliente.getNombre() + "?",
                    "Estado del cliente", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
            navegacion.servicio.cambiarActividadCliente(cliente, activo);
            mostrar();
        } catch (IllegalArgumentException | IllegalStateException error) {
            navegacion.ventana.error(error);
        }
    }
}
