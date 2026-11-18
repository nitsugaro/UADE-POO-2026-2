package vista;

import javax.swing.*;
import java.awt.*;

public final class InicioVista extends PanelVista {
    public final JButton venta = new JButton("Nueva venta");
    public final JButton historial = new JButton("Historial de ventas");
    public final JButton catalogo = new JButton("Administrar libros");
    public final JButton categorias = new JButton("Administrar categorías");
    public final JButton editoriales = new JButton("Administrar editoriales");
    public final JButton socio = new JButton("Hacerse socio");
    public final JButton prestamos = new JButton("Préstamos y devoluciones");

    public InicioVista() {
        super("Librería · Inicio", new ImageIcon(IconoLibro.crear(32)));

        JPanel accesos = new JPanel(new GridLayout(0, 1, 0, 12));
        accesos.add(seccion("Ventas", barra(venta, historial)));
        accesos.add(seccion("Catálogo", barra(catalogo, categorias)));
        accesos.add(barra(editoriales));
        accesos.add(seccion("Socios y préstamos", barra(socio, prestamos)));
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.add(accesos, BorderLayout.NORTH);
        remove(acciones);
        add(contenido, BorderLayout.CENTER);
        configurarTamano(580, 370);
    }
}
