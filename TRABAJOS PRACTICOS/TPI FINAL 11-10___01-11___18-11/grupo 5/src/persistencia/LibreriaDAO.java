package persistencia;

import modelo.*;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public final class LibreriaDAO {
    private final Path archivo;

    public LibreriaDAO(Path archivo) {
        this.archivo = archivo;
    }

    public void cargarEjemplos() throws SQLException {
        ConexionSQLite.transaccion(archivo, conexion -> {
            ejecutar(conexion, """
                    UPDATE empleados SET nombre = 'Mariana Acosta'
                    WHERE legajo = 'E001' AND nombre = 'Empleado de ejemplo'
                    """);
            ejecutar(conexion, """
                    INSERT INTO empleados (legajo, nombre, activo)
                    SELECT 'E001', 'Mariana Acosta', 1 WHERE NOT EXISTS (SELECT 1 FROM empleados)
                    """);
            cargarClientesEjemplo(conexion);

            if (ejecutar(conexion, "INSERT OR IGNORE INTO inicializaciones VALUES ('catalogo')") == 0) {
                return null;
            }
            ejecutar(conexion, "UPDATE autores SET nombre = 'Nicolás Aguirre' WHERE nombre = 'Autor de ejemplo'");
            ejecutar(conexion, "UPDATE editoriales SET nombre = 'Editorial Horizonte' WHERE nombre = 'Editorial de ejemplo'");
            ejecutar(conexion, "UPDATE editoriales SET nombre = 'Ediciones del Sur' WHERE nombre = 'Ediciones Demo'");
            agregarLibroEjemplo(conexion, 1, "Introducción a Java", "Nicolás Aguirre",
                    "Editorial Horizonte", "Programación", "25000.00", 10);
            agregarLibroEjemplo(conexion, 2, "Programación orientada a objetos", "Nicolás Aguirre",
                    "Editorial Horizonte", "Programación", "32000.00", 5);
            agregarLibroEjemplo(conexion, 3, "SQLite desde cero", "Lucía Torres",
                    "Editorial Horizonte", "Bases de datos", "18500.00", 8);
            agregarLibroEjemplo(conexion, 4, "Interfaces con Java Swing", "Mateo Luna",
                    "Editorial Horizonte", "Programación", "27800.00", 6);
            agregarLibroEjemplo(conexion, 5, "El faro de papel", "Sofía Vidal",
                    "Ediciones del Sur", "Narrativa", "14200.00", 12);
            agregarLibroEjemplo(conexion, 6, "Atlas de cielos nocturnos", "Mateo Luna",
                    "Ediciones del Sur", "Ciencia", "29900.00", 0);
            for (CatalogoEjemplo libro : CatalogoEjemplo.LIBROS) {
                agregarLibroEjemplo(conexion, libro.numero(), libro.titulo(), libro.autor(),
                        "Ediciones del Sur", libro.categoria(), libro.precio(), libro.stock());
            }
            return null;
        });
    }

    private record ClienteEjemplo(String nombre, String dni, String nombreAnterior, String dniAnterior) {}

    private static void cargarClientesEjemplo(Connection conexion) throws SQLException {
        List<ClienteEjemplo> clientes = List.of(
                new ClienteEjemplo("Ana Belén Rojas", "32714682", "Ana Demo", "00000001"),
                new ClienteEjemplo("Bruno Medina", "38420517", "Bruno Demo", "00000002"),
                new ClienteEjemplo("Carla Benítez", "35681429", "Carla Demo", "00000003"),
                new ClienteEjemplo("Diego Cabrera", "30196384", "Diego Demo", "00000004"),
                new ClienteEjemplo("Elena Márquez", "27945216", "Elena Demo", "00000005"),
                new ClienteEjemplo("Facundo Molina", "40813752", "Facundo Demo", "00000006"),
                new ClienteEjemplo("Gabriela Pereyra", "33462871", "Gabriela Demo", "00000007"),
                new ClienteEjemplo("Hernán Navarro", "29271546", "Hernán Demo", "00000008"),
                new ClienteEjemplo("Inés Duarte", "41759283", "Inés Demo", "00000009"),
                new ClienteEjemplo("Julián Ferreyra", "39136458", "Julián Demo", "00000010"),
                new ClienteEjemplo("Lucía Romano", "42387165", "Lucía Demo", "00000011"),
                new ClienteEjemplo("Martín Godoy", "31578246", "Martín Demo", "00000012"),
                new ClienteEjemplo("Natalia Quiroga", "36792158", "Natalia Demo", "00000013")
        );
        for (ClienteEjemplo cliente : clientes) {
            actualizarClienteEjemplo(conexion, cliente);
            ejecutar(conexion, """
                    INSERT INTO clientes (nombre, dni)
                    SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM clientes WHERE dni = ?)
                    """, cliente.nombre(), cliente.dni(), cliente.dni());
        }

        // Registros que creaba el antiguo ejemplo de consola.
        actualizarClienteEjemplo(conexion, new ClienteEjemplo("Ana Paula Sosa", "34825916", "Ana Prueba", "90000001"));
        actualizarClienteEjemplo(conexion, new ClienteEjemplo("Bruno Leiva", "37364281", "Bruno Prueba", "90000002"));
        actualizarClienteEjemplo(conexion, new ClienteEjemplo("Carla Suárez", "36281749", "Carla Prueba", "90000003"));
    }

    private static void actualizarClienteEjemplo(Connection conexion, ClienteEjemplo cliente) throws SQLException {
        ejecutar(conexion, """
                UPDATE clientes SET nombre = ?, dni = ?
                WHERE nombre = ? AND dni = ?
                  AND NOT EXISTS (SELECT 1 FROM clientes WHERE dni = ?)
                """, cliente.nombre(), cliente.dni(), cliente.nombreAnterior(), cliente.dniAnterior(), cliente.dni());
    }

    private static void agregarLibroEjemplo(Connection conexion, int numero, String titulo, String autor,
            String editorial, String categoria, String precio, int stock) throws SQLException {
        String isbn = isbnEjemplo(numero);
        String anterior = String.format(Locale.ROOT, "DEMO-%03d", numero);
        int migrados = ejecutar(conexion, """
                UPDATE libros SET isbn = ? WHERE isbn = ? AND titulo = ?
                  AND NOT EXISTS (SELECT 1 FROM libros WHERE isbn = ?)
                """, isbn, anterior, titulo, isbn);
        if (migrados > 0) {
            ejecutar(conexion, """
                    UPDATE detalles_venta SET isbn = ?
                    WHERE isbn = ? AND libro_id = (SELECT id FROM libros WHERE isbn = ?)
                    """, isbn, anterior, isbn);
            return;
        }
        try (PreparedStatement consulta = conexion.prepareStatement("SELECT 1 FROM libros WHERE isbn = ?")) {
            consulta.setString(1, isbn);
            try (ResultSet filas = consulta.executeQuery()) {
                if (filas.next()) {
                    return;
                }
            }
        }
        long autorId = buscarOCrear(conexion, "autores", autor);
        long editorialId = buscarOCrear(conexion, "editoriales", editorial);
        long categoriaId = buscarOCrear(conexion, "categorias", categoria);
        long libroId = insertar(conexion, """
                INSERT INTO libros (isbn, titulo, editorial_id, categoria_id, precio, stock, activo)
                VALUES (?, ?, ?, ?, ?, ?, 1) RETURNING id
                """, isbn, titulo, editorialId, categoriaId, precio, stock);
        ejecutar(conexion, "INSERT INTO libro_autor (libro_id, autor_id) VALUES (?, ?)", libroId, autorId);
    }

    private static String isbnEjemplo(int numero) {
        String base = String.format(Locale.ROOT, "978987819%03d", numero);
        int suma = 0;
        for (int i = 0; i < base.length(); i++) {
            suma += (base.charAt(i) - '0') * (i % 2 == 0 ? 1 : 3);
        }
        return base + (10 - suma % 10) % 10;
    }

    // La tabla se elige entre los nombres fijos del esquema. El nombre se parametriza.
    private static long buscarOCrear(Connection conexion, String tabla, String nombre) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement(
                "SELECT id FROM " + tabla + " WHERE nombre = ? ORDER BY id LIMIT 1")) {
            consulta.setString(1, nombre);
            try (ResultSet filas = consulta.executeQuery()) {
                if (filas.next()) {
                    return filas.getLong(1);
                }
            }
        }
        return insertar(conexion, "INSERT INTO " + tabla + " (nombre) VALUES (?) RETURNING id", nombre);
    }

    public List<Categoria> listarCategorias() throws SQLException {
        List<Categoria> categorias = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("SELECT id, nombre FROM categorias ORDER BY nombre, id")) {
            while (filas.next()) {
                categorias.add(new Categoria(filas.getLong("id"), filas.getString("nombre")));
            }
        }
        return categorias;
    }

    public Categoria guardarCategoria(Categoria anterior, String nombre) throws SQLException {
        Categoria nueva = new Categoria(anterior == null ? null : anterior.getId(), nombre);
        return ConexionSQLite.transaccion(archivo, conexion -> {
            // Mismo bloqueo y comparación exacta de nombres que guardarLibro/buscarOCrear.
            ejecutar(conexion, "UPDATE categorias SET id = id WHERE 0");
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id FROM categorias WHERE nombre = ? AND (? IS NULL OR id <> ?)")) {
                consulta.setString(1, nueva.getNombre());
                consulta.setObject(2, nueva.getId());
                consulta.setObject(3, nueva.getId());
                try (ResultSet filas = consulta.executeQuery()) {
                    if (filas.next()) throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
                }
            }
            long id;
            if (anterior == null) {
                id = insertar(conexion, "INSERT INTO categorias (nombre) VALUES (?) RETURNING id", nueva.getNombre());
            } else {
                id = anterior.getId();
                exigirActualizacion(ejecutar(conexion,
                        "UPDATE categorias SET nombre = ? WHERE id = ? AND nombre = ?",
                        nueva.getNombre(), id, anterior.getNombre()));
            }
            return new Categoria(id, nueva.getNombre());
        });
    }

    private static void exigirCategoriaSinLibros(Connection conexion, long id) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement("SELECT 1 FROM libros WHERE categoria_id = ? LIMIT 1")) {
            consulta.setLong(1, id);
            try (ResultSet filas = consulta.executeQuery()) {
                if (filas.next()) throw new IllegalStateException(
                        "Esta categoría está asignada a uno o más libros. Reasignalos antes de eliminarla");
            }
        }
    }

    public void validarBajaCategoria(Categoria categoria) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo)) {
            exigirCategoriaSinLibros(conexion, categoria.getId());
        }
    }

    public void eliminarCategoria(Categoria categoria) throws SQLException {
        ConexionSQLite.transaccion(archivo, conexion -> {
            ejecutar(conexion, "UPDATE categorias SET id = id WHERE 0");
            exigirCategoriaSinLibros(conexion, categoria.getId());
            exigirActualizacion(ejecutar(conexion, "DELETE FROM categorias WHERE id = ? AND nombre = ?",
                    categoria.getId(), categoria.getNombre()));
            return null;
        });
    }

    public List<Editorial> listarEditoriales() throws SQLException {
        List<Editorial> editoriales = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("SELECT id, nombre FROM editoriales ORDER BY nombre, id")) {
            while (filas.next()) {
                editoriales.add(new Editorial(filas.getLong("id"), filas.getString("nombre")));
            }
        }
        return editoriales;
    }

    public Editorial guardarEditorial(Editorial anterior, String nombre) throws SQLException {
        Editorial nueva = new Editorial(anterior == null ? null : anterior.getId(), nombre);
        return ConexionSQLite.transaccion(archivo, conexion -> {
            // Mismo bloqueo y comparación exacta de nombres que guardarLibro/buscarOCrear.
            ejecutar(conexion, "UPDATE editoriales SET id = id WHERE 0");
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id FROM editoriales WHERE nombre = ? AND (? IS NULL OR id <> ?)")) {
                consulta.setString(1, nueva.getNombre());
                consulta.setObject(2, nueva.getId());
                consulta.setObject(3, nueva.getId());
                try (ResultSet filas = consulta.executeQuery()) {
                    if (filas.next()) throw new IllegalArgumentException("Ya existe una editorial con ese nombre");
                }
            }
            long id;
            if (anterior == null) {
                id = insertar(conexion, "INSERT INTO editoriales (nombre) VALUES (?) RETURNING id", nueva.getNombre());
            } else {
                id = anterior.getId();
                exigirActualizacion(ejecutar(conexion,
                        "UPDATE editoriales SET nombre = ? WHERE id = ? AND nombre = ?",
                        nueva.getNombre(), id, anterior.getNombre()));
            }
            return new Editorial(id, nueva.getNombre());
        });
    }

    private static void exigirEditorialSinLibros(Connection conexion, long id) throws SQLException {
        try (PreparedStatement consulta = conexion.prepareStatement("SELECT 1 FROM libros WHERE editorial_id = ? LIMIT 1")) {
            consulta.setLong(1, id);
            try (ResultSet filas = consulta.executeQuery()) {
                if (filas.next()) throw new IllegalStateException(
                        "Esta editorial está asignada a uno o más libros. Reasignalos antes de eliminarla");
            }
        }
    }

    public void validarBajaEditorial(Editorial editorial) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo)) {
            exigirEditorialSinLibros(conexion, editorial.getId());
        }
    }

    public void eliminarEditorial(Editorial editorial) throws SQLException {
        ConexionSQLite.transaccion(archivo, conexion -> {
            ejecutar(conexion, "UPDATE editoriales SET id = id WHERE 0");
            exigirEditorialSinLibros(conexion, editorial.getId());
            exigirActualizacion(ejecutar(conexion, "DELETE FROM editoriales WHERE id = ? AND nombre = ?",
                    editorial.getId(), editorial.getNombre()));
            return null;
        });
    }

    private List<Empleado> listarEmpleados(Connection conexion) throws SQLException {
        List<Empleado> empleados = new ArrayList<>();
        try (Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("SELECT id, legajo, nombre, activo FROM empleados ORDER BY id")) {
            while (filas.next()) {
                empleados.add(new Empleado(filas.getLong("id"), filas.getString("legajo"),
                        filas.getString("nombre"), filas.getBoolean("activo")));
            }
        }
        return empleados;
    }

    private List<Libro> listarLibros(Connection conexion) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        try (Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("""
                    SELECT l.*, e.nombre AS editorial, c.nombre AS categoria
                    FROM libros l JOIN editoriales e ON l.editorial_id = e.id
                    JOIN categorias c ON l.categoria_id = c.id ORDER BY l.id
                    """);
                PreparedStatement autores = conexion.prepareStatement("""
                    SELECT a.id, a.nombre FROM autores a JOIN libro_autor la ON la.autor_id = a.id
                    WHERE la.libro_id = ? ORDER BY a.id
                    """)) {
            while (filas.next()) {
                long id = filas.getLong("id");
                autores.setLong(1, id);
                List<Autor> listaAutores = new ArrayList<>();
                try (ResultSet resultados = autores.executeQuery()) {
                    while (resultados.next()) {
                        listaAutores.add(new Autor(resultados.getLong("id"), resultados.getString("nombre")));
                    }
                }
                libros.add(new Libro(id, filas.getString("isbn"), filas.getString("titulo"), listaAutores,
                        new Editorial(filas.getLong("editorial_id"), filas.getString("editorial")),
                        new Categoria(filas.getLong("categoria_id"), filas.getString("categoria")),
                        dinero(filas, "precio"), filas.getInt("stock"), filas.getBoolean("activo")));
            }
        }
        return libros;
    }

    public record Datos(List<Libro> libros, List<Empleado> empleados, List<Cliente> clientes,
            List<Venta> ventas, List<Devolucion> devoluciones) {}

    public Datos cargar() throws SQLException {
        return ConexionSQLite.transaccion(archivo, conexion -> {
            List<Libro> listaLibros = listarLibros(conexion);
            List<Empleado> listaEmpleados = listarEmpleados(conexion);
            List<Cliente> listaClientes = ClienteDAO.listar(conexion);
            Map<Long, Libro> libros = new HashMap<>();
            Map<Long, Empleado> empleados = new HashMap<>();
            Map<Long, Cliente> clientes = new HashMap<>();
            listaLibros.forEach(libro -> libros.put(libro.getId(), libro));
            listaEmpleados.forEach(empleado -> empleados.put(empleado.getId(), empleado));
            listaClientes.forEach(cliente -> clientes.put(cliente.getId(), cliente));

            Map<Long, Venta> ventas = new LinkedHashMap<>();
            try (Statement consulta = conexion.createStatement();
                    ResultSet filas = consulta.executeQuery("""
                        SELECT v.*, p.id AS pago_id, p.fecha AS fecha_pago, p.medio, p.importe, p.recibido, p.referencia
                        FROM ventas v LEFT JOIN pagos p ON p.venta_id = v.id ORDER BY v.id
                        """);
                    PreparedStatement detalles = conexion.prepareStatement("""
                        SELECT * FROM detalles_venta WHERE venta_id = ? ORDER BY rowid
                        """)) {
                while (filas.next()) {
                    long id = filas.getLong("id");
                    String codigo = filas.getString("descuento_codigo");
                    Descuento descuento = codigo == null ? null
                            : Descuento.rehidratar(codigo, dinero(filas, "descuento_porcentaje"));
                    detalles.setLong(1, id);
                    List<DetalleVenta> listaDetalles = new ArrayList<>();
                    try (ResultSet lineas = detalles.executeQuery()) {
                        while (lineas.next()) {
                            listaDetalles.add(new DetalleVenta(libros.get(lineas.getLong("libro_id")),
                                    lineas.getInt("cantidad"), dinero(lineas, "precio_unitario"),
                                    lineas.getString("titulo"), lineas.getString("isbn")));
                        }
                    }
                    Long clienteId = idOpcional(filas, "cliente_id");
                    ventas.put(id, Venta.rehidratar(id, fecha(filas, "fecha"), empleados.get(filas.getLong("empleado_id")),
                            clientes.get(clienteId), listaDetalles, descuento,
                            leerPago(filas), EstadoVenta.valueOf(filas.getString("estado"))));
                }
            }

            List<Devolucion> devoluciones = new ArrayList<>();
            try (Statement consulta = conexion.createStatement();
                    ResultSet filas = consulta.executeQuery("SELECT * FROM devoluciones ORDER BY id")) {
                while (filas.next()) {
                    devoluciones.add(Devolucion.rehidratar(filas.getLong("id"), ventas.get(filas.getLong("venta_id")),
                            fecha(filas, "fecha"), empleados.get(filas.getLong("empleado_id")), filas.getString("motivo")));
                }
            }
            return new Datos(listaLibros, listaEmpleados, listaClientes,
                    List.copyOf(ventas.values()), devoluciones);
        });
    }

    public Cliente asociar(String nombre, String dni) throws SQLException {
        return ConexionSQLite.transaccion(archivo, conexion -> {
            // La primera escritura serializa dos altas concurrentes para el mismo DNI.
            int filas = ejecutar(conexion, """
                    INSERT INTO socios (cliente_id)
                    SELECT id FROM clientes WHERE dni = ? ORDER BY id LIMIT 1
                    ON CONFLICT (cliente_id) DO NOTHING
                    """, dni);
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id, nombre FROM clientes WHERE dni = ? ORDER BY id LIMIT 1")) {
                consulta.setString(1, dni);
                try (ResultSet fila = consulta.executeQuery()) {
                    if (fila.next()) {
                        if (filas == 0) {
                            throw new IllegalArgumentException("Ese DNI ya está registrado como socio");
                        }
                        return new Cliente(fila.getLong("id"), fila.getString("nombre"), dni);
                    }
                }
            }
            Cliente nuevo = new Cliente(null, nombre, dni);
            long id = insertar(conexion, "INSERT INTO clientes (nombre, dni) VALUES (?, ?) RETURNING id",
                    nuevo.getNombre(), nuevo.getDni());
            ejecutar(conexion, "INSERT INTO socios (cliente_id) VALUES (?)", id);
            return new Cliente(id, nuevo.getNombre(), nuevo.getDni());
        });
    }

    public List<Cliente> listarSocios() throws SQLException {
        List<Cliente> socios = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("""
                    SELECT c.id, c.nombre, c.dni FROM socios s JOIN clientes c ON c.id = s.cliente_id
                    ORDER BY c.nombre, c.id
                    """)) {
            while (filas.next()) {
                socios.add(new Cliente(filas.getLong("id"), filas.getString("nombre"), filas.getString("dni")));
            }
        }
        return socios;
    }

    public void prestar(String dni, Libro libro) throws SQLException {
        ConexionSQLite.transaccion(archivo, conexion -> {
            int filas = ejecutar(conexion, """
                    INSERT INTO prestamos (socio_id, libro_id, fecha)
                    SELECT s.cliente_id, ?, ? FROM socios s JOIN clientes c ON c.id = s.cliente_id
                    WHERE c.dni = ? ORDER BY s.cliente_id LIMIT 1
                    """, libro.getId(), LocalDateTime.now().toString(), dni);
            if (filas == 0) {
                throw new IllegalArgumentException("Registrá al socio antes de hacer un préstamo");
            }
            retirarStock(conexion, libro, 1);
            return null;
        });
    }

    public List<Prestamo> listarPrestamos() throws SQLException {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection conexion = ConexionSQLite.abrir(archivo);
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("""
                    SELECT p.*, l.titulo, c.nombre, c.dni FROM prestamos p
                    JOIN libros l ON l.id = p.libro_id JOIN clientes c ON c.id = p.socio_id
                    ORDER BY p.devuelto_en IS NOT NULL, p.id DESC
                    """)) {
            while (filas.next()) {
                String devolucion = filas.getString("devuelto_en");
                prestamos.add(new Prestamo(filas.getLong("id"), filas.getLong("libro_id"),
                        filas.getString("titulo"), filas.getString("nombre"), filas.getString("dni"),
                        fecha(filas, "fecha"), devolucion == null ? null : LocalDateTime.parse(devolucion)));
            }
        }
        return prestamos;
    }

    public void devolverPrestamo(long id, Libro libro) throws SQLException {
        ConexionSQLite.transaccion(archivo, conexion -> {
            int filas = ejecutar(conexion, """
                    UPDATE prestamos SET devuelto_en = ? WHERE id = ? AND libro_id = ? AND devuelto_en IS NULL
                    """, LocalDateTime.now().toString(), id, libro.getId());
            if (filas == 0) {
                throw new IllegalArgumentException("El préstamo ya fue devuelto o no existe");
            }
            reponerStock(conexion, libro, 1);
            return null;
        });
    }

    public Libro guardarLibro(Libro anterior, Libro nuevo) throws SQLException {
        return ConexionSQLite.transaccion(archivo, conexion -> {
            // Se toma el bloqueo de escritura antes de verificar el ISBN y el estado anterior.
            ejecutar(conexion, "UPDATE libros SET id = id WHERE 0");
            try (PreparedStatement consulta = conexion.prepareStatement(
                    "SELECT id FROM libros WHERE isbn = ?")) {
                consulta.setString(1, nuevo.getIsbn());
                try (ResultSet filas = consulta.executeQuery()) {
                    if (filas.next() && (anterior == null || filas.getLong(1) != anterior.getId())) {
                        throw new IllegalArgumentException("Ya existe un libro con ese ISBN");
                    }
                }
            }
            long editorial = buscarOCrear(conexion, "editoriales", nuevo.getEditorial().getNombre());
            long categoria = buscarOCrear(conexion, "categorias", nuevo.getCategoria().getNombre());
            long id;
            if (anterior == null) {
                id = insertar(conexion, """
                        INSERT INTO libros (isbn, titulo, editorial_id, categoria_id, precio, stock, activo)
                        VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id
                        """, nuevo.getIsbn(), nuevo.getTitulo(), editorial, categoria,
                        nuevo.getPrecioVenta().toPlainString(), nuevo.getStock(), nuevo.isActivo());
            } else {
                id = anterior.getId();
                exigirActualizacion(ejecutar(conexion, """
                        UPDATE libros SET isbn = ?, titulo = ?, editorial_id = ?, categoria_id = ?,
                                          precio = ?, stock = ?, activo = ?
                        WHERE id = ? AND isbn = ? AND titulo = ? AND editorial_id = ? AND categoria_id = ?
                          AND precio = ? AND stock = ? AND activo = ?
                        """, nuevo.getIsbn(), nuevo.getTitulo(), editorial, categoria,
                        nuevo.getPrecioVenta().toPlainString(), nuevo.getStock(), nuevo.isActivo(),
                        id, anterior.getIsbn(), anterior.getTitulo(), anterior.getEditorial().getId(),
                        anterior.getCategoria().getId(), anterior.getPrecioVenta().toPlainString(),
                        anterior.getStock(), anterior.isActivo()));
                List<Long> autoresGuardados = new ArrayList<>();
                try (PreparedStatement consulta = conexion.prepareStatement(
                        "SELECT autor_id FROM libro_autor WHERE libro_id = ? ORDER BY autor_id")) {
                    consulta.setLong(1, id);
                    try (ResultSet filas = consulta.executeQuery()) {
                        while (filas.next()) {
                            autoresGuardados.add(filas.getLong(1));
                        }
                    }
                }
                List<Long> autoresAnteriores = anterior.getAutores().stream().map(Autor::getId).sorted().toList();
                exigirActualizacion(autoresGuardados.equals(autoresAnteriores) ? 1 : 0);
                ejecutar(conexion, "DELETE FROM libro_autor WHERE libro_id = ?", id);
            }
            List<Autor> autores = new ArrayList<>();
            for (Autor autor : nuevo.getAutores()) {
                long autorId = buscarOCrear(conexion, "autores", autor.getNombre());
                if (ejecutar(conexion, "INSERT OR IGNORE INTO libro_autor VALUES (?, ?)", id, autorId) == 1) {
                    autores.add(new Autor(autorId, autor.getNombre()));
                }
            }
            return new Libro(id, nuevo.getIsbn(), nuevo.getTitulo(), autores,
                    new Editorial(editorial, nuevo.getEditorial().getNombre()),
                    new Categoria(categoria, nuevo.getCategoria().getNombre()),
                    nuevo.getPrecioVenta(), nuevo.getStock(), nuevo.isActivo());
        });
    }

    public void actualizarLibro(Libro libro, BigDecimal precio, int stock, boolean activo) throws SQLException {
        try (Connection conexion = ConexionSQLite.abrir(archivo)) {
            exigirActualizacion(ejecutar(conexion, """
                    UPDATE libros SET precio = ?, stock = ?, activo = ?
                    WHERE id = ? AND precio = ? AND stock = ? AND activo = ?
                    """, precio.toPlainString(), stock, activo, libro.getId(), libro.getPrecioVenta().toPlainString(),
                    libro.getStock(), libro.isActivo()));
        }
    }

    public long guardarVenta(Venta venta, Pago pago, EstadoVenta estado) throws SQLException {
        return ConexionSQLite.transaccion(archivo, conexion -> {
            Descuento descuento = venta.getDescuento();
            Cliente cliente = venta.getCliente();
            long id = insertar(conexion, """
                    INSERT INTO ventas (fecha, empleado_id, cliente_id, estado, descuento_codigo, descuento_porcentaje)
                    VALUES (?, ?, ?, ?, ?, ?) RETURNING id
                    """, venta.getFecha().toString(), venta.getEmpleado().getId(),
                    cliente == null ? null : cliente.getId(), estado.name(),
                    descuento == null ? null : descuento.getCodigo(),
                    descuento == null ? null : descuento.getPorcentaje().toPlainString());
            for (DetalleVenta detalle : venta.getDetalles()) {
                ejecutar(conexion, """
                        INSERT INTO detalles_venta (venta_id, libro_id, cantidad, precio_unitario, titulo, isbn)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """, id, detalle.getLibro().getId(), detalle.getCantidad(),
                        detalle.getPrecioUnitario().toPlainString(), detalle.getTitulo(), detalle.getIsbn());
            }
            if (pago != null) {
                ejecutar(conexion, """
                        INSERT INTO pagos (venta_id, id, fecha, medio, importe, recibido, referencia)
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                        """, id, pago.getId(), pago.getFecha().toString(), pago.getMedioPago().name(),
                        pago.getImportePagado().toPlainString(),
                        pago.getMontoRecibido() == null ? null : pago.getMontoRecibido().toPlainString(),
                        pago.getReferenciaAutorizacion());
            }
            if (estado == EstadoVenta.CONFIRMADA) {
                moverStock(conexion, venta, false);
            }
            return id;
        });
    }

    public long guardarDevolucion(Venta venta, LocalDateTime fecha, Empleado responsable, String motivo)
            throws SQLException {
        return ConexionSQLite.transaccion(archivo, conexion -> {
            exigirActualizacion(ejecutar(conexion, """
                    UPDATE ventas SET estado = 'DEVUELTA' WHERE id = ? AND estado = 'CONFIRMADA'
                    """, venta.getId()));
            long id = insertar(conexion, """
                    INSERT INTO devoluciones (venta_id, fecha, empleado_id, motivo) VALUES (?, ?, ?, ?) RETURNING id
                    """, venta.getId(), fecha.toString(), responsable.getId(), motivo.trim());
            moverStock(conexion, venta, true);
            return id;
        });
    }

    private static void moverStock(Connection conexion, Venta venta, boolean reponer) throws SQLException {
        for (DetalleVenta detalle : venta.getDetalles()) {
            if (reponer) {
                reponerStock(conexion, detalle.getLibro(), detalle.getCantidad());
            } else {
                retirarStock(conexion, detalle.getLibro(), detalle.getCantidad());
            }
        }
    }

    private static void retirarStock(Connection conexion, Libro libro, int cantidad) throws SQLException {
        exigirActualizacion(ejecutar(conexion, """
                UPDATE libros SET stock = stock - ? WHERE id = ? AND stock = ? AND activo = 1 AND stock >= ?
                """, cantidad, libro.getId(), libro.getStock(), cantidad));
    }

    private static void reponerStock(Connection conexion, Libro libro, int cantidad) throws SQLException {
        exigirActualizacion(ejecutar(conexion, """
                UPDATE libros SET stock = stock + ? WHERE id = ? AND stock = ?
                """, cantidad, libro.getId(), libro.getStock()));
    }

    private static void exigirActualizacion(int filas) {
        if (filas != 1) {
            throw new IllegalStateException("Los datos cambiaron en la base. Volvé a abrir la aplicación y reintentá.");
        }
    }

    private static Pago leerPago(ResultSet fila) throws SQLException {
        String medio = fila.getString("medio");
        if (medio == null) {
            return null;
        }
        Long id = idOpcional(fila, "pago_id");
        LocalDateTime fecha = fecha(fila, "fecha_pago");
        BigDecimal importe = dinero(fila, "importe");
        return switch (MedioPago.valueOf(medio)) {
            case EFECTIVO -> Pago.enEfectivo(id, fecha, importe, dinero(fila, "recibido"));
            case TARJETA -> Pago.conTarjeta(id, fecha, importe, fila.getString("referencia"));
        };
    }

    private static Long idOpcional(ResultSet fila, String columna) throws SQLException {
        long id = fila.getLong(columna);
        return fila.wasNull() ? null : id;
    }

    private static BigDecimal dinero(ResultSet fila, String columna) throws SQLException {
        return new BigDecimal(fila.getString(columna));
    }

    private static LocalDateTime fecha(ResultSet fila, String columna) throws SQLException {
        return LocalDateTime.parse(fila.getString(columna));
    }

    private static int ejecutar(Connection conexion, String sql, Object... valores) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, valores);
            return sentencia.executeUpdate();
        }
    }

    private static long insertar(Connection conexion, String sql, Object... valores) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignarParametros(sentencia, valores);
            try (ResultSet filas = sentencia.executeQuery()) {
                if (!filas.next()) {
                    throw new SQLException("No se obtuvo el ID generado");
                }
                return filas.getLong(1);
            }
        }
    }

    private static void asignarParametros(PreparedStatement sentencia, Object[] valores) throws SQLException {
        for (int i = 0; i < valores.length; i++) {
            sentencia.setObject(i + 1, valores[i]);
        }
    }
}
