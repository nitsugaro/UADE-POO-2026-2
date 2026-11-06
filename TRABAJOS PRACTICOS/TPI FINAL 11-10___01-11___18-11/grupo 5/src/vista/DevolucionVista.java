package vista;

import javax.swing.*;
import java.awt.*;

public final class DevolucionVista extends PanelVista {
    public final JLabel resumen = new JLabel();
    public final JTextField motivo = new JTextField();
    public final JComboBox<String> empleados = new JComboBox<>();
    public final JCheckBox aptos = new JCheckBox("Todos los libros están sin uso y aptos para reventa");
    public final JButton confirmar = new JButton("Confirmar devolución total");
    public final JButton volver = new JButton("Volver");

    public DevolucionVista() {
        super("Devolver venta");

        FormularioPanel formulario = new FormularioPanel();
        formulario.agregar(new JLabel("Devolución total dentro de los 7 días de la compra."));
        formulario.agregar(seccion("Reintegro al medio de pago original", resumen));
        formulario.agregarCampo("Empleado responsable", empleados);
        formulario.agregarCampo("Motivo de la devolución *", motivo);
        formulario.agregar(aptos);
        formulario.agregar(new JLabel("Se devolverán todos los libros y se repondrá su stock."));
        add(formulario.alineadoArriba(), BorderLayout.CENTER);
        acciones.add(confirmar);
        acciones.add(volver);
        configurarTamano(660, 400);
    }
}
