package tp2.ejercicio2;

import java.util.ArrayList;

public class Aerolinea {

    private String nombre;
    private ArrayList<Vuelo> vuelos;

    public Aerolinea(String nombre) {
        this.nombre = nombre;
        this.vuelos = new ArrayList<>();
    }

    public void agregarVuelo(Vuelo vuelo) {
        vuelos.add(vuelo);
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Vuelo> getVuelos() {
        return vuelos;
    }
}
