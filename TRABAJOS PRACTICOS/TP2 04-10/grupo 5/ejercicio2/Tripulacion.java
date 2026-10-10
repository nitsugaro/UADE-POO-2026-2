package tp2.ejercicio2;

import java.util.ArrayList;

public class Tripulacion {

    private ArrayList<Piloto> pilotos;
    private OperadorComunicaciones operador;
    private ArrayList<ComisarioAbordo> comisarios;
    private ArrayList<Azafata> azafatas;

    public Tripulacion(OperadorComunicaciones operador) {
        this.pilotos = new ArrayList<>();
        this.comisarios = new ArrayList<>();
        this.azafatas = new ArrayList<>();
        this.operador = operador;
    }

    public void agregarPiloto(Piloto piloto) {
        if (pilotos.size() < 2) {
            pilotos.add(piloto);
        } else {
            System.out.println("La tripulación ya tiene 2 pilotos.");
        }
    }

    public void agregarComisario(ComisarioAbordo comisario) {
        if (comisarios.size() < 2) {
            comisarios.add(comisario);
        } else {
            System.out.println("La tripulación ya tiene 2 comisarios.");
        }
    }

    public void agregarAzafata(Azafata azafata) {
        if (azafatas.size() < 4) {
            azafatas.add(azafata);
        } else {
            System.out.println("La tripulación ya tiene 4 azafatas.");
        }
    }

    public ArrayList<Piloto> getPilotos() {
        return pilotos;
    }

    public OperadorComunicaciones getOperador() {
        return operador;
    }

    public ArrayList<ComisarioAbordo> getComisarios() {
        return comisarios;
    }

    public ArrayList<Azafata> getAzafatas() {
        return azafatas;
    }
}