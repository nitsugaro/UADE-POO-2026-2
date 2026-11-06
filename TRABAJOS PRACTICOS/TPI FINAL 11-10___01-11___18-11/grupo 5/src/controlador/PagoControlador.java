package controlador;

import modelo.Pago;
import modelo.Venta;
import vista.PagoVista;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.time.LocalDateTime;

/** Coordina el formulario de pago; los importes los calcula el modelo. */
public final class PagoControlador {
    private final InicioControlador app;
    private final Venta venta;
    private final Runnable volver;
    private final PagoVista vista = new PagoVista();

    public PagoControlador(InicioControlador app, Venta venta, Runnable volver) {
        this.app = app;
        this.venta = venta;
        this.volver = volver;
    }

    public void mostrar() {
        vista.total.setText("$ " + venta.calcularTotal());

        if (venta.getCliente() == null) {
            vista.cliente.setText("Para pagar con tarjeta, agregá un cliente desde la venta.");
        } else {
            vista.cliente.setText("Cliente: " + venta.getCliente().getNombre());
        }

        vista.medio.addActionListener(evento -> cambiarMedio());
        vista.volver.addActionListener(evento -> volver.run());
        vista.confirmar.addActionListener(evento -> confirmar());
        vista.recibido.addActionListener(evento -> confirmar());
        vista.referencia.addActionListener(evento -> confirmar());
        vista.recibido.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent evento) {
                actualizarVuelto();
            }

            @Override
            public void removeUpdate(DocumentEvent evento) {
                actualizarVuelto();
            }

            @Override
            public void changedUpdate(DocumentEvent evento) {
                actualizarVuelto();
            }
        });

        cambiarMedio();
        app.ventana.mostrar(vista);
    }

    private void cambiarMedio() {
        vista.mostrarMedioSeleccionado();
        vista.error.setText(" ");
        boolean puedePagar = vista.esEfectivo() || venta.getCliente() != null;
        vista.confirmar.setEnabled(puedePagar);
    }

    private Pago crearPago() {
        if (vista.esEfectivo()) {
            return Pago.enEfectivo(null, LocalDateTime.now(), venta.calcularTotal(),
                    InicioControlador.dinero(vista.recibido.getText()));
        }

        if (venta.getCliente() == null) {
            throw new IllegalStateException("Volvé a la venta y agregá un cliente para pagar con tarjeta.");
        }

        return Pago.conTarjeta(null, LocalDateTime.now(), venta.calcularTotal(), vista.referencia.getText());
    }

    private void actualizarVuelto() {
        vista.error.setText(" ");
        if (vista.recibido.getText().isBlank()) {
            vista.vuelto.setText("Ingresá el monto recibido para consultar el vuelto.");
            return;
        }

        try {
            Pago pago = crearPago();
            vista.vuelto.setText("Vuelto: $ " + pago.calcularVuelto());
        } catch (IllegalArgumentException error) {
            vista.vuelto.setText(error.getMessage());
        }
    }

    private void confirmar() {
        vista.error.setText(" ");
        try {
            Pago pago = crearPago();
            app.ejecutar(() -> {
                app.servicio.confirmar(venta, pago);
                new HistorialVentasControlador(app).mostrarDetalle(venta);
            });
        } catch (IllegalArgumentException | IllegalStateException error) {
            vista.error.setText(error.getMessage());
        }
    }
}
