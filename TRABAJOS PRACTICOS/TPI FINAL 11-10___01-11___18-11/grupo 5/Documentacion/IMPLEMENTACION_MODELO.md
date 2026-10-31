# Modelo del TPO: ventas y devoluciones de libros

## Alcance e inspeccion (31/10/2026)

Se inspeccionaron la estructura, las clases Java existentes, POO.iml, .idea/misc.xml,
.gitignore y el estado de Git. No se encontraron instrucciones AGENTS.md en el
proyecto ni infraestructura Maven, Gradle o JUnit. La herramienta rg no estaba
instalada; se utilizo Get-ChildItem de PowerShell.

La ubicacion identificada es src/TPO, que ya contenia Documentacion y no tenia
clases Java. Las clases Autor y Editorial del parcial pertenecen a otro ejercicio
y no se reutilizaron ni modificaron. No se modificaron TP1, TP2 ni Parcial.
Los documentos anteriores del TPO permanecen intactos; el alcance implementado
es el solicitado para ventas y devoluciones, que reemplaza la propuesta anterior.

El modulo hereda openjdk-26, con nivel de lenguaje JDK_26. Se verifico y utilizo
C:/Users/facub/.jdks/openjdk-26.0.2.1/bin/javac.exe (26.0.2.1).

## Archivos

Todos los archivos siguientes son nuevos salvo POO.iml:

- src/TPO/src/modelo/Autor.java, Editorial.java, Categoria.java: datos de catalogo.
- src/TPO/src/modelo/Libro.java: catalogo, autores, precio y operaciones de stock.
- src/TPO/src/modelo/Cliente.java, Empleado.java: participantes.
- src/TPO/src/modelo/Venta.java: detalles, importes y transiciones.
- src/TPO/src/modelo/DetalleVenta.java: instantanea inmutable de una linea.
- src/TPO/src/modelo/Descuento.java: codigo HOTSALE y porcentaje historico.
- src/TPO/src/modelo/Pago.java: registro inmutable de pago aprobado.
- src/TPO/src/modelo/Devolucion.java: registro inmutable de devolucion total.
- src/TPO/src/modelo/PoliticaDevolucion.java: validacion sin efectos secundarios.
- src/TPO/src/modelo/EstadoVenta.java, MedioPago.java: enumeraciones.
- src/TPO/src/modelo/Validaciones.java: auxiliar interno para reglas comunes.
- src/TPO/src/excepciones/StockInsuficienteException.java y
  DevolucionNoPermitidaException.java: excepciones de dominio no comprobadas.
- src/TPO/test/pruebas/PruebasModelo.java: prueba ejecutable, sin dependencias.
- src/TPO/Documentacion/IMPLEMENTACION_MODELO.md: este registro.
- POO.iml: se agregaron unicamente las raices de fuentes y pruebas del TPO.
  Este archivo ya esta ignorado por Git; la configuracion es local.
- out/tpo-pruebas: clases compiladas, ignoradas por Git.

## Relaciones y responsabilidades

Libro tiene uno o mas Autor, una Editorial y una Categoria.
Venta tiene un Empleado, cero o un Cliente, una lista de DetalleVenta,
cero o un Descuento y cero o un Pago. Cada detalle referencia un Libro y guarda
su cantidad, titulo, ISBN y precio unitario historicos.
Devolucion referencia la Venta original y su Empleado responsable; conserva
importe y medio de reintegro tomados del pago original.
PoliticaDevolucion consulta la venta y las condiciones recibidas.

Venta es Experto para sus importes y Creador de sus detalles. Los detalles no
tienen setters: modificar cantidades reemplaza un detalle inmutable conservando
sus datos historicos. Las listas usan List y ArrayList; los getters entregan
copias defensivas no modificables. Libro copia tambien la lista recibida.

## Decisiones y supuestos

