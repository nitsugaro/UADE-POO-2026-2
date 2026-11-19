package vista;

import javax.swing.*;
import java.awt.*;

public final class LibroFormulario extends JPanel {
    public final JTextField isbn = new JTextField(28);
    public final JTextField titulo = new JTextField(28);
    public final JComboBox<String> autores = comboEditable();
    public final JComboBox<String> editorial = comboEditable();
    public final JComboBox<String> categoria = comboEditable();
    public final JTextField precio = new JTextField(12);
    public final JSpinner stock = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
    public final JCheckBox activo = new JCheckBox("Activo", true);

    public LibroFormulario() {
        super(new BorderLayout());
        FormularioPanel campos = new FormularioPanel();
        campos.agregarCampo("ISBN", isbn);
        campos.agregarCampo("Título", titulo);
        campos.agregarCampo("Autores (separados por punto y coma)", autores);
        campos.agregarCampo("Editorial", editorial);
        campos.agregarCampo("Categoría", categoria);
        campos.agregar(PanelVista.barra(conEtiqueta("Precio ($)", precio),
                conEtiqueta("Stock disponible", stock), activo));
        add(campos, BorderLayout.CENTER);
    }

    private static JComboBox<String> comboEditable() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setEditable(true);
        combo.setPrototypeDisplayValue("Una opción de aproximadamente 28 letras");
        combo.setMaximumRowCount(10);
        return combo;
    }

    private static JPanel conEtiqueta(String texto, JComponent campo) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setLabelFor(campo);
        return PanelVista.barra(etiqueta, campo);
    }
}
