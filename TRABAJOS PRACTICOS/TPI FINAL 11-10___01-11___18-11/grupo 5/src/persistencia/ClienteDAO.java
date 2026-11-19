package persistencia;

import modelo.Cliente;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Traduce entre filas SQL y objetos Cliente, sin cambiar el modelo. */
public final class ClienteDAO {
    private final Path archivo;

    public ClienteDAO(Path archivo) {
        this.archivo = Objects.requireNonNull(archivo);
    }

    public Cliente guardar(Cliente cliente) throws SQLException {
        Objects.requireNonNull(cliente);
        if (cliente.getId() != null) {
            throw new IllegalArgumentException("Guardar requiere un cliente nuevo con ID null");
        }
        ConexionSQLite.inicializar(archivo);
        return ConexionSQLite.transaccion(archivo, conexion -> {
            bloquearAltas(conexion);
            return insertarNuevo(conexion, cliente);
        });
    }

    static void bloquearAltas(Connection conexion) throws SQLException {
        try (Statement sentencia = conexion.createStatement()) {
            sentencia.executeUpdate("UPDATE clientes SET id = id WHERE 0");
        }
    }

    static List<Cliente> buscarPorDni(Connection conexion, String dni) throws SQLException {
        List<Cliente> encontrados = new ArrayList<>();
        try (PreparedStatement consulta = conexion.prepareStatement("SELECT id,nombre,dni,activo FROM clientes WHERE dni = ? ORDER BY id")) {
            consulta.setString(1, dni);
            try (ResultSet filas = consulta.executeQuery()) {
                while (filas.next()) encontrados.add(leer(filas));
            }
        }
        return encontrados;
    }

    static Cliente insertarNuevo(Connection conexion, Cliente cliente) throws SQLException {
        List<Cliente> existentes = buscarPorDni(conexion, cliente.getDni());
        if (!existentes.isEmpty()) {
            if (existentes.stream().anyMatch(Cliente::isActivo)) throw new IllegalArgumentException(
                    "Ese DNI ya está registrado. Buscá el cliente existente.");
            throw new IllegalArgumentException("Ese DNI pertenece a un cliente inactivo. Reactivalo desde Administrar clientes.");
        }
        try (PreparedStatement sentencia = conexion.prepareStatement("INSERT INTO clientes (nombre,dni,activo) VALUES (?,?,1) RETURNING id")) {
            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getDni());
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) throw new SQLException("No se obtuvo el ID generado");
                return new Cliente(fila.getLong(1), cliente.getNombre(), cliente.getDni());
            }
        }
    }
    public Optional<Cliente> buscarPorId(long id) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT id, nombre, dni, activo FROM clientes WHERE id = ?")) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(leer(resultado)) : Optional.empty();
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo)) {
            return listar(conexion);
        }
    }

    static List<Cliente> listar(Connection conexion) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT id, nombre, dni, activo FROM clientes ORDER BY id");
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                clientes.add(leer(resultado));
            }
        }
        return clientes;
    }

    public void cambiarActividad(long id, boolean activo) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                PreparedStatement sentencia = conexion.prepareStatement("UPDATE clientes SET activo = ? WHERE id = ?")) {
            sentencia.setBoolean(1, activo);
            sentencia.setLong(2, id);
            if (sentencia.executeUpdate() != 1) throw new IllegalStateException("El cliente ya no existe");
        }
    }

    public Cliente modificar(Cliente anterior, String nombre, String dni) throws SQLException {
        Objects.requireNonNull(anterior);
        if (anterior.getId() == null) throw new IllegalArgumentException("El cliente no está guardado");
        Cliente propuesta = new Cliente(anterior.getId(), nombre, dni);
        return ConexionSQLite.transaccion(archivo, conexion -> {
            // Serializa la lectura del DNI y su actualización entre escritores.
            try (Statement bloqueo = conexion.createStatement()) {
                bloqueo.executeUpdate("UPDATE clientes SET id = id WHERE 0");
            }
            Cliente actual;
            try (PreparedStatement consulta = conexion.prepareStatement("SELECT id,nombre,dni,activo FROM clientes WHERE id = ?")) {
                consulta.setLong(1, anterior.getId());
                try (ResultSet fila = consulta.executeQuery()) {
                    if (!fila.next()) throw new IllegalStateException("El cliente ya no existe");
                    actual = leer(fila);
                }
            }
            if (!actual.getNombre().equals(anterior.getNombre()) || !actual.getDni().equals(anterior.getDni())) {
                throw new IllegalStateException("Los datos del cliente cambiaron. Volvé a abrir la aplicación y reintentá.");
            }
            // Duplicados históricos: mantener el DNI permite corregir solo el nombre.
            if (!actual.getDni().equals(propuesta.getDni())) {
                try (PreparedStatement consulta = conexion.prepareStatement("SELECT 1 FROM clientes WHERE dni = ? AND id <> ? LIMIT 1")) {
                    consulta.setString(1, propuesta.getDni());
                    consulta.setLong(2, actual.getId());
                    try (ResultSet fila = consulta.executeQuery()) {
                        if (fila.next()) throw new IllegalArgumentException("Ese DNI ya pertenece a otro cliente, activo o inactivo.");
                    }
                }
            }
            try (PreparedStatement sentencia = conexion.prepareStatement("UPDATE clientes SET nombre = ?, dni = ? WHERE id = ?")) {
                sentencia.setString(1, propuesta.getNombre());
                sentencia.setString(2, propuesta.getDni());
                sentencia.setLong(3, actual.getId());
                sentencia.executeUpdate();
            }
            return new Cliente(actual.getId(), propuesta.getNombre(), propuesta.getDni(), actual.isActivo());
        });
    }
    private static Cliente leer(ResultSet resultado) throws SQLException {
        return new Cliente(resultado.getLong("id"), resultado.getString("nombre"),
                resultado.getString("dni"), resultado.getBoolean("activo"));
    }
}
