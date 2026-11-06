package controlador;

import modelo.Cliente;
import modelo.Venta;
import vista.ClienteVista;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClienteControlador {
    private final Navegacion navegacion;
    private final Venta venta;
    private final Runnable volver;
    private final ClienteVista vista = new ClienteVista();
    private final List<Cliente> visibles = new ArrayList<>();

    public ClienteControlador(Navegacion navegacion, Venta venta, Runnable volver) {
        this.navegacion = navegacion;
        this.venta = venta;
        this.volver = volver;
        conectarEventos();
    }

    private void conectarEventos() {
        vista.buscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                filtrarClientes();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                filtrarClientes();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                filtrarClientes();
            }
        });
        vista.clientes.addListSelectionListener(evento -> actualizarSeleccion());
        vista.seleccionar.addActionListener(evento -> seleccionarCliente());
        vista.crear.addActionListener(evento -> crearCliente());
        vista.volver.addActionListener(evento -> volver.run());
        vista.nombre.addActionListener(evento -> vista.dni.requestFocusInWindow());
        vista.dni.addActionListener(evento -> crearCliente());
    }

    public void mostrar() {
        filtrarClientes();
        if (navegacion.servicio.clientes().isEmpty()) {
            vista.opciones.setSelectedIndex(1);
        } else if (venta.getCliente() != null) {
            vista.clientes.setSelectedIndex(visibles.indexOf(venta.getCliente()));
        }
        navegacion.ventana.mostrar(vista, true);
        SwingUtilities.invokeLater(this::enfocarCampoInicial);
    }

    private void enfocarCampoInicial() {
        if (vista.opciones.getSelectedIndex() == 1) {
            vista.nombre.requestFocusInWindow();
        } else {
            vista.buscar.requestFocusInWindow();
        }
    }

    private void filtrarClientes() {
        String filtro = vista.buscar.getText().trim().toLowerCase(Locale.ROOT);
        List<Cliente> clientes = navegacion.servicio.clientes();
        visibles.clear();
        vista.resultados.clear();

        for (Cliente cliente : clientes) {
            boolean coincideNombre = cliente.getNombre().toLowerCase(Locale.ROOT).contains(filtro);
            boolean coincideDni = cliente.getDni().contains(filtro);
            if (coincideNombre || coincideDni) {
                visibles.add(cliente);
                vista.resultados.addElement(cliente.getNombre() + " · DNI " + cliente.getDni());
            }
        }

        if (clientes.isEmpty()) {
            vista.estadoBusqueda.setText("Todavía no hay clientes. Registrá el primero en Crear cliente.");
        } else if (visibles.isEmpty()) {
            vista.estadoBusqueda.setText("No hay coincidencias. Probá otro nombre o DNI.");
        } else {
            vista.estadoBusqueda.setText("Seleccioná el cliente que querés agregar a la venta.");
        }
        actualizarSeleccion();
    }

    private void actualizarSeleccion() {
        vista.seleccionar.setEnabled(vista.clientes.getSelectedIndex() >= 0);
    }

    private void seleccionarCliente() {
        int indice = vista.clientes.getSelectedIndex();
        if (indice >= 0) {
            asignarCliente(visibles.get(indice));
        }
    }

    private void crearCliente() {
        String nombre = vista.nombre.getText().trim();
        String dni = vista.dni.getText().trim();
        vista.error.setText(" ");

        if (nombre.isEmpty()) {
            vista.error.setText("Ingresá el nombre completo del cliente.");
            vista.nombre.requestFocusInWindow();
            return;
        }
        if (!dni.matches("[0-9]+")) {
            vista.error.setText("Ingresá un DNI con números únicamente.");
            vista.dni.requestFocusInWindow();
            return;
        }

        try {
            Cliente cliente = navegacion.servicio.crearCliente(nombre, dni);
            asignarCliente(cliente);
        } catch (IllegalArgumentException error) {
            vista.error.setText(error.getMessage());
        }
    }

    private void asignarCliente(Cliente cliente) {
        venta.cambiarCliente(cliente);
        venta.quitarPago();
        volver.run();
    }
}
