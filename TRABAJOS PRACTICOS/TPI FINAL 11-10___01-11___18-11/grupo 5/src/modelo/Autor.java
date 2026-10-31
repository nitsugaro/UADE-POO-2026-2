package modelo;

public final class Autor {
    private final Long id;
    private final String nombre;

    /** id null representa un objeto aun no persistido; un id asignado es inmutable. */
    public Autor(Long id, String nombre) {
        this.id = Validaciones.id(id);
        this.nombre = Validaciones.texto(nombre, "Nombre");
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
}
