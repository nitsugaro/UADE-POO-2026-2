package vista;

import javax.swing.*;
import java.awt.*;

public final class InicioVista extends PanelVista {
    public final JComboBox<String> empleados = new JComboBox<>();
    public final JButton venta = new JButton("Nueva venta");
    public final JButton historial = new JButton("Historial de ventas");
    public final JButton catalogo = new JButton("Catálogo");

    public InicioVista() {
        super("Librería · Inicio");

        FormularioPanel formulario = new FormularioPanel();
        formulario.agregarCampo("Empleado responsable", empleados);
        formulario.agregar(barra(venta, historial, catalogo));
        formulario.agregar(new JLabel("<html>Sesión de demostración.<br>Los datos se pierden al cerrar.</html>"));
        remove(acciones);
        add(formulario.alineadoArriba(), BorderLayout.CENTER);
        configurarTamano(580, 240);
    }
}
