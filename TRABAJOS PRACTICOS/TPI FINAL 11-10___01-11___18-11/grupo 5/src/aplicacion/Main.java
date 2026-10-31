package aplicacion;

import modelo.Cliente;
import persistencia.ClienteDAO;
import persistencia.ConexionSQLite;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

/** Ejecutar sin argumentos para insertar y consultar clientes de prueba. */
public final class Main {
    public static void main(String[] args) {
        Path archivo = ConexionSQLite.rutaPredeterminada();
        System.out.println("Base de datos: " + archivo.toAbsolutePath().normalize());

        try {
            ConexionSQLite.inicializar(archivo);
            ClienteDAO dao = new ClienteDAO(archivo);

            List<Cliente> ejemplos = List.of(
                    new Cliente(null, "Ana Prueba", "90000001"),
                    new Cliente(null, "Bruno Prueba", "90000002"),
                    new Cliente(null, "Carla Prueba", "90000003")
            );

            // Cada ejecucion agrega tres registros nuevos, sin borrar los anteriores.
            for (Cliente ejemplo : ejemplos) {
                Cliente guardado = dao.guardar(ejemplo);
                // buscarPorId abre otra conexion: comprobamos lo escrito en el archivo.
                Cliente recuperado = dao.buscarPorId(guardado.getId())
                        .orElseThrow(() -> new SQLException("No se recupero el cliente guardado"));
                if (!recuperado.getNombre().equals(ejemplo.getNombre())
                        || !recuperado.getDni().equals(ejemplo.getDni())) {
                    throw new SQLException("Los datos recuperados no coinciden con los insertados");
                }
                System.out.println("Insertado y verificado: " + recuperado.getId()
                        + " | " + recuperado.getNombre() + " | " + recuperado.getDni());
            }

            System.out.println("\nTodos los clientes de la base:");
            for (Cliente cliente : dao.listar()) {
                System.out.println(cliente.getId() + " | " + cliente.getNombre()
                        + " | " + cliente.getDni());
            }
            System.out.println("\nOK: conexion, insercion y consulta de los 3 clientes verificadas.");
        } catch (SQLException error) {
            System.err.println("ERROR al verificar SQLite: " + error.getMessage());
            System.exit(1);
        }
    }
}