- IDs Long positivos, finales y recibidos por constructor. null representa un
  objeto aun no persistido. No hay contadores ni setters de ID. La futura capa
  de persistencia debera materializar una instancia con el ID devuelto por SQL
  Server y mantener referencias coherentes; la reconstruccion de agregados
  historicos queda para esa etapa. No se puede confirmar una venta rehidratada
  para simular su carga: necesitara una ruta de reconstruccion propia.
- Para acumular el mismo libro se compara su ID no nulo; sin persistencia se
  usa identidad de objeto. Dos libros nuevos distintos no se confunden por
  tener ambos ID null. Si se reciben dos instancias con el mismo ID, se conserva
  la referencia y el precio de la primera incorporacion.
- Dinero BigDecimal, dos decimales, HALF_UP. Se normaliza cada importe al
  ingresarlo; un precio que redondea a cero se rechaza. Los subtotales se
  calculan con el precio unitario normalizado. El descuento se redondea sobre
  el subtotal completo y se resta para obtener el total. Se usa compareTo para
  comparar importes. No se admiten importes negativos.
- HOTSALE es el unico codigo, con mayusculas y 10.00 por ciento inmutable.
  Se toleran espacios externos. No se puede aplicar otro descuento mientras
  haya uno; durante preparacion puede quitarse y volver a aplicarse, sin
  acumulacion.
- Nombre, ISBN, titulo, legajo y motivo deben ser no vacios. ISBN se trata como
  identificador textual sin validar digito verificador. DNI es String de
  digitos, conserva ceros iniciales y no impone longitud ni unicidad.
- El catalogo mantiene titulo e ISBN inmutables; igualmente se copian al
  detalle para preservar el comprobante. Cambiar precio o actividad del libro
  no recalcula ventas anteriores.
- Una venta puede prepararse sin cliente ni pago, y con cantidades que superen
  el stock: la disponibilidad se exige al confirmar, sin reservar unidades.
  Cada cambio exige EN_PREPARACION. Si se modifica la venta despues de registrar
  el pago, confirmar vuelve a exigir coincidencia con el nuevo total.
- Pago solo representa aprobaciones: enEfectivo y conTarjeta son las rutas de
  creacion. Tarjeta guarda exclusivamente una referencia ficticia no vacia.
  Para tarjeta, montoRecibido es null y calcularVuelto devuelve 0.00.
  Un Cliente construido ya garantiza nombre y DNI validos.
- Las fechas LocalDateTime siempre se reciben por parametro; no se consulta el
  reloj. Venta.fecha representa la fecha de compra, suministrada al crearla.
  Si la preparacion atraviesa dias, el futuro coordinador debera definir la
  fecha efectiva de compra antes de persistir; no se registra una segunda
  fecha de confirmacion en esta etapa. No se impone relacion temporal adicional
  entre la fecha del pago y la de venta.
- El plazo compara solo fechas de calendario: desde el dia de compra hasta
  compra + 7 inclusive. Una hora anterior del mismo dia es admisible.
- El empleado de la venta debe estar activo al confirmar; se exige tambien
  responsable activo para completar la devolucion. La confirmacion manual
  aptosParaReventa significa que TODOS los libros estan sin uso y revendibles.
- Se rechazan desbordamientos de cantidades y stock mediante Math.addExact,
  sin modificar el valor anterior.

## Transiciones y limite transaccional

Venta.confirmar valida detalles, empleado, actividad de libros, stock, cliente
para tarjeta y pago exacto, y pasa EN_PREPARACION a CONFIRMADA.
Venta.cancelar pasa EN_PREPARACION a CANCELADA.
No existe un setter publico de estado.

PoliticaDevolucion.validar no cambia estados. Venta.completarDevolucion primero
valida la politica y construye el registro; solo si todo es valido pasa
CONFIRMADA a DEVUELTA. No se puede devolver otra vez ni completar una devolucion
desde preparacion o cancelacion. Devolucion no tiene constructor publico.
El reintegro es el importe pagado (con descuento), nunca el efectivo entregado
ni el total sin descuento. Su medio se deriva del pago y no puede elegirse.

