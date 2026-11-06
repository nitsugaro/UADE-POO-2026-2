package controlador;

import modelo.*;
import excepciones.StockInsuficienteException;
import excepciones.DevolucionNoPermitidaException;
import servicio.VentaServicio;
import vista.*;
import javax.swing.*;
import java.awt.event.*;
import java.math.BigDecimal;

public final class InicioControlador {
    final VentanaPrincipal ventana;
    final VentaServicio servicio;
    private Empleado empleado;

    public InicioControlador(VentanaPrincipal ventana, VentaServicio servicio) {
        this.ventana = ventana;
        this.servicio = servicio;
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                int respuesta = JOptionPane.showConfirmDialog(
                        ventana, "Los datos de la sesión se perderán. ¿Cerrar?", "Cerrar librería",
                        JOptionPane.YES_NO_OPTION);

                if (respuesta == JOptionPane.YES_OPTION) {
                    ventana.dispose();
                }
            }
        });
    }

    public void mostrar() {
        InicioVista vista = new InicioVista();
        for (Empleado item : servicio.empleados()) {
            vista.empleados.addItem(item.getLegajo() + " · " + item.getNombre());
        }
        if (empleado != null) {
            vista.empleados.setSelectedIndex(servicio.empleados().indexOf(empleado));
        }
        vista.venta.addActionListener(e -> ejecutar(() -> {
            int indice = vista.empleados.getSelectedIndex();
            if (indice < 0) {
                throw new IllegalArgumentException("Seleccioná un empleado");
            }
            empleado = servicio.empleados().get(indice);
            nuevaVenta();
        }));
        vista.historial.addActionListener(e -> new HistorialVentasControlador(this).mostrar());
        vista.catalogo.addActionListener(e -> new CatalogoControlador(this).mostrar());
        ventana.mostrar(vista);
    }

    void nuevaVenta() {
        if (empleado == null) {
            mostrar();
            return;
        }
        new VentaControlador(this, servicio.nuevaVenta(empleado)).mostrar();
    }

    void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (IllegalArgumentException | IllegalStateException | ArithmeticException
                | StockInsuficienteException | DevolucionNoPermitidaException error) {
            ventana.error(error);
        }
    }

    static BigDecimal dinero(String texto) {
        try {
            return new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Ingresá un importe numérico válido");
        }
    }

    static int seleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Seleccioná una fila");
        }
        return tabla.convertRowIndexToModel(fila);
    }
}
