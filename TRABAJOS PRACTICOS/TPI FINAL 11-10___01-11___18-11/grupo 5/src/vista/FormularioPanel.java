package vista;

import javax.swing.*;
import java.awt.*;

/** Filas con altura natural: el espacio extra queda debajo del formulario. */
public final class FormularioPanel extends JPanel {
    private int fila;

    public FormularioPanel() {
        super(new GridBagLayout());
    }

    public void agregarCampo(String texto, JComponent campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setLabelFor(campo);
        agregar(etiqueta);
        agregar(campo);
    }

    public void agregar(JComponent componente) {
        GridBagConstraints posicion = new GridBagConstraints();
        posicion.gridx = 0;
        posicion.gridy = fila++;
        posicion.weightx = 1;
        posicion.fill = GridBagConstraints.HORIZONTAL;
        posicion.anchor = GridBagConstraints.NORTHWEST;
        posicion.insets = new Insets(0, 0, 8, 0);
        add(componente, posicion);
    }

    public JPanel alineadoArriba() {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(this, BorderLayout.NORTH);
        return contenedor;
    }
}
