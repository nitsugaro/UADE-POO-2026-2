# SQLite en el TPO del grupo 5

La aplicación Swing usa el archivo `datos/libreria.db` para guardar clientes,
catálogo, socios, préstamos, ventas confirmadas o canceladas, pagos y devoluciones. Al iniciar,
crea las tablas faltantes y carga los registros guardados. Si el archivo ya
tiene clientes de la versión anterior, conserva sus datos e identificadores.
Al iniciar se cargan 13 clientes con nombres y DNI ficticios y 56 libros con
identificadores en formato ISBN-13. El catálogo incluye 50 títulos conocidos
en español con datos comerciales ficticios. La carga actualiza los códigos y
nombres de los ejemplos anteriores, conservando sus identificadores internos.
La carga inicial del catálogo conserva las ediciones de libros al volver a abrir la aplicación.

## Ejecutar la interfaz

Con JDK 17 o superior, desde la carpeta `grupo 5`:

```sh
mkdir -p out
find src test -name '*.java' > out/fuentes.txt
javac -encoding UTF-8 -cp 'lib/*' -d out @out/fuentes.txt
java -cp 'out:lib/*' aplicacion.Main
```

En IntelliJ, marcar `src` como Sources Root y `test` como Test Sources Root.
En las dependencias del módulo, agregar `lib/sqlite-jdbc-3.50.3.0.jar`. Ejecutar
`aplicacion.Main` con la carpeta `grupo 5` como directorio de trabajo.

En Windows se usa `;` como separador del classpath. En JDK recientes se puede
añadir `--enable-native-access=ALL-UNNAMED` a las opciones de la JVM para evitar
la advertencia de acceso nativo del driver. La opción de JVM
`-Dtpo.db=/ruta/absoluta/otra.db` permite elegir otro archivo.

## Qué se guarda

- El alta de cliente guarda el DNI como texto para conservar ceros iniciales.
- El alta y la edición de libros guardan sus datos, autores y stock disponible.
- Un socio se vincula por DNI a un cliente existente o se registra con un cliente nuevo.
- Cada préstamo retira un ejemplar del stock disponible. Su devolución repone el
  ejemplar. El stock y el movimiento se guardan juntos en una transacción.
- Al confirmar una venta se guardan la venta, sus detalles y su pago junto con
  el descuento de stock. La cancelación también queda en el historial. El
  carrito en preparación vive en la sesión hasta confirmar o cancelar.
- Una devolución registra el motivo y el responsable, cambia el estado de la
  venta y repone el stock en una misma transacción.
- El historial conserva los precios de cada operación. Los importes se
  almacenan como texto decimal y se reconstruyen con `BigDecimal`.

Si otra sesión cambió los datos necesarios para una operación, la escritura se
revierte y la vista muestra un mensaje para volver a abrir la aplicación.

## Ejemplo de consola y pruebas

El ejemplo original sigue disponible para listar clientes o registrar uno en
el mismo archivo que usa la interfaz:

```sh
sh ejecutar-sqlite.sh
sh ejecutar-sqlite.sh 'Juan de prueba' '00123456'
```

Tras compilar, las pruebas pueden ejecutarse con el mismo classpath:

```sh
java -cp 'out:lib/*' pruebas.PruebasSQLite
java -cp 'out:lib/*' pruebas.PruebasServicio
java -cp 'out:lib/*' pruebas.PruebasModelo
java -Djava.awt.headless=true -cp 'out:lib/*' pruebas.PruebasVistas
```

Las pruebas de persistencia usan archivos temporales y verifican la reapertura,
la compatibilidad con la tabla de clientes anterior y la reversión de una venta
si cambia el stock durante la operación.
