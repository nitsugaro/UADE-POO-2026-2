# SQLite en el TPO del grupo 5

## Que se integro

Se sigue el ejemplo SQLite de las diapositivas: un driver JAR local en `lib`,
JDBC, creacion de tabla, insercion y consulta. No hace falta Maven ni servidor,
usuario o contrasena. Las primeras dos capturas corresponden a MySQL y no se
necesitan para este ejemplo SQLite. No hay que instalar XAMPP.

Se incluyo `lib/sqlite-jdbc-3.50.3.0.jar`, la version de la diapositiva, desde
Maven Central:
https://repo.maven.apache.org/maven2/org/xerial/sqlite-jdbc/3.50.3.0/
Driver y licencia: https://github.com/xerial/sqlite-jdbc/tree/3.50.3.0

El alcance implementado es persistencia inicial de clientes (alta, consulta por
ID y listado) usando el modelo existente. Todavia no se guardan libros, ventas,
pagos ni devoluciones. No se modificaron las reglas ni las clases del modelo.
La documentacion anterior mencionaba SQL Server; este ejemplo implementa la
alternativa SQLite solicitada a partir de las diapositivas.

## 1. Comprobar Java

En una terminal ejecutar `java -version` y `javac -version`. Se necesita JDK 17
o superior. La integracion se comprobo con el JDK 26 instalado en esta maquina.

## 2. Entender los archivos

- `src/persistencia/ConexionSQLite.java`: abre el archivo de base de datos,
  activa claves foraneas y crea la tabla `clientes` sin borrar datos.
- `src/persistencia/ClienteDAO.java`: convierte objetos Cliente en registros SQL
  y reconstruye objetos a partir de las consultas.
- `src/aplicacion/EjemploSQLite.java`: main que inicializa la tabla, opcionalmente
  inserta un cliente y lista los clientes guardados.
- `datos/libreria.db`: archivo creado al ejecutar; esta ignorado en Git.

El recorrido es `main -> ClienteDAO -> JDBC -> libreria.db`. En el futuro una
pantalla llamara a un controlador/servicio y este usara el DAO.

## 3. Ejecutar en Mac

Para una comprobacion automatica sin argumentos, ejecutar el main de
`src/aplicacion/Main.java` en IntelliJ. Inserta Ana, Bruno y Carla Prueba,
consulta cada ID desde una nueva conexion y muestra todos los clientes. Cada
ejecucion agrega tres registros nuevos. Usa la misma base `datos/libreria.db`.
Desde terminal, luego de compilar con el script de abajo:

```sh
java --enable-native-access=ALL-UNNAMED -cp 'out:lib/*' aplicacion.Main
```

Abrir una terminal en la carpeta del grupo:

```sh
cd '/Users/juanetchart29/IdeaProjects/UADE-POO-2026-2/TRABAJOS PRACTICOS/TPI FINAL 11-10___01-11___18-11/grupo 5'
sh ejecutar-sqlite.sh 'Juan de prueba' '00123456'
```

Se compila el proyecto, se crea la base si no existe, se guarda el cliente y
se muestra el listado. Cada ejecucion con nombre y DNI agrega otro cliente:
el modelo actual no exige DNI unico.

Para consultar sin insertar:

```sh
sh ejecutar-sqlite.sh
```

El cliente anterior debe seguir apareciendo aunque cierres la terminal. Este
es el objetivo de la persistencia: los datos sobreviven al proceso Java.

Los comandos equivalentes a las diapositivas, desde esta misma carpeta, son:

```sh
mkdir -p out
find src test -name '*.java' -print > out/fuentes.txt
javac -encoding UTF-8 -cp 'lib/*' -d out @out/fuentes.txt
java --enable-native-access=ALL-UNNAMED -cp 'out:lib/*' aplicacion.EjemploSQLite 'Juan de prueba' '00123456'
```

En Mac/Linux el separador del classpath es `:`. En Windows se usa `;`, como en
las diapositivas. `lib/*` incluye el JAR; `out` contiene los .class compilados.
La opcion de acceso nativo evita la advertencia del driver con JDK recientes;
si se usa JDK 17 se puede omitir.

## 4. Ejecutar en IntelliJ

1. Abrir la carpeta `grupo 5` como proyecto Java independiente con JDK 17 o
   superior; o agregar sus raices al modulo que ya usas para el TPO.
2. Marcar `src` como Sources Root y `test` como Test Sources Root desde
   clic derecho -> Mark Directory as.
3. En File -> Project Structure -> Modules -> Dependencies -> + -> JARs or
   Directories, seleccionar `lib/sqlite-jdbc-3.50.3.0.jar` del grupo y aplicar.
4. Abrir `aplicacion.EjemploSQLite` y ejecutar el main.
5. En Run -> Edit Configurations fijar Working directory a la carpeta del
   grupo. En Program arguments poner `"Juan de prueba" 00123456` para insertar,
   o dejar vacio para solo consultar. Con JDK 26 agregar a VM options:
   `--enable-native-access=ALL-UNNAMED`.

La ruta de la base se imprime al ejecutar. Opcionalmente se puede elegir otra
con la opcion de VM `-Dtpo.db=/ruta/absoluta/otra.db`.

## 5. Entender el SQL y el objeto

La tabla tiene `id INTEGER PRIMARY KEY`, `nombre TEXT NOT NULL` y `dni TEXT NOT
NULL`, mas restricciones de texto no vacio y DNI numerico. SQLite genera el ID
cuando no se incluye en el INSERT. DNI sigue siendo texto para conservar ceros
iniciales. No se impone unicidad adicional que no figure en el modelo.

El INSERT usa `VALUES (?, ?)`. `PreparedStatement` asigna nombre y DNI sin
concatenarlos con el SQL, incluso si contienen comillas. `ResultSet` permite
leer las filas devueltas por SELECT. `try (...)` cierra automaticamente todos
los recursos, tambien si ocurre un error.

Para insertar se construye `new Cliente(null, nombre, dni)`. El DAO devuelve una
nueva instancia con el ID generado, porque el ID de Cliente es final. Se debe
conservar esa instancia devuelta para referenciar al cliente persistido.

La inicializacion debe ejecutarse una vez al arrancar, antes de usar el DAO.
Los errores SQL se propagan al main; una futura interfaz debera mostrarlos sin
anunciar exito cuando la operacion fallo.

## 6. Verificar

Luego de compilar con el script, ejecutar:

```sh
java --enable-native-access=ALL-UNNAMED -cp 'out:lib/*' pruebas.PruebasSQLite
java -cp out pruebas.PruebasModelo
```

La prueba SQLite usa una base temporal independiente que elimina al terminar.
Comprueba ID generado, datos al reabrir, ceros iniciales, texto con comillas,
consulta inexistente, rechazo de una insercion con ID existente y claves foraneas.

## Siguiente etapa del TPO

Agregar tablas/DAO de catalogo (incluida la tabla intermedia libro_autor) y un
servicio para ventas. No alcanza con copiar el DAO de Cliente: venta, detalles,
pago y stock deben guardarse en una sola transaccion con la misma Connection.
Un fallo debe deshacer el SQL y recargar o descartar los objetos modificados.
`Venta.confirmar()` no descuenta stock. Ademas hace falta una ruta de
reconstruccion de ventas historicas sin volver a confirmarlas ni recalcular
sus precios. Mantener BigDecimal y representar el dinero sin perdida de precision.
