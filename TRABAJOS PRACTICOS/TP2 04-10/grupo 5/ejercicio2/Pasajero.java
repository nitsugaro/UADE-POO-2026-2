package tp2.ejercicio2;

public class Pasajero {

    private String dni;
    private String nombre;
    private String apellido;
    private Asiento asiento;

    public Pasajero(String dni, String nombre, String apellido, Asiento asiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.asiento = asiento;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public Asiento getAsiento() {
        return asiento;
    }
}
