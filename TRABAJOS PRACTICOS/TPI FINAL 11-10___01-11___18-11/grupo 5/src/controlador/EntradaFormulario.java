package controlador;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.swing.JTable;

final class EntradaFormulario {
    private static final Pattern DIACRITICOS = Pattern.compile("\\p{M}+");

    private EntradaFormulario() {
    }

    static BigDecimal dinero(String texto) {
        try {
            return new BigDecimal(texto.trim().replace(',', '.'));
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("Ingresá un importe numérico válido");
        }
    }

    static String normalizarBusqueda(String texto) {
        return DIACRITICOS.matcher(Normalizer.normalize(texto.trim(), Normalizer.Form.NFD))
                .replaceAll("").toLowerCase(Locale.ROOT);
    }

    static int seleccionado(JTable tabla) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Seleccioná una fila");
        }
        return tabla.convertRowIndexToModel(fila);
    }
}
