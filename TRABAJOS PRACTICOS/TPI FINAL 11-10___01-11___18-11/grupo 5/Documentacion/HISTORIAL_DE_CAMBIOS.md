# Historial de cambios y decisiones

[Inicio](../README.md) · [Estado vigente](ESTADO_Y_PENDIENTES.md) · [Guía](GUIA_TPO.md) · [Pruebas](PRUEBAS.md)

Este archivo conserva evidencia de etapas anteriores. **Las afirmaciones de cada registro describen su fecha, no el estado vigente.** Los diagnósticos de clientes sin estado/edición, de cuatro entidades por elegir y de ABM faltantes fueron superados por las implementaciones del 18/11. No deben convertirse nuevamente en tareas. Los resultados son antecedentes registrados, no ejecuciones nuevas del 19/11.

## Cronología y precedencia

| Etapa | Decisión o resultado | Vigencia |
|---|---|---|
| 31/10/2026, modelo | Ventas y devoluciones reemplazan el alcance comercial anterior; reglas puras, 88 controles | Reglas integradas en la guía; rutas src/TPO y SQL Server futuro superados |
| Integración MVC/SQLite, anterior al relevamiento del 17/11 | Swing, servicio, DAO, transacciones e historial | Implementada; explicación actual en la guía |
| 17–18/11, diagnóstico inicial | Inventario y candidatas ABM; ensayo de cuatro operaciones desde servicio | Superado en faltantes de Categorías, Editoriales y Clientes; no fue ensayo Swing |
| 18/11, Categorías | Baja física solo sin referencias, incluso de libros inactivos | Implementada |
| 18/11, Editoriales | Misma regla; comprobación de referencias dentro de transacción | Implementada; elección Libros/Clientes/Categorías/Editoriales resuelta |
| 18/11, Clientes: estado | Baja lógica con préstamo abierto, devolución habilitada, reactivación por ID | Implementada |
| 18/11, Clientes: edición | Actualización por ID, DNI libre al cambiarlo, duplicados históricos preservados | Implementada |
| 18/11, Clientes: alta | Alta administrativa y unicidad transaccional común; asociación sin ambigüedad | Completa las operaciones de Clientes |
| 18/11, recorrido interactivo | Bloqueo al enumerar ventanas, antes de iniciar Main | Pendiente; registro y plan en PRUEBAS |
| 19/11, reorganización documental | Cinco documentos, sin cambios funcionales | Registro al final de este archivo |

<a id="modelo-20261031"></a>
## 31/10/2026 — contexto y ejecución del modelo

