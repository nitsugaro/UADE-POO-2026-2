# TPO de librería — grupo 5

Aplicación Java Swing con catálogo de libros, administración de clientes, categorías y editoriales, venta y devolución total con historial y stock persistidos en SQLite. Incluye asociación de socios y préstamos. Las cuatro administraciones y los dos recorridos de entrega están implementados con pruebas automatizadas; la verificación interactiva Swing sigue pendiente.

## Abrir y ejecutar en IntelliJ

1. Abrir esta carpeta, **grupo 5**, que contiene src, test, lib y datos.
2. Seleccionar un JDK 17 o superior. Las ejecuciones registradas usaron OpenJDK 26.0.2.1. No se usa Maven, Gradle ni JUnit.
3. Marcar src como Sources Root y test como Test Sources Root. En Project Structure → Modules → Dependencies agregar [lib/sqlite-jdbc-3.50.3.0.jar](lib/sqlite-jdbc-3.50.3.0.jar).
4. Crear una configuración Application con clase [aplicacion.Main](src/aplicacion/Main.java) y classpath de ese módulo.
5. Directorio de trabajo: esta carpeta grupo 5 (no src ni Documentacion). Argumentos del programa: ninguno. Opciones de VM para práctica:

~~~text
-Dtpo.db=datos/practica-companeros.db --enable-native-access=ALL-UNNAMED
~~~

Elegir un nombre de base nuevo para el primer ensayo y mantenerlo al reabrir. Sin tpo.db se usa datos/libreria.db, reservada para el proyecto. Main crea esquema y datos de ejemplo al arrancar; no requiere servidor de base de datos. No activar headless para usar Swing.

Alternativa PowerShell desde esta raíz, con javac/java del mismo JDK en PATH:

~~~powershell
$fuentesTpo = @(Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object FullName)
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 -cp 'lib/*' -d out $fuentesTpo
if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación' }
java --enable-native-access=ALL-UNNAMED '-Dtpo.db=datos/practica-companeros.db' -cp 'out;lib/*' aplicacion.Main
~~~

Si el JDK no está en PATH, usar su ejecutable completo. En el equipo relevado existe C:/Users/facub/.jdks/openjdk-26.0.2.1/bin; esa ruta no es un requisito para otros equipos. Las rutas/entradas de estos comandos se contrastaron con el proyecto; no se ejecutó Main ni se recompiló en esta reorganización.

## Dónde continuar

| Documento | Cuándo leerlo |
|---|---|
| [Estado y pendientes](Documentacion/ESTADO_Y_PENDIENTES.md) | Para decidir qué falta y en qué orden completar la entrega |
| [Guía del TPO](Documentacion/GUIA_TPO.md) | Para estudiar modelo, MVC, SQLite, recorridos y preparar la defensa |
| [Pruebas](Documentacion/PRUEBAS.md) | Para ejecutar controles automáticos y completar los recorridos Swing pendientes |
| [Historial de cambios](Documentacion/HISTORIAL_DE_CAMBIOS.md) | Para consultar decisiones, archivos afectados y resultados de cada iteración |

Para incorporarse al grupo: ejecutar con base de práctica, leer Estado, estudiar los recorridos de la Guía y completar las tablas de Pruebas. La consigna, captura de Teams, UML e informe anteriores se conservan como [fuentes independientes](Documentacion/GUIA_TPO.md#negocio); UML e informe final todavía deben actualizarse.