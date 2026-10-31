package persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre una conexion nueva por operacion; quien la recibe debe cerrarla. */
public final class ConexionSQLite {
    private ConexionSQLite() { }

    public static Path rutaPredeterminada() {
        return Path.of(System.getProperty("tpo.db", "datos/libreria.db"));
    }

    public static Connection abrir(Path archivo) throws SQLException {
        Path ruta = archivo.toAbsolutePath().normalize();
        try {
            Files.createDirectories(ruta.getParent());
        } catch (IOException error) {
            throw new SQLException("No se pudo crear el directorio de la base", error);
        }
        Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + ruta);
        try {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.execute("PRAGMA foreign_keys = ON");
                sentencia.execute("PRAGMA busy_timeout = 5000");
            }
            return conexion;
        } catch (SQLException error) {
            try {
                conexion.close();
            } catch (SQLException cierre) {
                error.addSuppressed(cierre);
            }
            throw error;
        }
    }

    /** Crea las tablas iniciales sin borrar datos existentes. */
    public static void inicializar(Path archivo) throws SQLException {
        try (Connection conexion = abrir(archivo);
             Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("""
                CREATE TABLE IF NOT EXISTS clientes (
                    id INTEGER PRIMARY KEY,
                    nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
                    dni TEXT NOT NULL CHECK (length(dni) > 0 AND dni NOT GLOB '*[^0-9]*')
                )
                """);
        }
    }
}
