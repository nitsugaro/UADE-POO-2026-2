package excepciones;

public class DevolucionNoPermitidaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DevolucionNoPermitidaException(String mensaje) { super(mensaje); }
}
