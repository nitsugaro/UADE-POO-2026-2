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
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                PreparedStatement sentencia = conexion.prepareStatement(
                "INSERT INTO clientes (nombre, dni) VALUES (?, ?)")) {
            sentencia.setString(1, cliente.getNombre());
            sentencia.setString(2, cliente.getDni());
            sentencia.executeUpdate();
            // El ID se consulta en la misma conexion que hizo el INSERT.
            try (Statement consulta = conexion.createStatement();
                    ResultSet resultado = consulta.executeQuery("SELECT last_insert_rowid()")) {
                if (!resultado.next()) {
                    throw new SQLException("No se obtuvo el ID generado");
                }
                return new Cliente(resultado.getLong(1), cliente.getNombre(), cliente.getDni());
            }
        }
    }

    public Optional<Cliente> buscarPorId(long id) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT id, nombre, dni FROM clientes WHERE id = ?")) {
            sentencia.setLong(1, id);
            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next() ? Optional.of(leer(resultado)) : Optional.empty();
            }
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                PreparedStatement sentencia = conexion.prepareStatement(
                "SELECT id, nombre, dni FROM clientes ORDER BY id");
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                clientes.add(leer(resultado));
            }
        }
        return clientes;
    }

    private Cliente leer(ResultSet resultado) throws SQLException {
        return new Cliente(resultado.getLong("id"), resultado.getString("nombre"),
                resultado.getString("dni"));
    }
}
