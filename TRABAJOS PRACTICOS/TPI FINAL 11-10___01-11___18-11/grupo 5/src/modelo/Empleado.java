package modelo;

public final class Empleado {
    private final Long id;
    private final String legajo;
    private final String nombre;
    private boolean activo;

    public Empleado(Long id, String legajo, String nombre, boolean activo) {
        this.id = Validaciones.id(id);
        this.legajo = Validaciones.texto(legajo, "Legajo");
        this.nombre = Validaciones.texto(nombre, "Nombre");
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public String getLegajo() {
        return legajo;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void activar() {
        activo = true;
    }

    public void desactivar() {
        activo = false;
    }
}
