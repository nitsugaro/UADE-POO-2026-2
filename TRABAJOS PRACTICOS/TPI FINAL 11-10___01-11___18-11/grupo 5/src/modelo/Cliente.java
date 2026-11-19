package modelo;

public final class Cliente {
    private final Long id;
    private String nombre;
    private String dni;
    private boolean activo;

    public boolean isActivo() { return activo; }

    public void cambiarActividad(boolean activo) { this.activo = activo; }

    public void actualizarDesde(Cliente actualizado) {
        if (!java.util.Objects.equals(id, actualizado.id)) {
            throw new IllegalArgumentException("Los datos corresponden a otro cliente");
        }
        nombre = actualizado.nombre;
        dni = actualizado.dni;
        activo = actualizado.activo;
    }

    public Cliente(Long id, String nombre, String dni) {
        this(id, nombre, dni, true);
    }

    public Cliente(Long id, String nombre, String dni, boolean activo) {
        this.activo = activo;
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
