package pruebas;

import modelo.Cliente;
import persistencia.ClienteDAO;
import persistencia.ConexionSQLite;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public final class PruebasSQLite {
    public static void main(String[] args) throws Exception {
        Path directorio = Files.createTempDirectory("tpo-sqlite-");
        Path archivo = directorio.resolve("datos/prueba.db");
        try {
            ConexionSQLite.inicializar(archivo);
            ClienteDAO dao = new ClienteDAO(archivo);
            exigir(dao.listar().isEmpty(), "Base inicialmente vacia");
            Cliente original = new Cliente(null, "O'Connor; DROP TABLE clientes; --", "00123456");
            Cliente guardado = dao.guardar(original);
            exigir(guardado.getId() != null && guardado.getId() > 0, "ID generado");
            exigir(original.getId() == null, "Objeto original inmutable");
            // Inicializar y abrir otro DAO debe conservar lo escrito anteriormente.
            ConexionSQLite.inicializar(archivo);
            ClienteDAO reabierto = new ClienteDAO(archivo);
            Cliente recuperado = reabierto.buscarPorId(guardado.getId()).orElseThrow();
            exigir(recuperado.getNombre().equals(original.getNombre()), "Texto parametrizado");
            exigir(recuperado.getDni().equals("00123456"), "DNI conserva ceros");
            exigir(reabierto.listar().size() == 1, "Persistencia al reabrir");
            exigir(reabierto.buscarPorId(999999).isEmpty(), "ID inexistente");
            try {
                dao.guardar(guardado);
                throw new AssertionError("Debia rechazar cliente ya persistido");
            } catch (IllegalArgumentException esperado) {
                exigir(dao.listar().size() == 1, "Rechazo sin insercion duplicada");
            }
            try (Connection conexion = ConexionSQLite.abrir(archivo);
                 Statement sentencia = conexion.createStatement();
                 ResultSet resultado = sentencia.executeQuery("PRAGMA foreign_keys")) {
                exigir(resultado.next() && resultado.getInt(1) == 1, "Claves foraneas activas");
            }
            System.out.println("OK: 9 verificaciones de SQLite");
        } finally {
            Files.deleteIfExists(archivo);
            Files.deleteIfExists(archivo.getParent());
            Files.deleteIfExists(directorio);
        }
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
