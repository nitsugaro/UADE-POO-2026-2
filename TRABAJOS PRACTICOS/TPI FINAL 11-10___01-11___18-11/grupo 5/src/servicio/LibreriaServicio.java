package servicio;

import modelo.*;
import persistencia.ClienteDAO;
import persistencia.ConexionSQLite;
import persistencia.LibreriaDAO;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Coordina las operaciones de la librería desde el hilo de Swing.
public final class LibreriaServicio {
    private final LibreriaDAO datos;
    private final ClienteDAO clientesDAO;
    private final List<Libro> libros;
    private final List<Cliente> clientes;
    private final List<Empleado> empleados;
    private final List<Venta> ventas;
    private final List<Devolucion> devoluciones;

    public LibreriaServicio() {
        this(ConexionSQLite.rutaPredeterminada());
    }

    public LibreriaServicio(Path archivo) {
        datos = new LibreriaDAO(archivo);
        clientesDAO = new ClienteDAO(archivo);
        try {
            ConexionSQLite.inicializar(archivo);
            datos.cargarEjemplos();
            LibreriaDAO.Datos guardados = datos.cargar();
            libros = new ArrayList<>(guardados.libros());
            clientes = new ArrayList<>(guardados.clientes());
            empleados = guardados.empleados();
            ventas = new ArrayList<>(guardados.ventas());
            devoluciones = new ArrayList<>(guardados.devoluciones());
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudo abrir o cargar la base de la librería.", error);
        }
    }

    public List<Libro> libros() {
        return List.copyOf(libros);
    }

    public List<Autor> autores() {
        try {
            return datos.listarAutores();
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudieron consultar los autores.", error);
        }
    }

    public List<Categoria> categorias() {
        try {
            return datos.listarCategorias();
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudieron consultar las categorías.", error);
        }
    }