IMPORTANTE: confirmar, cancelar y completarDevolucion NO modifican stock.
El futuro controlador y la persistencia deben coordinar la transicion y los
movimientos de stock en una unica transaccion SQL. Se debera descontar o reponer
exactamente una vez; no agregar movimientos duplicados en modelo y controlador.
Los metodos de Libro son operaciones explicitas independientes.
El modelo actual es en memoria, sin control de concurrencia ni rollback de
objetos ante fallas SQL. La integracion debera resolver validacion concurrente,
rollback/recarga del agregado e idempotencia entre procesos.

## Pruebas ejecutadas y resultado real

Se compilaron exclusivamente las fuentes y pruebas del TPO con OpenJDK
26.0.2.1, UTF-8 y -Xlint:all. Resultado: codigo de salida 0, sin advertencias.
Se ejecuto pruebas.PruebasModelo: codigo de salida 0, salida:

```text
OK: 88 verificaciones del modelo
```

La prueba utiliza controles propios que lanzan AssertionError y no necesita
-ea. Los try/catch estan unicamente en el comprobador de excepciones esperadas
de la prueba; no se agregaron manejos artificiales al modelo.

Cobertura: totales y HOTSALE (10.05 - 1.01 = 9.04), normalizacion HALF_UP,
historial de precio/titulo/ISBN, acumulacion e identidad de libros, cantidades
invalidas y desbordamiento, stock insuficiente, efectivo con vuelto y monto
insuficiente, tarjeta sin cliente y posterior confirmacion con cliente,
pago distinto del total, venta vacia, empleado y libro inactivos, falta de pago,
prohibicion de ediciones y segunda confirmacion, cancelacion, devolucion dentro
del plazo, septimo dia a las 23:59, octavo dia, fecha anterior, motivo vacio,
libros no aptos, responsable ausente, fallo sin cambio de estado, consulta sin
mutacion, devolucion duplicada, reintegro por ambos medios, stock intacto al
confirmar/cancelar/devolver, validaciones basicas y colecciones protegidas.

### Ejecutar desde IntelliJ

1. Abrir el proyecto POO con Project SDK openjdk-26.
2. Las raices locales se agregaron a POO.iml. Si se abre en otra maquina,
   marcar src/TPO/src como Sources Root y src/TPO/test como Test Sources Root
   desde Mark Directory as. No marcar toda src como una unica raiz.
3. Abrir src/TPO/test/pruebas/PruebasModelo.java y ejecutar su main desde el
   triangulo verde. No se requiere JUnit, dependencias ni opciones de assertions.
4. Verificar la salida de las 88 comprobaciones y exit code 0.

### Repetir desde PowerShell en la raiz del proyecto

```powershell
$jdkTpo = 'C:/Users/facub/.jdks/openjdk-26.0.2.1/bin'
$fuentesTpo = @(Get-ChildItem 'src/TPO/src','src/TPO/test' -Recurse -Filter '*.java' |
    ForEach-Object { $_.FullName })
& "$jdkTpo/javac.exe" -encoding UTF-8 -Xlint:all -d 'out/tpo-pruebas' $fuentesTpo
if ($LASTEXITCODE -eq 0) {
    & "$jdkTpo/java.exe" -cp 'out/tpo-pruebas' pruebas.PruebasModelo
}
```

## Pendiente fuera de esta etapa

Pantallas Swing, controladores MVC, procesamiento y rechazo de pagos ficticios,
DAO, conexion SQL Server, esquema y restricciones, generacion SQL de IDs,
rehidratacion de estados e historiales, transacciones y concurrencia del stock,
precarga de empleados y manejo de errores en interfaz y persistencia.
No se implementaron ABM de empleados, permisos, proveedores, compras, papeleria,
devoluciones parciales, cambios ni notas de credito. No se realizaron commits
ni push.