Las rutas y comandos de esta sección son históricos. Para ejecutar hoy usar [README](../README.md) y [PRUEBAS](PRUEBAS.md#automatizadas). Las decisiones de modelo se consolidaron y actualizaron en [GUIA: modelo](GUIA_TPO.md#modelo).
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


<a id="diagnostico-20261118"></a>
## 17–18/11/2026 — diagnóstico y registros de implementaciones

> Archivo histórico: desde aquí las afirmaciones se interpretan en el contexto de su etapa; prevalece la cronología anterior.

## Referencia y criterio de planificación

Diagnóstico actualizado al 18/11/2026 (lectura iniciada el 17/11). Rama `tpo-grupo5`, commit `67d2d31df9673c47a58e12fdb3d82baef67eba1c`. Al iniciar había únicamente una modificación local en este Markdown, que contenía la prueba de las cuatro operaciones ABM. Se conserva su evidencia y se actualiza su interpretación. No hay cambios locales en código. Este diagnóstico inicial solo editó este archivo; la iteración de Categorías posterior se detalla al final.

Fuentes: [TP_Final_Consignas.docx](TP_Final_Consignas.docx), checklist proporcionado por el grupo y código actual. [GUIA_TPO.md](GUIA_TPO.md) y pruebas anteriores son antecedentes. El PDF comercial y el UML anterior no imponen funcionalidades: no se exige implementar compras, proveedores, papelería ni roles por aparecer allí.

El DOCX dice: **«6 interfaces Swing (4 de alta/baja/modificar y 2 de negocio/funcionales)»**. El checklist actual explicita **alta, baja, modificación y consulta**. Ninguno define inequívocamente una entidad por interfaz; se adopta **como criterio de planificación del grupo** una administración completa por entidad. La aceptación docente de ese cómputo queda pendiente, especialmente para socios como asociación independiente.

Las cuatro operaciones anteriores —alta libro, modificación libro, alta cliente y alta socio— no acreditan cuatro ABM por entidad: libro se contaba dos veces y clientes/socios no tienen todas las operaciones. La elección de las cuatro entidades queda al grupo.

Estados: **implementado** = comportamiento presente con evidencia; **parcial** = partes presentes y faltantes identificadas; **faltante** = no encontrado; **pendiente de verificación** = falta comprobar o decidir, no necesariamente programar. Implementado no significa aprobado por la cátedra ni probado manualmente.

## Matriz del checklist completo

| Requisito | Estado | Qué existe y evidencia | Qué falta concretamente | Comprobación de cierre |
|---|---|---|---|---|
| Java funcional que resuelva el negocio elegido | Implementado; UI pendiente de verificación | Main conecta navegación/servicio; venta, pago, devolución y préstamos tienen listeners y persistencia; pruebas previas aprobadas | Ensayo integral desde Swing; ABM faltantes se cuentan abajo, no se agregan negocios del PDF histórico | Venta efectivo/tarjeta, devolución y préstamo en base temporal; stock, estado y reapertura correctos |
| Diagrama final actualizado, mínimo 12 clases | Parcial | SVG histórico; src supera 12 clases propias | Diagrama de la implementación final, relaciones y estados actuales | Contrastar clases/métodos/relaciones con src y revisar mínimo de 12 |
| Tres patrones o buenas prácticas vistas en clase e implementadas | Implementado; selección docente pendiente de verificación | Experto: Venta/DetalleVenta; Creador: Venta.agregarLibro; MVC/separación de responsabilidades | Preparar explicación y cotejar selección con material de clase; no hay que inventar patrones nuevos | Mostrar tres colaboraciones concretas y el problema que resuelve cada una |
| Excepción personalizada y tres try/catch | Implementado | Dos excepciones de negocio; bloques independientes en PagoControlador, CatalogoControlador y DevolucionControlador | Mensajes y recuperación visibles pendientes de prueba | Provocar pago insuficiente, ISBN duplicado y devolución inválida sin cambios parciales |
| Seis interfaces Swing completas: cuatro administrativas y dos de negocio | Parcial | Libros tiene A/B/M/C conectadas; clientes y socios parciales. Venta y devolución son dos recorridos funcionales; préstamos es adicional | Elegir cuatro entidades, completar administraciones elegidas y verificar UI | A/B/M/C por cada entidad elegida, persistencia y reinicio; dos recorridos de negocio enteros |
| Persistencia en archivos o BD | Implementado | ConexionSQLite, ClienteDAO, LibreriaDAO; pruebas de reapertura y rollback en bases temporales | Ensayar base/paquete final; futuras operaciones deben guardar y migrar esquema si corresponde | Reabrir después de guardar y verificar datos; fallo transaccional no deja escrituras parciales |
| Tres usos de diálogos | Implementado | Formularios libro/socio, cancelar venta, confirmar devolución y cierre con borrador | Prueba visual de aceptar/cancelar; llamadas conectadas por lectura | Abrir tres diálogos por sus rutas y verificar ambos resultados |
| Informe final ~10 páginas con propuesta, UML, estructura y capturas | Parcial | PDF histórico, guía de estudio, SQLite y notas modelo/MVC | Falta informe final vigente, nuevo UML y capturas reales finales | PDF/DOC legible de ~10 páginas con las cuatro partes y correspondencia con versión entregada |

## Inventario ABM por entidad

**Swing** significa acceso conectado por lectura, no prueba interactiva. **Servicio/DAO** indica operación sin pantalla administrativa. Consulta de selección no equivale por sí sola a consulta administrativa.

| Entidad | Alta | Baja | Modificación | Consulta | Acceso desde Swing | Persistencia | Qué falta para completar su ABM |
|---|---|---|---|---|---|---|---|
| Libros | Swing: Nuevo libro | Swing: editar, desmarcar activo; lógica | Swing: Editar libro | Administrativa: catálogo con búsqueda, stock/precio/activo e inactivos | Inicio → Administrar libros → Nuevo / seleccionar → Editar | libros, libro_autor y referencias; servicio.guardarLibro → DAO.guardarLibro | No se detectó operación ausente; verificar las cuatro desde UI, especialmente baja/reactivación |
| Clientes | Swing dentro de venta; también al asociar DNI nuevo con nombre | Falta; sin activo | Falta; nombre/DNI finales; DAO solo inserta nuevos | Selector nombre/DNI en venta; servicio.clientes y DAO.listar/buscarPorId; no administración propia | Inicio → Nueva venta → Nuevo cliente / Buscar cliente | clientes(id,nombre,dni), ClienteDAO | Administración/consulta independiente, modificación, baja y políticas de identidad/historia |
| Socios (asociación) | Swing: Hacerse socio | Falta; sin estado | Falta; no tiene datos propios editables | Padrón de selección en préstamos; servicio.socios/DAO.listarSocios; no administración | Inicio → Hacerse socio / Préstamos y devoluciones | socios(cliente_id), PK/FK a clientes; no clase Socio | Definir modificación del vínculo, baja/reactivación y administración propia |
| Empleados | Solo carga inicial DAO; sin alta administrativa | Modelo activar/desactivar, sin flujo persistido administrativo | Falta nombre/legajo; estado solo en modelo | servicio.empleados; listarEmpleados privado al cargar; responsable visible en historial | Sin administración; primer empleado activo automático | empleados(id,legajo UNIQUE,nombre,activo), LibreriaDAO | Alta/edición/baja persistidas, consulta/pantalla y política último empleado activo |
| Autores | Indirecta desde libro; DAO.buscarOCrear | Falta; quitar autor del libro borra vínculo, no autor | Cambiar texto del libro selecciona/crea autor, no renombra entidad | Nombres en catálogo/formulario; no listado administrativo | Formulario libro, autores separados por `;` | autores y libro_autor; sin AutorDAO | API/pantalla propias, renombrado global y baja de autores vinculados |
| Editoriales | Swing: Nueva editorial, también creación desde libro | Swing: eliminación confirmada solo sin referencias, incluidos inactivos; DAO revalida | Swing: renombrar preservando ID y nombres de libros en sesión | Listado administrativo completo, incluso sin libros | Inicio → Administrar editoriales | LibreriaDAO.listarEditoriales/guardarEditorial/eliminarEditorial; esquema y FK existentes | Implementado; pendiente interacción Swing (ver registro de Editoriales) |
| Categorías | Swing: Nueva categoría; también desde libro | Swing: eliminar solo sin referencias, incluidos libros inactivos; DAO revalida | Swing: renombrar conservando ID; actualiza libros en sesión | Administración propia, incluye categorías sin libros | Inicio → Administrar categorías | LibreriaDAO.listarCategorias/guardarCategoria/eliminarCategoria; tabla y FK existentes | Implementado; pendiente ensayo interactivo de confirmación/cancelación y catálogo (ver registro de iteración) |

Las demás entidades existentes son transaccionales: Venta permite crear/editar borrador, cancelar y consultar historial; Pago registra cobro, Devolucion registra reintegro, Prestamo registra movimiento y devolución. No tienen ABM libre y no se propone contar dos veces sus pantallas como administración y negocio. DetalleVenta pertenece a Venta, Descuento tiene HOTSALE fijo, los estados/medios son enums y Validaciones/PoliticaDevolucion son auxiliares. No se encontraron Proveedor, Compra, Producto ni Persona: no son candidatas ya implementadas.

### Libros y evidencia de sus cuatro operaciones

[CatalogoControlador.editar](../src/controlador/CatalogoControlador.java) instancia [LibroFormulario](../src/vista/LibroFormulario.java): `editar(null)` abre «Nuevo libro» vacío y `editar(libro)` abre «Editar libro» precargado. Comparten clase/método y pertenecen a una administración. Ambos llaman [LibreriaServicio.guardarLibro](../src/servicio/LibreriaServicio.java) → [LibreriaDAO.guardarLibro](../src/persistencia/LibreriaDAO.java); después se actualiza el objeto Libro o se agrega el nuevo.

La baja es `activo=false` dentro de edición, no DELETE. Catálogo muestra inactivos y permite reactivarlos. Venta filtra activos; préstamo exige `Libro.hayDisponibilidad(1)`, que incluye actividad. **Ninguna de A/B/M/C fue probada interactuando con Swing**. Alta/modificación se ejecutaron por servicio/SQLite. Las pruebas generales anteriores verificaron actividad y persistencia del libro inactivo; la prueba ABM acotada mantuvo el libro activo para prestar. Consulta se verificó por código y datos del servicio, no por clics/búsqueda del usuario.

### Clientes, socios y consecuencias para la historia

[Cliente](../src/modelo/Cliente.java) tiene id, nombre y dni finales; no apellido separado/contacto/activo. [ClienteDAO.guardar](../src/persistencia/ClienteDAO.java) exige ID null y solo INSERT. DNI conserva ceros, pero no tiene UNIQUE. `LibreriaDAO.asociar` busca primer ID por DNI: duplicados pueden volver ambigua la identidad.

Socios tiene solo `cliente_id`: sin nombre/DNI propios, número de socio, fecha de alta ni activo. No hay clase Socio: listarSocios devuelve Cliente mediante JOIN. Asociar con DNI existente y nombre vacío crea vínculo sin duplicar cliente; DNI nuevo requiere nombre y crea ambos. Repetir asociación se rechaza. Modificar nombre/DNI desde una futura pantalla de socios modificaría Cliente, no otra persona.

Ventas tiene cliente_id opcional (tarjeta exige cliente); prestamos.socio_id referencia socios(cliente_id). `LibreriaDAO.listarPrestamos` lee nombre/DNI actuales de clientes y título actual de libro por JOIN: **no son instantáneas históricas**. Las ventas se rehidratan con referencias a cliente; DetalleVenta sí guarda título/ISBN/precio históricos, pero no nombre/DNI de cliente. Al modificar identidad se debe decidir entre mostrar datos actuales o agregar instantáneas. Reemplazar Cliente inmutable en memoria sin actualizar referencias puede dejar borradores/ventas mostrando datos anteriores hasta recarga.

Borrar físicamente cliente con ventas/asociación o socio con préstamos compromete claves foráneas e historia. Una baja lógica futura requiere estado, migración, filtros de selección y validación en servicio, sin borrar registros. Decidir: ¿baja cliente también suspende socio?, ¿baja socio permite comprar?, ¿se bloquea baja con préstamos abiertos o se permite manteniendo devolución?, ¿se reactiva? El historial y la resolución de préstamos existentes deben conservarse según la política elegida.

### Candidatas adicionales: opciones, no obligaciones acumulativas

| Candidata | Modelo/tabla/acceso existente | Qué agregar | Operaciones afectadas y decisión |
|---|---|---|---|
| Empleado | Modelo con activo, tabla y legajo único, carga y colección de servicio | CRUD persistido, consulta y vista/controlador; actualización de objetos | Responsable de ventas/devoluciones. ¿Se selecciona empleado o sigue primero activo? ¿Permitir baja del último? No obliga a agregar roles |
| Editorial | Modelo id/nombre, tabla, FK y buscarOCrear | Lista/alta propias, renombrar por ID, baja y UI; adaptar selección de libros | Varios libros comparten ID. ¿Rechazar baja usada, reasignar o inactivar para nuevas selecciones? Renombrar no debe crear otra fila |
| Categoría | Modelo id/nombre, tabla, FK y creación por texto | Igual conjunto administrativo que editorial | Clasificación de libros. ¿Baja usada requiere reasignar? ¿Nombres únicos? |
| Autor | Modelo/tabla y relación múltiple libro_autor | Administración propia, renombrado global, baja y selección | Libro exige al menos un autor; quitar vínculo no elimina entidad. ¿Cómo evitar libro sin autores? |

Editorial/categoría tienen estructuras menores, pero no ABM existente. Empleado ya tiene activo, con impacto en responsables. Autor tiene relación múltiple. Socios exige definir qué se modifica antes de considerarlo ABM separado; no se justifica inventar atributos solo para contar pantallas. La combinación final la decide el grupo.

## Evidencia técnica proporcionada a cada requisito

### Prácticas implementadas

| Práctica | Código concreto | Problema que resuelve |
|---|---|---|
| GRASP Experto | [DetalleVenta.calcularSubtotal](../src/modelo/DetalleVenta.java); [Venta.calcularSubtotal/calcularDescuento/calcularTotal](../src/modelo/Venta.java) | Calcula donde se conocen cantidades, precios y detalles; evita repetir fórmulas en Swing |
| GRASP Creador | Venta.agregarLibro crea DetalleVenta; conCantidad reemplaza detalles al acumular | Mantiene composición y reglas del detalle dentro de la venta |
| MVC y separación de responsabilidades | [VentaVista](../src/vista/VentaVista.java) → [VentaControlador](../src/controlador/VentaControlador.java) → Venta/servicio → DAO | Separa presentación, eventos, reglas y SQL; permite probar reglas sin abrir ventanas |

Listas defensivas, BigDecimal y transacciones aportan otras buenas prácticas. Experto/Creador/SRP ya aparecían en el material histórico, pero no se dispone de un catálogo completo de contenidos vistos en clase: confirmar cuáles tres se presentarán. Los DAO realizan acceso/reconstrucción de datos; **no hay interfaz Repository propia ni repositorio intercambiable inyectado**. No presentar ese nombre como clase existente.

### Excepciones y tres bloques independientes

[StockInsuficienteException](../src/excepciones/StockInsuficienteException.java) y [DevolucionNoPermitidaException](../src/excepciones/DevolucionNoPermitidaException.java) extienden RuntimeException y representan reglas del negocio.

| Bloque concreto | Qué protege y cómo maneja el error | Prueba de aceptación visible |
|---|---|---|
| [PagoControlador.confirmar](../src/controlador/PagoControlador.java) | try crea pago, llama servicio.confirmar y muestra detalle; catch IllegalArgumentException, IllegalStateException y StockInsuficienteException escribe el mensaje en vista.error | Efectivo insuficiente no confirma ni descuenta stock; muestra error y permite corregir |
| [CatalogoControlador.editar](../src/controlador/CatalogoControlador.java) | try confirma spinner y guarda; catch ParseException informa stock inválido; catch IllegalArgumentException/IllegalStateException muestra error | ISBN duplicado no crea registro; informa error y mantiene formulario |
| [DevolucionControlador.confirmarDevolucion](../src/controlador/DevolucionControlador.java) | try confirma diálogo y llama servicio.devolver; catch incluye DevolucionNoPermitidaException y ArithmeticException | Motivo vacío o no aptos no cambia venta/stock y muestra error |

Son tres sitios try/catch independientes, no tres tipos de excepción en un solo catch. ConexionSQLite.transaccion también hace rollback ante SQLException o RuntimeException. Los errores se probaron a nivel de reglas/servicio; falta observar estos mensajes desde UI.

### Diálogos y rutas de entrada

| Uso | Acción que lo abre | Código |
|---|---|---|
| Alta/edición libro | Inicio → Administrar libros → Nuevo libro / Editar libro | CatalogoControlador.editar → JOptionPane.showConfirmDialog con LibroFormulario |
| Alta socio | Inicio → Hacerse socio | [InicioControlador.asociar](../src/controlador/InicioControlador.java) → showConfirmDialog con FormularioPanel |
| Cancelación | Inicio → Nueva venta → Cancelar | VentaControlador.cancelarVenta → showConfirmDialog YES/NO |
| Devolución total | Historial de ventas → detalle de confirmada → Devolver → Confirmar | DevolucionControlador.confirmarDevolucion → showConfirmDialog YES/NO |
| Cierre con borrador | Cerrar ventana con venta en preparación | [VentanaPrincipal.confirmarCierre](../src/vista/VentanaPrincipal.java) → showConfirmDialog |

También hay showMessageDialog en VentanaPrincipal.error/informar. Estas rutas se verificaron por lectura; no se contabilizan como pruebas interactivas.

### Dos recorridos funcionales presentes

Venta: Inicio → Nueva venta → agregar libro → Pagar → confirmar → resumen. VentaControlador administra el carrito; PagoControlador llama servicio.confirmar; Venta valida y DAO.guardarVenta guarda venta/detalles/pago y stock en transacción. Efectivo admite cliente ausente; tarjeta exige cliente y referencia simulada, no pago bancario real.

Devolución: Inicio → Historial de ventas → detalle de confirmada → Devolver → motivo/aptitud → confirmar. DevolucionControlador llama servicio.devolver; PoliticaDevolucion valida siete días, estado y aptitud; DAO.guardarDevolucion registra reintegro y stock. Préstamos y devoluciones de préstamos aporta otro recorrido, mediante PrestamoControlador. Por lectura no se encontró corte entre esos botones y el servicio; la falta de ensayo Swing se registra como verificación pendiente, no como funcionalidad faltante.

## Comparación del UML e insumos del informe

El [SVG original](UML%20-%20TPO.svg) contiene Persona, Producto, ArticuloPapeleria, Proveedor, Compra, DetalleCompra, RolEmpleado y SistemaLibreria, ausentes de src. Presenta herencia de Libro desde Producto y de participantes desde Persona; el código actual usa clases independientes. La coordinación real está en LibreriaServicio, Navegacion y controladores. Esas diferencias requieren actualizar el artefacto, no implementar lo histórico.

También cambian autor textual por lista de objetos Autor, cliente obligatorio por cliente opcional en efectivo y boolean confirmada por EstadoVenta con preparación/confirmada/cancelada/devuelta. Faltan en el diagrama actual Descuento, PoliticaDevolucion, Devolucion, Prestamo y la arquitectura MVC/persistencia según el nivel elegido. Pago contiene medio, recibido/vuelto y referencia ficticia. Ya existen doce clases de dominio sin contar enums: Autor, Categoria, Editorial, Libro, Cliente, Empleado, Venta, DetalleVenta, Pago, Descuento, Devolucion y PoliticaDevolucion. El mínimo cuantitativo no es el impedimento.

Se inspeccionaron los textos del SVG, no cada flecha visualmente. El nuevo diagrama deberá revisar multiplicidades/composición/dependencias y coincidir con las entidades finalmente elegidas.

Material disponible para el informe: PDF histórico, GUIA_TPO.md con paquetes/índice/recorridos/preguntas, SQLITE.md con configuración y pruebas, notas IMPLEMENTACION_MODELO.md e INTERFAZ_MVC.md sobre evolución. Faltan propuesta vigente de la versión final, UML actualizado, capturas reales de administración y negocio y resultados manuales. En Documentacion no se encontró un informe final alternativo ni capturas de la aplicación: Consigna Teams.png es una consigna. Después de estabilizar el código podrá prepararse el PDF/DOC de aproximadamente diez páginas. Aquí solo se identifican insumos.

## Pendientes para decidir y ejecutar de a uno

A2–A4 son condicionales a las entidades elegidas: no se deben implementar todas las candidatas por defecto. Cada fila separa ausencia de operación, decisión previa y aceptación observable.

| ID / requisito | Falta y punto exacto de corte | Archivos o métodos | Dependencias y pregunta al grupo | Prueba concreta de aceptación |
|---|---|---|---|---|
| A1 / cuatro administraciones | Solo libros ofrece A/B/M/C; clientes/socios terminan en altas y selección; no hay cuatro administraciones completas | InicioVista/InicioControlador, Navegacion; inventario anterior | Elegir cuatro entidades. ¿Socios cuenta separado y qué significa modificarlo? | Lista aprobada de cuatro entidades, cada una con A/B/M/C y reglas explícitas. No requiere código todavía |
| A2a / clientes, si se elige | ClienteControlador solo crea/asigna/selecciona; no acceso administrativo ni edición. ClienteDAO.guardar rechaza ID existente | ClienteVista/Controlador, Cliente.java, ClienteDAO, LibreriaServicio | A1; decidir DNI editable/único y datos históricos actuales vs instantánea | Desde administración modificar nombre/DNI manteniendo ID; reabrir y ver cambios; ventas/préstamos conservan vínculos y muestran la política elegida |
| A2b / baja cliente | No existe activo en modelo/tabla, baja DAO/servicio ni filtro de inactividad | Cliente.java, esquema clientes, ClienteDAO.listar, servicio.asociar/confirmar/prestar, Venta.validarConfirmacion, ClienteControlador | A2a y reglas cliente/socio; migrar bases previas. ¿Baja cliente suspende asociación? | Dar de baja con venta/préstamo históricos; reabrir: aparece inactivo en administración, no es seleccionable/aceptable para operaciones nuevas según regla; historia y devolución de préstamos conservadas |
| A3 / socios, si se elige | InicioControlador.asociar solo agrega; PrestamoControlador muestra selector/prestar/devolver; no modificación/baja del vínculo | socios/prestamos en ConexionSQLite; DAO.asociar/listarSocios/prestar/devolverPrestamo; servicio e InicioControlador | A1; datos personales dependen de Cliente. ¿Qué se modifica del vínculo? ¿Baja con préstamos abiertos se bloquea o admite? ¿Reactivación? | Alta no duplica cliente; modificación propia cumple regla definida; baja persiste y bloquea nuevos préstamos, conservando historial y resolución de abiertos; reactivación según decisión |
| A4 / entidad adicional elegida | Empleado/Editorial/Categoria/Autor no tienen pantalla administrativa ni API completa; carga inicial/buscarOCrear no completan el flujo | Modelos respectivos, LibreriaDAO.listarEmpleados/buscarOCrear/guardarLibro, esquema, Inicio/Navegacion | A1; elegir candidata y regla de baja de registros usados, unicidad y migración | A/B/M/C desde su pantalla; reabrir conserva ID/cambios; no rompe libro/venta relacionado; baja rechazada o inactividad aplicada según política, sin perder historia |
| V1 / libros | No se detecta CRUD faltante; falta evidencia de interacción para A/B/M/C | CatalogoControlador.editar/actualizar, servicio.guardarLibro, Libro.actualizarDesde/hayDisponibilidad | Puede verificarse antes de ampliar entidades | Crear, consultar, editar, inactivar, reabrir, consultar inactivo; no aparece para nueva venta/préstamo; historia conserva detalles; reactivar recupera disponibilidad |
| V2 / negocio, errores y dialogs | Servicio probado no acredita foco, selección, mensajes o cancelación de UI | Main, Venta/Pago/Devolucion/PrestamoControlador, VentanaPrincipal | Control de ventanas disponible y BD temporal; repetir tras cambios relevantes | Venta efectivo/tarjeta, devolución y préstamo; aceptar/cancelar tres dialogs y provocar tres errores sin efectos parciales; reapertura consistente |
| D1 / UML final | Artefacto desactualizado; no faltan clases para llegar a doce | UML - TPO.svg frente a src | A1 y modelo terminado | Nuevo diagrama con 12 o más clases propias reales, relaciones/estados correctos; no inventar clases ausentes |
| D2 / informe final | No existe documento vigente que reúna todos los insumos | PDF histórico, guía, SQLite, futuro UML/capturas | D1 y V1/V2, alcance estable | PDF/DOC ~10 páginas con propuesta, UML, estructura y capturas legibles de versión final |
| V3 / formato de entrega de fuente y datos | No se ensayó paquete final desde carpeta limpia | lib/sqlite-jdbc-3.50.3.0.jar, datos, Main, ejecutar-sqlite.sh, SQLITE.md | Implementación final y base de demostración elegida | Compilar y abrir con instrucciones sin configuración IDE particular, guardar/reabrir; driver y datos incluidos. El script ejecuta EjemploSQLite, no Main |

Orden sugerido: **A1 → completar una entidad elegida por vez (A2/A3/A4 según selección) → V1/V2 → D1 → D2 → V3**. V1 puede adelantarse para detectar problemas visuales. No hace falta reescribir prácticas, excepciones o diálogos ya presentes para aumentar contadores.

Ejemplos de instrucciones futuras, a enviar de a una:

1. «Comparemos las candidatas y definamos las cuatro entidades y sus reglas, sin implementar todavía».
2. «Implementá solo el ABM de [entidad] con [reglas decididas], incluida persistencia y la aceptación de la fila [ID]».
3. «Verificá desde Swing los recorridos [V1/V2] con base temporal y registrá resultados».
4. Una vez estable el código: «Actualizá el UML»; luego «Prepará el informe final con los insumos verificados».

## Qué se verificó y qué evidencia se reutiliza

### Lectura de código y comparación actual

Se releyeron consigna DOCX mediante XML, textos del SVG, modelos de entidades candidatas, esquema y DAO, servicio, navegación/listeners, bloques de error y acciones de diálogos. Se contrastaron guía y diagnóstico local anterior. La comparación `git diff ad55cbc2d74e5e697c6f42335bf05c90e75d3fc3 HEAD --stat -- src test ejecutar-sqlite.sh` no mostró diferencias. Las fuentes de las pruebas y funcionalidades relevantes siguen iguales; por eso se conserva la evidencia previa sin repetir ejecuciones ni atribuirles fecha nueva.

### Probado desde modelo y servicio/SQLite

Resultados del relevamiento anterior del 17/11/2026, OpenJDK 26.0.2.1, compilación UTF-8 con el driver de lib:

| Prueba anterior | Resultado | Alcance real |
|---|---|---|
| pruebas.PruebasModelo | Código 0, 88 verificaciones | Dinero, estados, validaciones, devolución y colecciones; no UI |
| pruebas.PruebasSQLite | Código 0, 9 verificaciones | DAO/clientes con base temporal; no UI |
| pruebas.PruebasServicio | Código 0, 86 verificaciones | Operaciones, reapertura, stock, rollback por conflicto, socios/préstamos y edición; no UI |
| pruebas.PruebasVistas | Código 0, 9 vistas en tamaños inicial/ampliado; pestañas/formulario | Distribución de componentes con headless=true; no clics, dialogs ni revisión visual completa |

Comandos del antecedente, con directorio auxiliar temporal:

```text
javac -encoding UTF-8 -cp "lib/*" -d DIR_TEMP [fuentes src y test]
java --enable-native-access=ALL-UNNAMED -Djava.awt.headless=true -Djava.io.tmpdir=DIR_TEMP -cp "DIR_TEMP;lib/*" pruebas.NOMBRE
```

SQLite/Servicio usan Files.createTempDirectory y borran sus bases en finally; no escribieron datos/libreria.db. Un primer intento de compilar con -Xlint:all en TEMP falló al escribir una clase y mostró 13 warnings serialVersionUID de vistas. Luego se compiló y ejecutó en directorio permitido. No se afirma que una compilación completa con lint carezca de advertencias.

### Prueba anterior acotada de cuatro operaciones

Se usó `VerificarABM`, comprobador temporal compilado junto a src fuera del proyecto, y base nueva `abm-verificacion-020ea62f-dd9f-47bd-ae32-cd15162d01de/abm.db` en el workspace auxiliar de Codex. Compilación conjunta y ejecución terminaron con código 0 y ocho comprobaciones OK; un intento previo de compilar solo el comprobador no resolvió los paquetes. Se eliminaron base, clases y comprobador; no se agregó test permanente.

| Secuencia | Resultado observado por servicio/SQLite |
|---|---|
| guardarLibro(null, ...) | «Libro prueba ABM», ISBN ABM-20261117, precio 123.45, stock 3, activo; catálogo +1 |
| guardarLibro(libro, ...) | «Libro ABM editado», precio 150.50, stock 4; mismo ID y sin duplicado; reapertura conservó cambios |
| crearCliente | «Cliente prueba ABM», DNI 00987654321; clientes +1 y todavía ausente de socios |
| asociar con nombre vacío y ese DNI | Mismo ID de cliente, sin duplicarlo, visible en socios(); reapertura conservó socio/DNI |
| prestar al socio | Préstamo registrado y stock 4 → 3; SQL confirmó exactamente un cliente y una asociación para el DNI |

Esto acredita cuatro operaciones y habilitación del socio para préstamo, **no cuatro entidades administradas ni interacción Swing**. No probó baja de cliente/socio porque no existe.

Las rutas de esas operaciones se verificaron por lectura: Inicio → Administrar libros → Nuevo libro; catálogo → seleccionar → Editar libro; Inicio → Nueva venta → Nuevo cliente → Guardar y usar en la venta; tras cancelar borrador para volver a Inicio → Hacerse socio con ese DNI → Préstamos y devoluciones. ClienteControlador asigna además el nuevo cliente al carrito; ese paso visual no se ejecutó en el comprobador del servicio.

### Probado desde Swing

**No hay recorridos interactivos acreditados.** El intento anterior de control de ventanas devolvió `Computer Use native pipe is unavailable` (error 2). No se reintentó en esta pasada; no se interpreta ese error como defecto del programa. Quedan por ejecutar A/B/M/C de libros, alta cliente/asociación observando el padrón, dos recorridos de negocio, errores y diálogos. La prueba headless de paneles no sustituye esas verificaciones. La base real se conservó.

## Correcciones respecto de los antecedentes

- El commit actual sustituye al antiguo como referencia del diagnóstico; los resultados de pruebas conservan fecha y contexto anteriores.
- «Cuatro operaciones elegidas» no significa cuatro administraciones por entidad. Libro se contaba dos veces y clientes/socios no tenían ABM completos. Este cambio es de criterio de planificación, no un fallo nuevo del código.
- Decir que solo faltaba «acreditar cuatro interfaces» era demasiado general: se confirman ausencias de edición/baja de cliente y administración de socios, no simplemente nombres de pantallas.
- Listar clientes para elegir en una venta y mostrar socios para prestar no acredita una consulta administrativa completa.
- Socio no tiene atributos personales propios ni clase propia: es un vínculo. Antes de pedir modificación se debe definir qué cambiaría y si justifica contarlo aparte.
- Historial de préstamo usa textos actuales por JOIN; no tiene la misma instantánea histórica que DetalleVenta. Esto importa al planificar edición de personas y libros.
- No se transforma evidencia de servicio/SQLite o de geometría en «probado desde Swing». Tampoco se convierten las clases del PDF/UML histórico en funciones obligatorias.

## Cierre para decidir el siguiente paso

### Faltantes confirmados

- Cuatro administraciones completas según el criterio por entidad: solo libros tiene A/B/M/C conectadas. Clientes carece de edición, baja y administración/consulta independiente; socios de administración propia, modificación definida y baja.
- Administración completa de la candidata adicional que se elija: ninguna de Empleado/Editorial/Categoria/Autor la tiene hoy. No implica implementar todas.
- UML final actualizado e informe vigente con propuesta, estructura y capturas finales.

### Verificaciones pendientes

- A/B/M/C de libros y recorridos funcionales desde Swing, mensajes de error, selección y al menos tres diálogos.
- Cómputo de interfaces y elección de prácticas admitidas por la cátedra; ensayo del paquete limpio y base final.
- Tras futuras implementaciones: reapertura, bajas y conservación de historia con las reglas elegidas.

### Decisiones que debe tomar el grupo

- Cuáles serán las cuatro entidades; si socios será una administración independiente y qué se modificaría del vínculo.
- Efectos de bajas de cliente/socio, préstamos abiertos y reactivación; no borrar historia ni bloquear su resolución accidentalmente.
- DNI único/editable, identidad histórica actual o instantánea y política de baja de registros referenciados de la candidata adicional.

La próxima instrucción más útil es resolver las decisiones de A1 y de una entidad concreta; después pedir su implementación y prueba de aceptación sin mezclar UML ni informe.



<a id="categorias"></a>
## Iteración implementada: ABM de Categorías (18/11/2026)

**Estado: implementado; verificación interactiva Swing pendiente.** Ruta: **Inicio → Administrar categorías**. Lista ID/nombre de todas las categorías, incluidas las que no tienen libros, y ofrece Nueva categoría, Modificar, Eliminar e Inicio. La selección usa conversión de fila visual a modelo para respetar el ordenamiento.

### Reglas e integración

- Nombre obligatorio, sin espacios externos. Se conserva el criterio del catálogo: igualdad exacta SQLite (`nombre = ?`), sensible a mayúsculas y acentos; la búsqueda visual normalizada no es la regla de identidad. Alta/renombrado duplicados se rechazan; el formulario de libros reutiliza la categoría existente por ese mismo nombre.
- Renombrar conserva ID y relaciones. Después del commit, el servicio actualiza todas las instancias de Categoria referenciadas por libros cargados, además de la instancia editada. Al volver al catálogo aparece el nombre nuevo en la misma sesión y al reabrir se carga desde SQLite.
- Baja física solo si **ningún libro**, activo o inactivo, referencia el ID. La UI consulta primero; si está usada muestra «Esta categoría está asignada a uno o más libros. Reasignalos antes de eliminarla». Si está libre pide confirmación; una respuesta distinta de Sí retorna sin ejecutar la baja.
- DAO.eliminarCategoria toma el bloqueo de escritura de la transacción, vuelve a comprobar referencias y elimina condicionalmente por ID/nombre. Una referencia agregada después de la prevalidación impide eliminar; no basta con el control visual. Se mantienen claves foráneas, registros y vínculos existentes: no hay migración, limpieza ni recreación de tablas.
- LibroFormulario sigue admitiendo texto para categoría. Cambiar ese texto reasigna el libro a una categoría existente o crea otra según el comportamiento previo; renombrar globalmente se realiza desde Administrar categorías. En otra sesión abierta los objetos pueden requerir recarga; la actualización inmediata implementada corresponde a la sesión que renombra.

### Archivos de esta implementación

| Archivo | Cambio |
|---|---|
| src/modelo/Categoria.java | Nombre modificable con validación; ID permanece final |
| src/persistencia/LibreriaDAO.java | Listar/guardar categorías, validación de baja y eliminación transaccional con referencias de activos e inactivos |
| src/servicio/LibreriaServicio.java | Operaciones de categorías y actualización de nombres en libros de la sesión tras guardar |
| src/vista/CategoriaVista.java (nuevo) | Tabla administrativa y acciones |
| src/controlador/CategoriaControlador.java (nuevo) | Alta/modificación, selección, mensajes, confirmación y cancelación de baja |
| src/vista/InicioVista.java | Botón Administrar categorías |
| src/controlador/InicioControlador.java y Navegacion.java | Conexión del acceso a la nueva pantalla |
| test/pruebas/PruebasCategorias.java (nuevo) | Secuencia de aceptación, duplicados, nombres vacíos y referencia tardía |
| test/pruebas/PruebasVistas.java | Incorpora CategoriaVista al control de geometría; ahora diez vistas |
| Documentacion/ESTADO_Y_PENDIENTES.md | Inventario de Categorías y este registro; preserva diagnóstico previo |

No se modificaron otros ABM, el UML ni el informe. La modificación documental local anterior se conservó.

### Compilación y resultados ejecutados

JDK `C:/Users/facub/.jdks/openjdk-26.0.2.1/bin`. Se compiló src/test con `javac -encoding UTF-8 -cp 'lib/*' -d out/verificacion-categorias`. Se ejecutaron las pruebas con `java --enable-native-access=ALL-UNNAMED -Djava.awt.headless=true -Djava.io.tmpdir=DIR_TEMP -cp 'DIR_TEMP;lib/*' pruebas.NOMBRE`, usando como DIR_TEMP la ruta absoluta de out/verificacion-categorias. Las pruebas persistentes crean archivos temporales y los eliminan; datos/libreria.db no fue utilizada.

| Prueba | Resultado |
|---|---|
| PruebasModelo | Código 0; 88 verificaciones |
| PruebasSQLite | Código 0; 9 verificaciones |
| PruebasServicio | Código 0; 86 verificaciones |
| PruebasVistas | Código 0; diez vistas en tamaños inicial/ampliado, pestañas y formulario |
| PruebasCategorias | Código 0; 14 verificaciones |

Las cuatro pruebas existentes pasaron inicialmente. La nueva prueba encontró que **la precarga ya contiene «Novela»**: el primer intento de renombrar «Novlea» se rechazó correctamente como duplicado. Para ejecutar literalmente la secuencia solicitada, el fixture renombra la categoría de ejemplo a «Novela de precarga (prueba)» exclusivamente en su base temporal. Después se recompiló todo y se ejecutaron de nuevo Categorías y Vistas (las dos pruebas modificadas); ambas pasaron. No se relajó la regla de duplicados.

Secuencia comprobada **desde servicio/SQLite**:

1. Alta «Novlea», visible en listado sin libros; se asignó por formulario lógico del servicio a libro activo, reutilizando su ID.
2. Renombrado a «Novela»: mismo ID y nombre actualizado en Libro de memoria; una nueva instancia del servicio confirma persistencia.
3. Baja con libro activo rechazada desde servicio; tras desactivar, baja directa por DAO también rechazada. Reapertura conserva vínculo y libro inactivo.
4. Reasignación a «Otra categoría prueba». Prevalidar baja no escribe: categoría sigue en listado. Luego eliminar confirma la escritura y retira la categoría.
5. Reapertura confirma que el ID eliminado no está y que el libro conserva la categoría de reasignación.

Además: rechazo de nombre vacío y duplicados al crear/renombrar, y rechazo cuando se agrega un libro inactivo entre prevalidación y eliminación.

**Cancelación:** se verificó por lectura que CategoriaControlador retorna antes de llamar eliminarCategoria si no se responde Sí; la prueba de servicio verifica que prevalidar no elimina. Esto no equivale a haber pulsado Cancelar/No en un diálogo real.

Procedimiento manual trasladado a [PRUEBAS: administraciones](PRUEBAS.md#administraciones). No se acreditaron clics en esta etapa.

<a id="editoriales"></a>
## Iteración implementada: ABM de Editoriales (18/11/2026)

**Ruta: Inicio → Administrar editoriales.** La pantalla lista ID/nombre de todas las editoriales y permite Nueva editorial, Modificar, Eliminar e Inicio. Se deshabilitan modificación/baja sin selección y se convierte fila visual a fila del modelo para respetar el ordenamiento.

### Comportamiento e integración

- Nombre obligatorio, recortando espacios externos; duplicados comparados con `nombre = ?`, el mismo criterio exacto de buscarOCrear del catálogo, sensible a mayúsculas y acentos. La normalización de búsquedas visuales no cambia esa identidad.
- Alta autónoma visible sin libros. El formulario de libros conserva su comportamiento: reutiliza una editorial del mismo nombre o crea una si no existe.
- Modificar conserva ID; tras el commit se actualizan todas las instancias de Editorial de los libros cargados en la sesión. Al volver al catálogo se ve el nombre nuevo y al reabrir se obtiene de SQLite. No se promete refresco automático de otra sesión abierta.
- Baja prohibida si existe cualquier fila de libros con editorial_id, sin filtrar actividad. Se informa «Esta editorial está asignada a uno o más libros. Reasignalos antes de eliminarla».
- Si está libre, se pide confirmación; No o cerrar el diálogo retorna antes de eliminar. DAO.eliminarEditorial vuelve a comprobar las referencias dentro de la transacción, después de tomar bloqueo de escritura, y elimina por ID/nombre esperado. Así una asignación posterior a la prevalidación también impide borrar.
- Se reutilizan Editorial, editoriales y libros.editorial_id. No se recrearon tablas ni se migraron/eliminaron registros anteriores. No se modificó Clientes, UML ni informe.

### Archivos modificados o agregados en esta iteración

| Archivo | Cambio |
|---|---|
| src/modelo/Editorial.java | Nombre modificable con validación; ID final |
| src/persistencia/LibreriaDAO.java | Listar, guardar, validar baja y eliminar editorial transaccionalmente |
| src/servicio/LibreriaServicio.java | Operaciones y propagación del nombre a todos los libros de la sesión |
| src/vista/EditorialVista.java (nuevo) | Listado y botones administrativos |
| src/controlador/EditorialControlador.java (nuevo) | Formularios, selección, confirmación, cancelación y errores |
| src/vista/InicioVista.java | Acceso a editoriales y altura inicial ajustada al menú ampliado |
| src/controlador/InicioControlador.java; src/controlador/Navegacion.java | Conexión del nuevo acceso |
| test/pruebas/PruebasEditoriales.java (nuevo) | Aceptación sobre base temporal con dos libros y referencia posterior desde otra sesión |
| test/pruebas/PruebasVistas.java | Agrega EditorialVista; ahora once vistas |
| Documentacion/ESTADO_Y_PENDIENTES.md | Estado vigente, elección de entidades y resultados |

Los cambios de Categorías presentes antes de esta iteración no se retiraron ni se contabilizan como nuevos de Editoriales.

### Ejecución y resultados

Compilación completa exitosa con OpenJDK 26.0.2.1:

```powershell
$j = 'C:/Users/facub/.jdks/openjdk-26.0.2.1/bin'
$o = Join-Path (Get-Location) 'out/verificacion-editoriales'
New-Item -ItemType Directory -Force $o | Out-Null
$f = @(Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object FullName)
& "$j/javac.exe" -encoding UTF-8 -cp 'lib/*' -d $o $f
foreach ($t in 'PruebasModelo','PruebasSQLite','PruebasServicio','PruebasCategorias','PruebasEditoriales','PruebasVistas') {
    & "$j/java.exe" --enable-native-access=ALL-UNNAMED '-Djava.awt.headless=true' "-Djava.io.tmpdir=$o" -cp "$o;lib/*" "pruebas.$t"
}
```

| Prueba | Resultado |
|---|---|
| PruebasModelo | Código 0; 88 verificaciones |
| PruebasSQLite | Código 0; 9 verificaciones |
| PruebasServicio | Código 0; 86 verificaciones |
| PruebasCategorias | Código 0; 14 verificaciones |
| PruebasEditoriales | Código 0; 20 verificaciones |
| PruebasVistas | Código 0; once vistas en tamaño inicial/ampliado, pestañas y formulario |

Se usaron archivos temporales, eliminados por las pruebas al terminar. No se utilizó datos/libreria.db. Los nombres de prueba de Editoriales son distintos de la precarga y no se alteró esta para liberar nombres. `git diff --check` no informó errores de espacios.

### Secuencia comprobada desde servicio/SQLite

1. Crear «Sello ABM prueba inicial» con espacios externos y consultarla antes de asignar libros. Rechazar nombre vacío y duplicado.
2. Asignar dos libros ED-PRUEBA-1 y ED-PRUEBA-2; comprobar que ambos reutilizan el mismo ID. Renombrar a «Sello ABM prueba corregido»; ambos objetos muestran el nombre nuevo conservando ID, y ambos lo conservan en una nueva instancia del servicio. Renombrar a otra editorial existente se rechaza.
3. Eliminar con referencias activas se rechaza; se verifica vínculo persistido. Reasignar el primer libro a «Sello ABM prueba destino» y desactivar el segundo: queda únicamente una referencia inactiva. La baja directa por DAO también se rechaza, con mensaje claro y vínculo persistido.
4. Reasignar el segundo; prevalidar baja no modifica el listado. Luego eliminar y reabrir: desaparece la editorial y ambos libros conservan sus nuevas asignaciones.
5. Control adicional: prevalidar una editorial libre y asignarle un libro inactivo desde otra instancia del servicio antes de eliminar. La nueva comprobación transaccional rechaza la eliminación y conserva la editorial.

Procedimiento manual trasladado a [PRUEBAS: administraciones](PRUEBAS.md#administraciones). No se acreditaron clics en esta etapa.

<a id="estado-clientes"></a>
## Iteración de Clientes: baja lógica y reactivación — 18/11/2026

Referencia inicial: rama `tpo-grupo5`, commit `ba6bebcc79403c977a0231c59b3dee2ed9755394`; al iniciar Git no mostró cambios locales. Se preservaron las implementaciones existentes de Categorías/Editoriales. Este registro prevalece sobre las afirmaciones históricas de que Cliente no tiene activo o administración.

**Clientes: parcial.** Implementados listado administrativo de activos/inactivos, baja lógica confirmada y reactivación confirmada con el mismo ID. Alta existente desde venta y Hacerse socio permanece; no se agregó alta dentro de Administración. Falta modificación de datos; edición y unicidad del DNI se dejan para otra iteración. Las cuatro entidades siguen siendo Libros, Clientes, Categorías y Editoriales. UML e informe no se modificaron.

### Navegación y reglas implementadas

Inicio → **Administrar clientes** → seleccionar fila → **Dar de baja** / **Reactivar**. Tabla con nombre, DNI y estado; selección respeta ordenamiento. Confirmar aplica el cambio; No o cerrar el diálogo retorna sin escribir. Se puede dar de baja aunque tenga préstamos pendientes.

La identidad sigue siendo el ID. No se eliminan clientes, asociaciones, ventas ni préstamos. Nombre y DNI permanecen inmutables en esta iteración. Cliente incorpora activo (constructor anterior crea activos); ClienteDAO lee el estado y permite actualizarlo. El servicio actualiza el cliente y las referencias de las ventas cargadas después de guardar.

Al inicializar SQLite se comprueba `PRAGMA table_info(clientes)`. Solo si falta activo, se agrega `INTEGER NOT NULL DEFAULT 1 CHECK (activo IN (0,1))`. Así las filas existentes quedan activas sin reemplazar tabla/IDs/DNI/vínculos; aperturas posteriores no sobrescriben bajas. La migración ocurre en la transacción de inicialización.

- Selección de cliente en nueva venta: oculta inactivos. Venta.validarConfirmacion rechaza cliente inactivo incluso en efectivo si está asignado; efectivo sin cliente continúa permitido.
- DAO.guardarVenta comprueba activo por ID dentro de la transacción antes de insertar una confirmación, tomando bloqueo de escritura. Protege también un borrador con objeto desactualizado por baja en otra sesión. PagoControlador muestra «El cliente está inactivo. Seleccioná otro cliente o quitá el cliente para pagar en efectivo». Se puede volver a venta, quitar/cambiar cliente y reintentar. Cancelar borrador no queda bloqueado por esta regla.
- Nuevo préstamo: selector muestra solo socios activos. La inserción SQL exige cliente activo; si no cumple, no se registra préstamo ni se toca stock.
- Hacerse socio: para DNI existente inactivo rechaza y pide reactivarlo desde Administración. La transacción revierte cualquier intento de insertar asociación; no cambia estado ni crea un reemplazo. No se cambió la política previa de duplicados de DNI.
- Historial de préstamos: se consulta por separado, sin filtrar por actividad. La pestaña **Historial de préstamos** permite seleccionar el préstamo del cliente inactivo y devolverlo. La devolución actualiza solamente un movimiento pendiente y repone stock una sola vez. Reactivar conserva la asociación original y habilita nuevos préstamos.

### Archivos modificados

- `src/modelo/Cliente.java`: estado y constructor compatible; `Venta.java`: validación de actividad al confirmar.
- `src/persistencia/ConexionSQLite.java`: migración idempotente; `ClienteDAO.java`: lectura/actualización del estado; `LibreriaDAO.java`: socios con estado, bloqueo de asociación de inactivos y validaciones SQL de venta/préstamo.
- `src/servicio/LibreriaServicio.java`: cambiarActividadCliente con actualización de referencias de sesión.
- Nuevos `src/vista/AdministrarClientesVista.java` y `src/controlador/AdministrarClientesControlador.java`.
- `InicioVista.java`, `InicioControlador.java`, `Navegacion.java`: acceso administrativo.
- `ClienteControlador.java`, `PrestamoControlador.java`: filtros únicamente para nuevas operaciones; historial de préstamos sin filtro de actividad.
- Nuevo `test/pruebas/PruebasClientesEstado.java`; `PruebasVistas.java`: agrega la pantalla administrativa.
- Este Markdown: estado vigente y registro. Sin cambios al UML, informe ni edición/unicidad del DNI.

### Pruebas ejecutadas

Compilación UTF-8 de todas las fuentes src/test con JDK `C:/Users/facub/.jdks/openjdk-26.0.2.1/bin`, driver `lib/*`, salida `out/verificacion-clientes`. Ejecuciones con `--enable-native-access=ALL-UNNAMED`, `-Djava.awt.headless=true`, `-Djava.io.tmpdir=RUTA_ABSOLUTA/out/verificacion-clientes` y classpath `RUTA_ABSOLUTA/out/verificacion-clientes;lib/*`. Compilación y todas las ejecuciones terminaron con código 0:

| Prueba | Resultado |
|---|---|
| PruebasModelo | 88 verificaciones |
| PruebasSQLite | 9 verificaciones |
| PruebasServicio | 86 verificaciones |
| PruebasCategorias | 14 verificaciones |
| PruebasEditoriales | 20 verificaciones |
| PruebasClientesEstado | 20 verificaciones |
| PruebasVistas | 12 vistas en tamaños inicial/ampliado, pestañas y formulario |

Se usaron bases temporales que las pruebas eliminan al finalizar; no se escribió datos/libreria.db. `git diff --check` no informó errores de espacios.

**Probado desde servicio/SQLite:** cliente con DNI `000987654`, venta histórica y asociación → préstamo → recreación del esquema anterior quitando solo activo en la base temporal → apertura/migración conserva IDs/DNI/venta/socio/préstamo → baja en otra sesión → borrador con cliente todavía activo en memoria rechazado por SQL, sin ID/pago/stock modificado → reinicio conserva inactividad → préstamo, asociación y venta rechazados sin nuevas filas persistidas ni cambio de stock → devolución del préstamo original aumenta stock una vez; repetición rechazada → reactivación mismo ID/socio → nuevo préstamo permitido. Nueva baja persiste tras otra reapertura; efectivo sin cliente sigue confirmando. Reactivación final también persiste.

**Swing:** no se realizaron clics. La prueba de vistas verifica distribución headless y la lectura del controlador acredita la rama de cancelar/cerrar sin escritura, pero no equivale a pulsar esos botones. El entorno previamente informó falta del componente nativo de Computer Use; no se reintentó en esta iteración.

Procedimiento manual trasladado a [PRUEBAS: administraciones](PRUEBAS.md#administraciones). No se acreditaron clics en esta etapa.

<a id="edicion-clientes"></a>
## Iteración implementada: modificación de Clientes — 18/11/2026

**Ruta: Inicio → Administrar clientes → seleccionar fila → Editar cliente.** Formulario precargado con nombre y DNI, habilitado tanto para activos como inactivos. La selección utiliza EntradaFormulario.seleccionado, que convierte fila visual a modelo cuando la tabla está ordenada. Cancelar o cerrar no llama al servicio y descarta los campos del formulario. Los cambios locales anteriores de baja/reactivación se preservaron.

### Regla de edición y memoria

Se guarda por ID interno. Nombre y DNI usan las validaciones de Cliente (texto no vacío y DNI de dígitos, recortando espacios externos); DNI sigue siendo String/TEXT con ceros iniciales. No se modifican activo ni referencias de ventas/socios/préstamos.

ClienteDAO.modificar adquiere el bloqueo de escritura al comenzar la transacción, lee la ficha por ID y comprueba que nombre/DNI no hayan cambiado respecto del objeto editado. Si cambiaron en otra sesión, rechaza con mensaje de recarga en lugar de sobrescribirlos. Si el DNI propuesto difiere del actual, comprueba su ausencia en todos los demás IDs, activos e inactivos, antes del UPDATE. Un conflicto rechaza ambos campos y muestra «Ese DNI ya pertenece a otro cliente, activo o inactivo». La transacción serializa dos ediciones que compiten por el mismo DNI.

Si el DNI se mantiene, no se exige unicidad: una ficha con duplicado histórico puede corregir nombre por ID. También puede cambiar ese DNI a uno libre. No se creó índice UNIQUE, no se limpiaron duplicados y no se alteraron las altas existentes; esta regla se limita a edición.

El UPDATE cambia únicamente nombre/DNI. El resultado devuelve el estado actual de la BD, por lo que editar no reactiva un cliente dado de baja incluso desde otra sesión. Solo después del commit, LibreriaServicio.modificarCliente actualiza el objeto canónico y referencias de ventas del mismo ID. Los selectores que se vuelven a abrir leen los clientes actualizados; socios y préstamos consultan por JOIN y muestran los datos corregidos. Historiales conservan IDs/vínculos y muestran la identidad actual, sin crear instantáneas nuevas. Otras instancias de aplicación pueden requerir recarga de sus objetos.

### Archivos de esta iteración

| Archivo | Cambio |
|---|---|
| src/modelo/Cliente.java | Nombre/DNI actualizables mediante actualizarDesde, con comprobación de ID; copia de ficha ya validada |
| src/persistencia/ClienteDAO.java | modificar transaccional por ID, control de DNI cambiado y conflicto de edición concurrente |
| src/servicio/LibreriaServicio.java | modificarCliente; actualiza referencias después de guardar |
| src/vista/AdministrarClientesVista.java | Botón Editar cliente |
| src/controlador/AdministrarClientesControlador.java | Formulario de edición, validación/errores y cancelación |
| test/pruebas/PruebasClientesEdicion.java (nuevo) | Casos de edición, duplicados históricos, vínculos, inactivos y dos escritores simultáneos |
| Documentacion/ESTADO_Y_PENDIENTES.md | Estado vigente y este registro |

No se agregaron altas administrativas ni se modificaron UML/informe. Los otros archivos que ya estaban modificados al comenzar corresponden a la iteración anterior y se conservaron.

### Verificación ejecutada

Compilación completa con JDK `C:/Users/facub/.jdks/openjdk-26.0.2.1/bin`, `javac -encoding UTF-8 -cp 'lib/*' -d out/verificacion-edicion-clientes` y todas las fuentes de src/test. Ejecución de cada main de pruebas con `--enable-native-access=ALL-UNNAMED -Djava.awt.headless=true`, `java.io.tmpdir` dirigido a la carpeta de verificación y classpath `CARPETA;lib/*`. Bases temporales eliminadas por las pruebas; no se usó datos/libreria.db.

| Prueba | Resultado (todas código 0) |
|---|---|
| PruebasModelo | 88 verificaciones |
| PruebasSQLite | 9 verificaciones |
| PruebasServicio | 86 verificaciones |
| PruebasCategorias | 14 verificaciones |
| PruebasEditoriales | 20 verificaciones |
| PruebasClientesEstado | 20 verificaciones |
| PruebasClientesEdicion | 20 verificaciones |
| PruebasVistas | 12 vistas en tamaños inicial/ampliado, pestañas y formulario |

Servicio/SQLite: socio con venta y préstamo previos cambia nombre y DNI a `0000999222`; conserva ID, asociación y préstamo, y se ven datos actuales en venta/socios/préstamos. Cambiar a DNI ocupado por activo y después por inactivo se rechaza, conservando nombre/DNI originales en memoria y BD. Nombre vacío/DNI inválido se rechazan. Una segunda ficha creada con DNI repetido conserva ese DNI al corregir solo su nombre y luego puede corregirlo a uno libre. Edición de inactivo mantiene estado; reapertura conserva datos y vínculos. Dos hilos con conexiones DAO independientes intentan asignar el mismo DNI libre: uno guarda, el otro rechaza y mantiene ambos campos; queda exactamente un registro con ese DNI.

`git diff --check` sin errores de espacios. No se realizaron clics en Swing; prueba headless de vistas y lectura del controlador no equivalen a interactuar con el diálogo. Cancelar/cerrar y la selección ordenada quedan pendientes de ensayo visual. El control nativo había fallado en el entorno y no se reintentó esta vez.

Procedimiento manual trasladado a [PRUEBAS: administraciones](PRUEBAS.md#administraciones). No se acreditaron clics en esta etapa.

<a id="alta-clientes"></a>
## Cierre de implementación del ABM de Clientes: alta administrativa — 18/11/2026

**Estado vigente: ABM de Clientes implementado; clics Swing pendientes de verificación.** Inicio → Administrar clientes ofrece Nuevo cliente, Editar cliente, Dar de baja, Reactivar y consulta de activos/inactivos. Las cuatro administraciones elegidas son Libros, Clientes, Categorías y Editoriales. Las afirmaciones históricas de que falta alta administrativa quedan superadas por este registro. UML e informe se mantienen para el final.

### Comportamiento

Nuevo cliente reutiliza el formulario nombre/DNI de Administración y `LibreriaServicio.crearCliente`. Guarda una ficha activa y refresca el listado; no crea venta ni asociación. Cancelar/cerrar descarta los campos sin llamar al servicio. En Venta, ClienteControlador mantiene su comportamiento: solo después de un alta exitosa asigna la nueva ficha al borrador mediante Guardar y usar; ante rechazo no se modifica el borrador.

ClienteDAO.guardar ahora inicializa el esquema compatible y ejecuta bloqueo de escritura, consulta de DNI e INSERT en una transacción. Las rutas administrativa, Venta y ejemplo de consola comparten esa operación. `LibreriaDAO.asociar` usa el mismo bloqueo y helper de inserción dentro de su transacción. DNI se valida como texto de dígitos, recortado y conservando ceros. Si existe un activo, informa «Ese DNI ya está registrado. Buscá el cliente existente»; si las fichas encontradas son inactivas, informa que debe reactivarse desde Administrar clientes. No se modifica ninguna ficha existente al rechazar. No hay índice UNIQUE ni eliminación de duplicados históricos.

Hacerse socio consulta todas las coincidencias dentro de la transacción:

- Cero: valida nombre y crea ficha activa más asociación de forma atómica.
- Una activa: asocia su ID sin crear ficha; si ya es socio informa el caso.
- Una inactiva: requiere reactivación explícita; no agrega asociación ni reactiva.
- Varias: informa ambigüedad histórica y pide corregirla en Administración; no elige el primer ID ni modifica asociaciones.

La edición mantiene su excepción para DNI sin cambios: permite corregir nombre de una ficha histórica duplicada por ID, o cambiar el DNI a uno libre. La nueva regla de altas no cambia IDs, ventas, préstamos ni asociaciones anteriores. El uso posterior de fichas históricas duplicadas fuera del alta/asociación no se amplía en esta iteración.

### Archivos de esta iteración

- `src/vista/AdministrarClientesVista.java`: botón Nuevo cliente.
- `src/controlador/AdministrarClientesControlador.java`: mismo formulario para alta/edición; alta solo crea ficha y refresca tabla.
- `src/persistencia/ClienteDAO.java`: alta transaccional y helpers comunes de bloqueo, búsqueda completa por DNI e inserción validada.
- `src/persistencia/LibreriaDAO.java`: asociación inequívoca y alta cliente/socio atómica.
- `test/pruebas/PruebasClientesEdicion.java`: duplicados históricos cargados por SQL directo en base temporal, no mediante el alta normal; se mantienen pruebas de edición y concurrencia.
- Nuevo `test/pruebas/PruebasClientesAlta.java`: altas, conflictos, asociación, ambigüedad y concurrencia.
- Este Markdown: estado vigente y resultados. Los demás cambios locales preexistentes se preservaron; no se modificaron UML/informe.

### Compilación y pruebas

Compilación completa con OpenJDK 26.0.2.1: `javac -encoding UTF-8 -cp 'lib/*' -d out/verificacion-alta-clientes [fuentes src/test]`. Cada main se ejecutó con `--enable-native-access=ALL-UNNAMED -Djava.awt.headless=true`, `java.io.tmpdir` apuntando a la carpeta absoluta de verificación y classpath `CARPETA;lib/*`. Compilación y todas las pruebas finalizaron con código 0:

| Prueba | Resultado |
|---|---|
| PruebasModelo | 88 verificaciones |
| PruebasSQLite | 9 verificaciones |
| PruebasServicio | 86 verificaciones |
| PruebasCategorias | 14 verificaciones |
| PruebasEditoriales | 20 verificaciones |
| PruebasClientesEstado | 20 verificaciones |
| PruebasClientesEdicion | 20 verificaciones |
| PruebasClientesAlta | 17 verificaciones |
| PruebasVistas | 12 vistas en tamaño inicial/ampliado, pestañas y formulario |

Servicio/SQLite: alta activa `00088771`, consulta/reapertura y ausencia de asociación automática; rechazo activo con borrador intacto; rechazo inactivo sin cambiar estado; asociación por ID existente, rechazo de socio repetido, creación de cliente/socio con DNI libre, nombre inválido sin ficha parcial. Dos fichas históricas con un DNI se cargan por SQL temporal: asociar rechaza por ambigüedad y conserva asociaciones/registros. Dos altas simultáneas con conexiones distintas y el mismo DNI producen un éxito, un rechazo y exactamente una ficha. PruebasClientesEdicion sigue comprobando nombre sobre duplicado, corrección a DNI libre, vínculos/reapertura y competencia de dos ediciones por DNI libre.

Las pruebas usan archivos temporales que eliminan al terminar; no se escribió `datos/libreria.db`. `git diff --check` sin errores de espacios. **No se realizaron clics en Swing**: la prueba headless y la revisión de ramas no equivalen a pulsar Guardar, Cancelar o cerrar. El entorno anteriormente informó falta del componente nativo de Computer Use; no se reintentó en esta pasada.

Procedimiento manual trasladado a [PRUEBAS: administraciones](PRUEBAS.md#administraciones). No se acreditaron clics en esta etapa.


<a id="reorganizacion"></a>
## 19/11/2026 — reorganización documental

Rama tpo-grupo5, HEAD ba6bebcc79403c977a0231c59b3dee2ed9755394. Se trabajó sobre el árbol abierto, preservando cambios locales de Clientes en controladores/modelo/DAO/servicio/esquema/vistas/pruebas y el estado documental acumulado. Sin commit ni push.

La guía anterior estaba fechada 17/11 con referencia ad55cbc2d74e5e697c6f42335bf05c90e75d3fc3 e indicaba Git limpio para aquel relevamiento. Esa afirmación no describe el árbol del 19/11. Se actualizaron reglas de DNI, baja/edición/alta de Clientes, índice de clases y número de pruebas. Los archivos originales del modelo reflejaban src/TPO y SQL Server futuro: hoy las fuentes están en src/test, la persistencia es SQLite, Venta asigna ID una vez al guardar y Libro permite editar título/ISBN.

### Inventario y destinos de consolidación

Se revisaron todos los Markdown encontrados en este proyecto: los tres retirados, GUIA_TPO y ESTADO_Y_PENDIENTES. No existía un README en la raíz de esta librería. Los originales DOCX/PDF/PNG/SVG permanecen independientes y sin cambios.

| Archivo original → sección revisada | Ubicación principal de destino y tratamiento |
|---|---|
| IMPLEMENTACION_MODELO.md → Alcance e inspección | [Historial del 31/10](#modelo-20261031): entorno, POO.iml local ignorado, ejercicios ajenos preservados, JDK y herramientas de esa fecha; [Guía §1](GUIA_TPO.md#negocio) para alcance actual |
| IMPLEMENTACION_MODELO.md → Archivos | Inventario histórico conservado arriba; [Guía §8](GUIA_TPO.md#clases) enlaza rutas actuales, incluidos nuevos controladores/vistas/pruebas |
| IMPLEMENTACION_MODELO.md → Relaciones y responsabilidades | [Guía §3](GUIA_TPO.md#modelo): composición, instantáneas, listas defensivas, Experto/Creador; arquitectura en §2 |
| IMPLEMENTACION_MODELO.md → Decisiones y supuestos | Guía §3 y §6: IDs, identidad, BigDecimal/HALF_UP, HOTSALE, texto/DNI/ISBN, edición, stock, pago, fechas, plazo, actividad y overflow; diferencias de versión explícitas |
| IMPLEMENTACION_MODELO.md → Transiciones y límite transaccional | Guía §3–5: estados, política pura, reintegro, servicio/DAO y commit antes de memoria; coordinación antigua pendiente ahora implementada |
| IMPLEMENTACION_MODELO.md → Pruebas ejecutadas y resultado real | Historial del 31/10 conserva salida, contexto y cobertura detallada de 88 controles; [PRUEBAS](PRUEBAS.md#automatizadas) reúne resultados vigentes |
| IMPLEMENTACION_MODELO.md → Ejecutar desde IntelliJ / Repetir desde PowerShell | Comandos históricos preservados arriba y rotulados; [README](../README.md) y PRUEBAS corrigen raíces y dependencias actuales; Guía §4 explica operación |
| IMPLEMENTACION_MODELO.md → Pendiente fuera de esta etapa | Registro original arriba, superado para MVC/SQLite/IDs/transacciones; estado actual en ESTADO, límites funcionales en Guía §1/5 |
| INTERFAZ_MVC.md → Recorrido | [Guía §5](GUIA_TPO.md#recorridos): búsqueda, cantidades, cliente, HOTSALE, pago/vuelto, historial/devolución, catálogo, socio/préstamo y retorno; procedimientos en PRUEBAS |
| INTERFAZ_MVC.md → Responsabilidades | Guía §2, §4 y §5: EDT/Main, navegación, EntradaFormulario, capas, transacciones y borrador en sesión |
| INTERFAZ_MVC.md → Verificación | Enlace operativo redirigido a PRUEBAS; SQLite integrado en Guía §4 |
| SQLITE.md → Introducción y precarga | [Guía §4](GUIA_TPO.md#sqlite): archivo predeterminado, 13 clientes/56 libros/50 títulos, migraciones, IDs y marcador de ejemplos |
| SQLITE.md → Ejecutar la interfaz | README y Guía §4: JDK, JAR, raíces, cwd, tpo.db, opciones JVM, classpath por plataforma; comandos conservados con base de prueba |
| SQLITE.md → Qué se guarda | Guía §4: tablas, dinero textual, clientes/catálogo/socios/préstamos/ventas/cancelaciones/devoluciones, transacción y conflictos |
| SQLITE.md → Ejemplo de consola y pruebas | Guía §4 conserva script, argumentos, diferencias con Main y rutas seguras; PRUEBAS conserva comandos Windows/Unix y amplía a las nueve entradas |
| GUIA_TPO.md anterior → diagrama, recorridos, clases, defensa | Guía actual conserva Mermaid, ejemplos, índice ampliado y preguntas/respuestas; actualiza referencias |
| ESTADO_Y_PENDIENTES.md anterior → diagnósticos y etapas | Este historial conserva decisiones, archivos y resultados por etapa; diagnósticos viejos rotulados como superados |
| ESTADO_Y_PENDIENTES.md anterior → procedimientos manuales e intento visual | [PRUEBAS](PRUEBAS.md#recorrido-swing) conserva error exacto, configuración, datos y tabla 3→2→2→3; [administraciones](PRUEBAS.md#administraciones) reúne casos pendientes |

La información técnica útil de los tres documentos retirados quedó incorporada en la guía; sus instrucciones ejecutables y resultados tienen ubicación principal en README/PRUEBAS, y el contexto obsoleto queda aquí para trazabilidad. No se descartan decisiones de etapas anteriores ni se convierten pruebas de servicio en pruebas visuales.

### Verificación de esta reorganización

Se ejecutaron consultas de Git (rama, HEAD y estado), inventario/búsquedas con rg y lectura UTF-8 de documentos y código. Test-Path confirmó el ejecutable local de javac. Una consulta python --version informó CommandNotFoundException: no se usó Python ni es dependencia del proyecto. Una búsqueda rg con un glob literal de Windows produjo error 123; se repitió sobre test/pruebas y se localizaron las nueve entradas y el uso de temporales.

No se ejecutaron compilación, tests ni Main; no se abrió SQLite ni se reintentó el control del escritorio. Las instrucciones se contrastaron con las rutas, métodos y dependencias presentes. Los resultados de ejecución siguen siendo los registrados el 18/11. UML e informe no se modificaron; no se validó gráficamente el SVG ni se produjeron capturas de Swing. El PDF y el SVG conservan el alcance de lectura previamente documentado.
Comprobaciones finales: extracción de word/document.xml del DOCX confirmó la redacción sobre seis interfaces, doce clases, tres prácticas, excepción/try-catch y documentación de aproximadamente diez páginas. El validador documental revisó 213 enlaces relativos (archivos y anclas explícitas), sin destinos ausentes; el índice de la guía referencia los 64 archivos Java de src/test. git diff --check terminó con código 0 y avisos de conversión LF→CRLF, sin errores de espacios. No se modificaron fuentes para resolver esos avisos.

Tras revisar la tabla de destinos se retiraron IMPLEMENTACION_MODELO.md, INTERFAZ_MVC.md y SQLITE.md. Sus nombres permanecen en esta trazabilidad, pero ningún enlace operativo apunta a los archivos retirados. README es la entrada a ejecución, estudio, pruebas y estado; todas las filas de aceptación manual permanecen pendientes.