    public Categoria guardarCategoria(Categoria anterior, String nombre) {
        try {
            Categoria guardada = datos.guardarCategoria(anterior, nombre);
            // Cada libro cargado puede tener una instancia distinta de la misma categoría.
            for (Libro libro : libros) {
                if (guardada.getId().equals(libro.getCategoria().getId())) {
                    libro.getCategoria().cambiarNombre(guardada.getNombre());
                }
            }
            if (anterior != null) anterior.cambiarNombre(guardada.getNombre());
            return guardada;
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void validarBajaCategoria(Categoria categoria) {
        try {
            datos.validarBajaCategoria(categoria);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void eliminarCategoria(Categoria categoria) {
        try {
            datos.eliminarCategoria(categoria);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public List<Editorial> editoriales() {
        try {
            return datos.listarEditoriales();
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudieron consultar las editoriales.", error);
        }
    }

    public Editorial guardarEditorial(Editorial anterior, String nombre) {
        try {
            Editorial guardada = datos.guardarEditorial(anterior, nombre);
            // Cada libro cargado puede tener una instancia distinta de la misma editorial.
            for (Libro libro : libros) {
                if (guardada.getId().equals(libro.getEditorial().getId())) {
                    libro.getEditorial().cambiarNombre(guardada.getNombre());
                }
            }
            if (anterior != null) anterior.cambiarNombre(guardada.getNombre());
            return guardada;
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void validarBajaEditorial(Editorial editorial) {
        try {
            datos.validarBajaEditorial(editorial);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void eliminarEditorial(Editorial editorial) {
        try {
            datos.eliminarEditorial(editorial);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public List<Cliente> clientes() {
        return List.copyOf(clientes);
    }

    public void cambiarActividadCliente(Cliente cliente, boolean activo) {
        if (!clientes.contains(cliente)) throw new IllegalArgumentException("El cliente no pertenece a esta sesión");
        try {
            clientesDAO.cambiarActividad(cliente.getId(), activo);
            cliente.cambiarActividad(activo);
            for (Venta venta : ventas) {
                if (venta.getCliente() != null && cliente.getId().equals(venta.getCliente().getId())) {
                    venta.getCliente().cambiarActividad(activo);
                }
            }
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void modificarCliente(Cliente cliente, String nombre, String dni) {
        if (!clientes.contains(cliente)) throw new IllegalArgumentException("El cliente no pertenece a esta sesión");
        try {
            Cliente guardado = clientesDAO.modificar(cliente, nombre, dni);
            cliente.actualizarDesde(guardado);
            for (Venta venta : ventas) {
                if (venta.getCliente() != null && guardado.getId().equals(venta.getCliente().getId())) {
                    venta.getCliente().actualizarDesde(guardado);
                }
            }
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public List<Empleado> empleados() {
        return List.copyOf(empleados);
    }

    public List<Venta> ventas() {
        return List.copyOf(ventas);
    }

    public boolean hayVentaEnPreparacion() {
        return ventas.stream().anyMatch(venta -> venta.getEstado() == EstadoVenta.EN_PREPARACION);
    }

    public List<Devolucion> devoluciones() {
        return List.copyOf(devoluciones);
    }

    public Cliente crearCliente(String nombre, String dni) {
        try {
            Cliente cliente = clientesDAO.guardar(new Cliente(null, nombre, dni));
            clientes.add(cliente);
            return cliente;
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public Empleado empleadoPredeterminado() {
        return empleados.stream().filter(Empleado::isActivo).findFirst()
                .orElseThrow(() -> new IllegalStateException("No hay un empleado activo para registrar la operación"));
    }

    public Cliente asociar(String nombre, String dni) {
        try {
            Cliente socio = datos.asociar(nombre, validarDni(dni));
            if (clientes.stream().noneMatch(cliente -> cliente.getId().equals(socio.getId()))) {
                clientes.add(socio);
            }
            return socio;
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public List<Cliente> socios() {
        try {
            return datos.listarSocios();
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudieron consultar los socios.", error);
        }
    }

    public List<Prestamo> prestamos() {
        try {
            return datos.listarPrestamos();
        } catch (SQLException error) {
            throw new IllegalStateException("No se pudieron consultar los préstamos.", error);
        }
    }

    public void prestar(String dni, Libro libro) {
        exigirLibro(libro);
        String documento = validarDni(dni);
        if (!libro.hayDisponibilidad(1)) {
            throw new IllegalArgumentException("El libro no está disponible para préstamo");
        }
        try {
            datos.prestar(documento, libro);
            libro.disminuirStock(1);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void devolverPrestamo(Prestamo prestamo) {
        if (prestamo == null) throw new IllegalArgumentException("Seleccioná un préstamo pendiente");
        Libro libro = libros.stream().filter(item -> item.getId() == prestamo.libroId()).findFirst()
                .orElseThrow(() -> new IllegalStateException("Volvé a abrir la aplicación para cargar el libro"));
        Math.addExact(libro.getStock(), 1);
        try {
            datos.devolverPrestamo(prestamo.id(), libro);
            libro.aumentarStock(1);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void guardarLibro(Libro anterior, String isbn, String titulo, List<String> nombresAutores,
            String editorial, String categoria, BigDecimal precio, int stock, boolean activo) {
        if (anterior != null) {
            exigirLibro(anterior);
        }
        List<Autor> autores = nombresAutores.stream().map(nombre -> new Autor(null, nombre)).toList();
        Libro nuevo = new Libro(anterior == null ? null : anterior.getId(), isbn, titulo, autores,
                new Editorial(null, editorial), new Categoria(null, categoria), precio, stock, activo);
        try {
            Libro guardado = datos.guardarLibro(anterior, nuevo);
            if (anterior == null) {
                libros.add(guardado);
            } else {
                anterior.actualizarDesde(guardado);
            }
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public Venta nuevaVenta(Empleado empleado) {
        if (empleado == null || !empleado.isActivo()) {
            throw new IllegalArgumentException("Seleccioná un empleado activo");
        }
        Venta venta = new Venta(null, LocalDateTime.now(), empleado, null);
        ventas.add(venta);
        return venta;
    }

    public void cancelar(Venta venta) {
        exigirBorrador(venta);
        guardarVenta(venta, venta.getPago(), EstadoVenta.CANCELADA);
        venta.cancelar();
    }

    public void confirmar(Venta venta, Pago pago) {
        exigirBorrador(venta);
        venta.validarConfirmacion(pago);
        for (DetalleVenta detalle : venta.getDetalles()) {
            exigirLibro(detalle.getLibro());
        }
        guardarVenta(venta, pago, EstadoVenta.CONFIRMADA);
        // La transacción ya terminó. Recién ahora se actualizan los objetos de la vista.
        venta.registrarPago(pago);
        venta.confirmar();
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.getLibro().disminuirStock(detalle.getCantidad());
        }
    }

    private void guardarVenta(Venta venta, Pago pago, EstadoVenta estado) {
        try {
            venta.asignarIdPersistido(datos.guardarVenta(venta, pago, estado));
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public Devolucion devolver(Venta venta, Empleado responsable, String motivo, boolean aptos) {
        exigirVenta(venta);
        LocalDateTime fecha = LocalDateTime.now();
        venta.validarDevolucion(fecha, responsable, motivo, aptos);
        for (DetalleVenta detalle : venta.getDetalles()) {
            Math.addExact(detalle.getLibro().getStock(), detalle.getCantidad());
        }
        try {
            long id = datos.guardarDevolucion(venta, fecha, responsable, motivo);
            Devolucion devolucion = venta.completarDevolucion(id, fecha, responsable, motivo, aptos);
            for (DetalleVenta detalle : venta.getDetalles()) {
                detalle.getLibro().aumentarStock(detalle.getCantidad());
            }
            devoluciones.add(devolucion);
            return devolucion;
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    public void cambiarPrecio(Libro libro, BigDecimal precio) {
        exigirLibro(libro);
        Libro actualizado = new Libro(libro.getId(), libro.getIsbn(), libro.getTitulo(), libro.getAutores(),
                libro.getEditorial(), libro.getCategoria(), precio, libro.getStock(), libro.isActivo());
        guardarCatalogo(libro, actualizado.getPrecioVenta(), libro.getStock(), libro.isActivo());
        libro.cambiarPrecio(actualizado.getPrecioVenta());
    }

    public void ingresarStock(Libro libro, int cantidad) {
        exigirLibro(libro);
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        int stock = Math.addExact(libro.getStock(), cantidad);
        guardarCatalogo(libro, libro.getPrecioVenta(), stock, libro.isActivo());
        libro.aumentarStock(cantidad);
    }

    public void cambiarActividad(Libro libro, boolean activo) {
        exigirLibro(libro);
        guardarCatalogo(libro, libro.getPrecioVenta(), libro.getStock(), activo);
        if (activo) {
            libro.activar();
        } else {
            libro.desactivar();
        }
    }

    private void guardarCatalogo(Libro libro, BigDecimal precio, int stock, boolean activo) {
        try {
            datos.actualizarLibro(libro, precio, stock, activo);
        } catch (SQLException error) {
            throw errorAlGuardar(error);
        }
    }

    private static String validarDni(String dni) {
        String documento = dni == null ? "" : dni.trim();
        if (!documento.matches("[0-9]+")) {
            throw new IllegalArgumentException("Ingresá un DNI válido");
        }
        return documento;
    }

    private void exigirLibro(Libro libro) {
        if (!libros.contains(libro)) {
            throw new IllegalArgumentException("El libro no pertenece al catálogo");
        }
    }

    private void exigirVenta(Venta venta) {
        if (!ventas.contains(venta)) {
            throw new IllegalArgumentException("La venta no pertenece a esta sesión");
        }
    }

    private void exigirBorrador(Venta venta) {
        exigirVenta(venta);
        if (venta.getEstado() != EstadoVenta.EN_PREPARACION || venta.getId() != null) {
            throw new IllegalStateException("La venta no está en preparación");
        }
    }

    private static IllegalStateException errorAlGuardar(SQLException error) {
        return new IllegalStateException("No se pudo guardar la operación en SQLite.", error);
    }
}
