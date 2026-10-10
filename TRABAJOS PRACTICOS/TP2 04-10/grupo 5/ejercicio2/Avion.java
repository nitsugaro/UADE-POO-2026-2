package tp2.ejercicio2;

public class Avion {

    private String matricula;
    private String modelo;
    private int capacidad;

    public Avion(String matricula, String modelo, int capacidad) {
        this.matricula = matricula;
        this.modelo = modelo;
        this.capacidad = capacidad;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getModelo() {
        return modelo;
    }

    public int getCapacidad() {
        return capacidad;
    }
}
