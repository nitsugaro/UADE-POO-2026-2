package excepciones;

public final class LimitePrestamosException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public LimitePrestamosException() {
        super("El socio ya tiene dos préstamos activos. Debe devolver un libro antes de solicitar otro.");
    }
}
