package vista;

import javax.swing.*;
import java.awt.*;

public final class PagoVista extends PanelVista {
    public final JComboBox<String> medio = new JComboBox<>(new String[]{
        "Efectivo", "Tarjeta (simulada)"
    });
    public final JTextField recibido = new JTextField(24);
    public final JTextField referencia = new JTextField(24);
    public final JLabel total = new JLabel();
    public final JLabel vuelto = new JLabel("Ingresá el monto recibido para consultar el vuelto.");
    public final JLabel cliente = new JLabel();
    public final JLabel error = new JLabel(" ");
    public final JButton confirmar = new JButton("Confirmar pago y venta");
    public final JButton volver = new JButton("Volver a la venta");

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel campos = new JPanel(tarjetas);

    public PagoVista() {
        super("Pago de la venta");

        total.setFont(total.getFont().deriveFont(Font.BOLD, 26f));
        error.setForeground(new Color(170, 30, 30));

        FormularioPanel formulario = new FormularioPanel();
        formulario.agregar(new JLabel("Total a pagar"));
        formulario.agregar(total);
        formulario.agregar(new JLabel("Medio de pago"));
        formulario.agregar(medio);

        FormularioPanel efectivo = new FormularioPanel();
        JLabel etiquetaRecibido = new JLabel("Monto recibido ($)");
        etiquetaRecibido.setLabelFor(recibido);
        efectivo.agregar(etiquetaRecibido);
        efectivo.agregar(recibido);
        efectivo.agregar(vuelto);

        FormularioPanel tarjeta = new FormularioPanel();
        tarjeta.agregar(cliente);
        JLabel etiquetaReferencia = new JLabel("Referencia de autorización ficticia");
        etiquetaReferencia.setLabelFor(referencia);
        tarjeta.agregar(etiquetaReferencia);
        tarjeta.agregar(referencia);
        tarjeta.agregar(new JLabel("Pago simulado: no se solicitan datos bancarios."));

        campos.add(efectivo.alineadoArriba(), "efectivo");
        campos.add(tarjeta.alineadoArriba(), "tarjeta");
        formulario.agregar(campos);
        formulario.agregar(error);

        add(formulario.alineadoArriba(), BorderLayout.CENTER);
        acciones.add(confirmar);
        acciones.add(volver);
        configurarTamano(660, 470);
    }

    public boolean esEfectivo() {
        return medio.getSelectedIndex() == 0;
    }

    public void mostrarMedioSeleccionado() {
        if (esEfectivo()) {
            tarjetas.show(campos, "efectivo");
        } else {
            tarjetas.show(campos, "tarjeta");
        }
    }
}
