package tp2.ejercicio2;

public class Aeropuerto {

    private String codigo;
    private String nombre;
    private Ciudad ciudad;

    public Aeropuerto(String codigo, String nombre, Ciudad ciudad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }
}
