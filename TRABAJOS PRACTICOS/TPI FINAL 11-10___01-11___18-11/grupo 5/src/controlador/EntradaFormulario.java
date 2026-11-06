package controlador;

import java.math.BigDecimal;
import javax.swing.JTable;

final class EntradaFormulario {
    private EntradaFormulario() {
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
