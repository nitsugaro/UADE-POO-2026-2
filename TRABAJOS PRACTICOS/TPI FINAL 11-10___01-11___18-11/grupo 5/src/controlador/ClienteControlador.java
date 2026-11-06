package controlador;

import modelo.*;
import vista.ClienteVista;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public final class ClienteControlador {
    private final InicioControlador app;
    private final Venta venta;
    private final Runnable volver;

    public ClienteControlador(InicioControlador app, Venta venta, Runnable volver) {
        this.app = app;
        this.venta = venta;
        this.volver = volver;
    }

    public void mostrar() {
        ClienteVista vista = new ClienteVista();
        List<Cliente> clientes = app.servicio.clientes();
        List<Cliente> visibles = new ArrayList<>();
        Runnable filtrar = () -> {
            String filtro = vista.buscar.getText().trim().toLowerCase(Locale.ROOT);
            visibles.clear();
            vista.resultados.clear();
            for (Cliente cliente : clientes) {
                if (cliente.getNombre().toLowerCase(Locale.ROOT).contains(filtro)
                        || cliente.getDni().contains(filtro)) {
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
            vista.seleccionar.setEnabled(false);
        };
        vista.buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filtrar.run();
            }

            public void removeUpdate(DocumentEvent e) {
                filtrar.run();
            }

            public void changedUpdate(DocumentEvent e) {
                filtrar.run();
            }
        });
        vista.clientes.addListSelectionListener(e -> vista.seleccionar.setEnabled(vista.clientes.getSelectedIndex() >= 0));
        vista.seleccionar.addActionListener(e -> app.ejecutar(() -> {
            int indice = vista.clientes.getSelectedIndex();
            if (indice < 0) {
                return;
            }
            asignar(visibles.get(indice));
        }));
        vista.crear.addActionListener(e -> {
            vista.error.setText(" ");
            if (vista.nombre.getText().trim().isEmpty()) {
                vista.error.setText("Ingresá el nombre completo del cliente.");
                vista.nombre.requestFocusInWindow();
                return;
            }
            if (!vista.dni.getText().trim().matches("[0-9]+")) {
                vista.error.setText("Ingresá un DNI con números únicamente.");
                vista.dni.requestFocusInWindow();
                return;
            }
            app.ejecutar(() -> asignar(app.servicio.crearCliente(vista.nombre.getText(), vista.dni.getText())));
        });
        vista.nombre.addActionListener(e -> vista.dni.requestFocusInWindow());
        vista.dni.addActionListener(e -> vista.crear.doClick());
        vista.volver.addActionListener(e -> volver.run());
        filtrar.run();
        if (clientes.isEmpty()) {
            vista.opciones.setSelectedIndex(1);
        } else if (venta.getCliente() != null) {
            vista.clientes.setSelectedIndex(visibles.indexOf(venta.getCliente()));
        }
        app.ventana.mostrar(vista);
        SwingUtilities.invokeLater(() -> {
            if (clientes.isEmpty()) {
                vista.nombre.requestFocusInWindow();
            } else {
                vista.buscar.requestFocusInWindow();
            }
        });
    }

    private void asignar(Cliente cliente) {
        venta.cambiarCliente(cliente);
        venta.quitarPago();
        volver.run();
    }
}
