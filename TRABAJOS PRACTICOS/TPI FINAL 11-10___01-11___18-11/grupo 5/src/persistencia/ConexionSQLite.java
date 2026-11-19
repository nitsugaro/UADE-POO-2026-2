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
    private ConexionSQLite() {
    }

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

    @FunctionalInterface
    interface Operacion<T> {
        T ejecutar(Connection conexion) throws SQLException;
    }

    static <T> T transaccion(Path archivo, Operacion<T> operacion) throws SQLException {
        try (Connection conexion = abrir(archivo)) {
            conexion.setAutoCommit(false);
            try {
                T resultado = operacion.ejecutar(conexion);
                conexion.commit();
                return resultado;
            } catch (SQLException | RuntimeException error) {
                try {
                    conexion.rollback();
                } catch (SQLException rollback) {
                    error.addSuppressed(rollback);
                }
                throw error;
            }
        }
    }

    /** Crea las tablas faltantes y conserva los datos de instalaciones anteriores. */
    public static void inicializar(Path archivo) throws SQLException {
        transaccion(archivo, conexion -> {
            try (Statement sentencia = conexion.createStatement()) {
                for (String sql : ESQUEMA) {
                    sentencia.executeUpdate(sql);
                }
                boolean tieneActivo = false;
                try (java.sql.ResultSet columnas = sentencia.executeQuery("PRAGMA table_info(clientes)")) {
                    while (columnas.next()) {
                        if (columnas.getString("name").equals("activo")) tieneActivo = true;
                    }
                }
                if (!tieneActivo) sentencia.executeUpdate(
                        "ALTER TABLE clientes ADD COLUMN activo INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0,1))");
            }
            return null;
        });
    }

    private static final String[] ESQUEMA = {
        """
        CREATE TABLE IF NOT EXISTS clientes (
            id INTEGER PRIMARY KEY,
            nombre TEXT NOT NULL CHECK (length(trim(nombre)) > 0),
            dni TEXT NOT NULL CHECK (length(dni) > 0 AND dni NOT GLOB '*[^0-9]*')
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS empleados (
            id INTEGER PRIMARY KEY, legajo TEXT NOT NULL UNIQUE,
            nombre TEXT NOT NULL, activo INTEGER NOT NULL CHECK (activo IN (0, 1))
        )
        """,
        "CREATE TABLE IF NOT EXISTS autores (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL)",
        "CREATE TABLE IF NOT EXISTS editoriales (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL)",
        "CREATE TABLE IF NOT EXISTS categorias (id INTEGER PRIMARY KEY, nombre TEXT NOT NULL)",
        """
        CREATE TABLE IF NOT EXISTS libros (
            id INTEGER PRIMARY KEY, isbn TEXT NOT NULL UNIQUE, titulo TEXT NOT NULL,
            editorial_id INTEGER NOT NULL REFERENCES editoriales(id),
            categoria_id INTEGER NOT NULL REFERENCES categorias(id),
            precio TEXT NOT NULL, stock INTEGER NOT NULL CHECK (stock BETWEEN 0 AND 2147483647),
            activo INTEGER NOT NULL CHECK (activo IN (0, 1))
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS libro_autor (
            libro_id INTEGER NOT NULL REFERENCES libros(id),
            autor_id INTEGER NOT NULL REFERENCES autores(id),
            PRIMARY KEY (libro_id, autor_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS socios (
            cliente_id INTEGER PRIMARY KEY REFERENCES clientes(id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS prestamos (
            id INTEGER PRIMARY KEY,
            socio_id INTEGER NOT NULL REFERENCES socios(cliente_id),
            libro_id INTEGER NOT NULL REFERENCES libros(id),
            fecha TEXT NOT NULL,
            devuelto_en TEXT
        )
        """,
        "CREATE TABLE IF NOT EXISTS inicializaciones (nombre TEXT PRIMARY KEY)",
        """
        CREATE TABLE IF NOT EXISTS ventas (
            id INTEGER PRIMARY KEY, fecha TEXT NOT NULL,
            empleado_id INTEGER NOT NULL REFERENCES empleados(id),
            cliente_id INTEGER REFERENCES clientes(id),
            estado TEXT NOT NULL CHECK (estado IN ('CONFIRMADA', 'CANCELADA', 'DEVUELTA')),
            descuento_codigo TEXT, descuento_porcentaje TEXT
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS detalles_venta (
            venta_id INTEGER NOT NULL REFERENCES ventas(id),
            libro_id INTEGER NOT NULL REFERENCES libros(id),
            cantidad INTEGER NOT NULL CHECK (cantidad > 0),
            precio_unitario TEXT NOT NULL, titulo TEXT NOT NULL, isbn TEXT NOT NULL,
            PRIMARY KEY (venta_id, libro_id)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS pagos (
            venta_id INTEGER PRIMARY KEY REFERENCES ventas(id), id INTEGER,
            fecha TEXT NOT NULL, medio TEXT NOT NULL CHECK (medio IN ('EFECTIVO', 'TARJETA')),
            importe TEXT NOT NULL, recibido TEXT, referencia TEXT
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS devoluciones (
            id INTEGER PRIMARY KEY, venta_id INTEGER NOT NULL UNIQUE REFERENCES ventas(id),
            fecha TEXT NOT NULL, empleado_id INTEGER NOT NULL REFERENCES empleados(id),
            motivo TEXT NOT NULL
        )
        """
    };
}
