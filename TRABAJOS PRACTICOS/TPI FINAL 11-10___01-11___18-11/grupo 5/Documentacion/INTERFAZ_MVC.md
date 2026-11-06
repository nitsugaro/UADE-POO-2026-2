## Recorrido

1. Inicio: seleccionar empleado y abrir Nueva venta.
2. Buscar libros por título o ISBN, seleccionar una fila y agregar cantidades.
   La tabla inferior permite cambiar cantidades o quitar libros.
3. Seleccionar/crear cliente (opcional para efectivo, obligatorio para tarjeta).
4. Aplicar opcionalmente HOTSALE, pasar al pago y confirmar.
5. Consultar el detalle de la operación y el vuelto.
6. Desde Inicio, acceder al historial, abrir una venta y devolverla íntegramente.
7. Desde Catálogo, cambiar precios, ingresar stock o activar/desactivar libros.

La tarjeta es simulada: únicamente registra una referencia ficticia de aprobación.
No procesa pagos bancarios. La devolución reintegra el importe y medio originales.

## Responsabilidades

- `vista`: componentes Swing, campos, tablas y mensajes; sin cálculos de negocio.
- `controlador`: eventos, navegación, conversión de entradas y presentación de errores.
- `modelo`: reglas, cálculos, estados y validaciones existentes.
- `controlador/Navegacion`: abre las pantallas y conserva el empleado seleccionado.
- `controlador/EntradaFormulario`: convierte importes y obtiene la fila seleccionada.
- `servicio/LibreriaServicio`: operaciones de clientes, catálogo, ventas y devoluciones.
- `servicio/DatosSesion`: listas e identificadores de la sesión en memoria.
- `servicio/DatosEjemplo`: crea los libros y el empleado de demostración.
- `aplicacion/Main`: inicia Swing en el Event Dispatch Thread y conecta dependencias.
- `persistencia`: ejemplos SQLite existentes, independientes de esta interfaz.

Todos los eventos y cambios de datos de esta implementación ocurren en el EDT.
El servicio valida antes de modificar stock y previene devoluciones duplicadas y
desbordamiento del stock. No representa una transacción SQL ni soporta concurrencia.

## Verificación

Después de compilar:

```sh
java -cp out pruebas.PruebasModelo
java -cp out pruebas.PruebasServicio
```

Las pruebas están en `test`; para compilarlas junto con las fuentes:

```sh
find src test -name '*.java' > out/fuentes-pruebas.txt
javac -encoding UTF-8 -d out @out/fuentes-pruebas.txt
```