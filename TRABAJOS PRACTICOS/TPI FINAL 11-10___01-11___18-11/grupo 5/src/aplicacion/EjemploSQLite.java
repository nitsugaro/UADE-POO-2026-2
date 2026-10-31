package aplicacion;

import modelo.Cliente;
import persistencia.ClienteDAO;
import persistencia.ConexionSQLite;
import java.nio.file.Path;
import java.sql.SQLException;

/** Como el ejemplo de clase: conectar, crear tabla, insertar y consultar. */
public final class EjemploSQLite {
    public static void main(String[] args) {
        Path archivo = ConexionSQLite.rutaPredeterminada();
        try {
            ConexionSQLite.inicializar(archivo);
            ClienteDAO dao = new ClienteDAO(archivo);
            System.out.println("Base SQLite: " + archivo.toAbsolutePath().normalize());
            if (args.length == 2) {
                Cliente guardado = dao.guardar(new Cliente(null, args[0], args[1]));
                System.out.println("Cliente guardado con ID " + guardado.getId());
            } else if (args.length != 0) {
                throw new IllegalArgumentException("Uso: EjemploSQLite [nombre dni]");
            }
            System.out.println("Clientes guardados:");
            for (Cliente cliente : dao.listar()) {
                System.out.println(cliente.getId() + " | " + cliente.getNombre()
                        + " | " + cliente.getDni());
            }
        } catch (SQLException | IllegalArgumentException error) {
            System.err.println("No se pudo completar la operacion: " + error.getMessage());
            System.exit(1);
        }
    }
}
