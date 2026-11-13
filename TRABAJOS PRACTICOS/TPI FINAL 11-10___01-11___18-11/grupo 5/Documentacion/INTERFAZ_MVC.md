## Recorrido

1. Desde Inicio, abrir Nueva venta. El sistema asigna el primer empleado activo.
2. Buscar libros por título o ISBN, seleccionar una fila y agregar cantidades.
   La tabla inferior permite cambiar cantidades o quitar libros.
3. Usar Buscar cliente para elegir uno por nombre, apellido o DNI, o Nuevo cliente
   para cargar sus datos. El cliente es opcional para efectivo y obligatorio para tarjeta.
4. Aplicar opcionalmente HOTSALE, pasar al pago y confirmar.
5. Consultar el detalle de la operación y el vuelto.
6. Desde Inicio, acceder al historial, abrir una venta y devolverla íntegramente.
7. Desde Administrar libros, buscar un título, cargar un libro o editar sus datos.
8. Desde Hacerse socio, registrar el DNI y, si es un cliente nuevo, su nombre.
9. Desde Préstamos y devoluciones, buscar al socio por parte del nombre, apellido
   o DNI y seleccionarlo junto con un ejemplar. Desde el historial, seleccionar
   el préstamo para devolverlo.

La tarjeta es simulada: únicamente registra una referencia ficticia de aprobación.
No procesa pagos bancarios. La devolución reintegra el importe y medio originales.

## Responsabilidades

- `vista`: componentes Swing, campos, tablas y mensajes; sin cálculos de negocio.
- `controlador`: eventos, navegación, conversión de entradas y presentación de errores.
- `modelo`: reglas, cálculos, estados y validaciones existentes.
- `controlador/Navegacion`: abre las pantallas.
- `controlador/EntradaFormulario`: convierte importes y obtiene la fila seleccionada.
- `servicio/LibreriaServicio`: operaciones de clientes, socios, catálogo, ventas y préstamos.
- `aplicacion/Main` inicia Swing en el Event Dispatch Thread y conecta dependencias.
- `persistencia` inicializa SQLite, carga el catálogo y el historial, y guarda las operaciones.

Al arrancar, el servicio reconstruye los datos desde SQLite. Al confirmar una
venta o una devolución, primero valida las reglas de negocio, guarda la
operación y el stock en una transacción, y luego actualiza los objetos de Swing.
El carrito en preparación se mantiene durante la sesión.

## Verificación

Ver las instrucciones de ejecución y de pruebas en [SQLITE.md](SQLITE.md).
