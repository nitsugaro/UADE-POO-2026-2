package modelo;

public final class Cliente {
    private final Long id;
    private final String nombre;
    private final String dni;

    public Cliente(Long id, String nombre, String dni) {
        this.id = Validaciones.id(id);
        this.nombre = Validaciones.texto(nombre, "Nombre");
        this.dni = Validaciones.texto(dni, "DNI");
        if (!this.dni.matches("[0-9]+")) {
            throw new IllegalArgumentException("El DNI debe contener solo digitos");
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }
}
