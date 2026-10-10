package tp2.ejercicio2;

public abstract class Tripulante {

    private String dni;
    private String nombre;
    private String apellido;

    public Tripulante(String dni, String nombre, String apellido) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
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
}
