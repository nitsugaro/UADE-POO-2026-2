package tp2.ejercicio2;

import java.util.ArrayList;

public class Vuelo {

    private String numero;
    private String fecha;

    private Aeropuerto origen;
    private Aeropuerto destino;

    private ArrayList<Aeropuerto> escalas;

    private Avion avion;
    private Tripulacion tripulacion;

    private ArrayList<Pasajero> pasajeros;

    public Vuelo(String numero,
                 String fecha,
                 Aeropuerto origen,
                 Aeropuerto destino,
                 Avion avion,
                 Tripulacion tripulacion) {

        this.numero = numero;
        this.fecha = fecha;
        this.origen = origen;
        this.destino = destino;
        this.avion = avion;
        this.tripulacion = tripulacion;

        this.escalas = new ArrayList<>();
        this.pasajeros = new ArrayList<>();
    }

    public void agregarEscala(Aeropuerto aeropuerto) {
        escalas.add(aeropuerto);
    }

    public void agregarPasajero(Pasajero pasajero) {

        if (pasajeros.size() < avion.getCapacidad()) {
            pasajeros.add(pasajero);
        } else {
            System.out.println("El avión no tiene más capacidad.");
        }
    }

    public String getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public Aeropuerto getOrigen() {
        return origen;
    }

    public Aeropuerto getDestino() {
        return destino;
    }

    public ArrayList<Aeropuerto> getEscalas() {
        return escalas;
    }

    public Avion getAvion() {
        return avion;
    }

    public Tripulacion getTripulacion() {
        return tripulacion;
    }

    public ArrayList<Pasajero> getPasajeros() {
        return pasajeros;
    }
}
