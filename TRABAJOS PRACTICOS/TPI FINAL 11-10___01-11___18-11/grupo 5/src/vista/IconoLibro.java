package vista;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

final class IconoLibro {
    private IconoLibro() {
    }

    static BufferedImage crear(int tamano) {
        BufferedImage imagen = new BufferedImage(tamano, tamano, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dibujo = imagen.createGraphics();
        try {
            dibujo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            dibujo.scale(tamano / 32.0, tamano / 32.0);

            dibujo.setColor(new Color(0x234B4B));
            dibujo.fillRoundRect(4, 2, 24, 28, 6, 6);
            dibujo.setColor(new Color(0xF6EBD4));
            dibujo.fillRoundRect(8, 23, 17, 4, 2, 2);
            dibujo.setColor(new Color(0x467171));
            dibujo.fillRoundRect(8, 5, 2, 15, 2, 2);
            dibujo.setColor(new Color(0xF6EBD4));
            dibujo.fillRoundRect(13, 8, 11, 3, 2, 2);
            dibujo.fillRoundRect(13, 14, 8, 2, 2, 2);
        } finally {
            dibujo.dispose();
        }
        return imagen;
    }
}
