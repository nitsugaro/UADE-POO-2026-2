# Estado y pendientes del TPO de librería — grupo 5

> Actualización del 18/11/2026: se implementó el ABM de Categorías. El registro de esta iteración al final prevalece sobre el diagnóstico previo y sus pendientes A4 relativos a Categorías. Los demás ABM no se modificaron.

## Estado vigente después de Editoriales — 18/11/2026

Las cuatro entidades elegidas por el grupo son **Libros, Clientes, Categorías y Editoriales**. Libros, Categorías y Editoriales tienen A/B/M/C conectadas; Clientes sigue pendiente para una próxima iteración. Socios no se contará como una de las cuatro administraciones. La elección ya está resuelta: las preguntas del diagnóstico histórico sobre elegir entidades quedan superadas. UML e informe se mantienen para después de Clientes y las verificaciones visuales.

Esta sección y el registro de Editoriales al final prevalecen sobre el diagnóstico histórico y su comparación de candidatas. Las implementaciones locales de Categorías y los cambios documentales previos se preservaron.

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

### Pendiente concreto de Categorías: prueba en IntelliJ

No se acreditan clics desde Swing. El entorno había informado `Computer Use native pipe is unavailable`; en esta iteración se ejecutaron pruebas headless y se revisaron listeners, sin reintentar control nativo ni afirmar interacción visual.

1. Ejecutar Main en IntelliJ con una ruta nueva `-Dtpo.db=datos/prueba-categorias.db` (no la base de entrega).
2. Inicio → Administrar categorías: si existe «Novela» de precarga, renombrarla a «Novela de precarga» para liberar el nombre. Crear «Novlea»; comprobar que aparece antes de asignarla.
3. Administrar libros → Nuevo libro: asignar «Novlea», activo. Volver a Categorías, renombrar a «Novela» y regresar al catálogo: debe mostrar nombre corregido sin reiniciar. Verificar también tras reiniciar con la misma base.
4. Intentar eliminarla con libro activo y luego inactivo: ambos muestran el mensaje de reasignación, conservando categoría/vínculo.
5. Reasignar el libro a otra categoría. En Categorías iniciar Eliminar y responder No/cerrar: debe seguir listada. Repetir y responder Sí: debe desaparecer.
6. Reiniciar: «Novela» no figura, el libro conserva la reasignación. Probar ordenamiento de tabla, selección, nombre vacío y duplicado; ningún error debe cambiar otra fila.

Categorías deja de ser una candidata sin implementación: ahora es una segunda administración A/B/M/C junto con Libros por lectura del flujo. El grupo aún debe elegir/completar las restantes; esta iteración no altera sus decisiones. No se identificaron pendientes funcionales de Categorías en las pruebas automatizadas; queda la verificación interactiva descrita.


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

### Qué falta verificar interactuando con Swing

**No se interactuó con Swing en esta iteración.** Se ejecutó prueba headless de geometría y se revisó el controlador. El antecedente del entorno es `Computer Use native pipe is unavailable`; no se reintentó el control nativo. La cancelación se acredita por lectura de `respuesta != YES_OPTION` y porque la prevalidación ejecutada no escribe, no como clic realizado en No/cerrar.

Pasos en IntelliJ, con Main y `-Dtpo.db=datos/prueba-editoriales.db` en una base nueva:

1. Abrir Inicio → Administrar editoriales. Crear «Sello visual prueba inicial» y comprobarlo sin libros. Intentar vacío/duplicado y verificar error sin agregar filas.
2. Crear dos libros con esa editorial desde Administrar libros. Renombrar a «Sello visual prueba corregido» desde Editoriales. Volver al catálogo y comprobar ambos nombres/ID; repetir tras reiniciar usando la misma base.
3. Intentar eliminar: debe indicar que hay libros. Reasignar el primero a «Sello visual destino», desactivar el segundo y reintentar: debe bloquearse igualmente.
4. Reasignar el segundo. Iniciar baja y responder No; repetir cerrando el diálogo: en ambos casos sigue listada. Repetir y responder Sí: desaparece. Reiniciar y verificar editorial ausente y asignaciones de ambos libros conservadas.
5. Ordenar la tabla y editar/eliminar una fila seleccionada para confirmar que opera sobre el registro correcto.

Pendiente de Editoriales: esta comprobación visual. No se identificaron fallos funcionales en las pruebas automatizadas. El siguiente ABM elegido es Clientes; UML e informe siguen fuera de esta iteración.
