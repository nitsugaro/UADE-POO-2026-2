# Estado y pendientes del TPO de librería — grupo 5

[Inicio](../README.md) · [Guía de estudio](GUIA_TPO.md) · [Pruebas](PRUEBAS.md) · [Historial](HISTORIAL_DE_CAMBIOS.md)

## Estado vigente después de Editoriales y del cierre de Clientes

Actualización documental: **19/11/2026**. Rama **tpo-grupo5**, commit de referencia **ba6bebcc79403c977a0231c59b3dee2ed9755394**, más cambios locales del grupo. Categorías y Editoriales están en la referencia; Clientes incluye modificaciones locales de modelo, controladores, servicio, DAO, esquema y pruebas. Se preservaron. El detalle por etapa está en el historial; no se hizo checkout, commit ni push.

**Las cuatro administraciones elegidas son Libros, Clientes, Categorías y Editoriales. Todas tienen alta, baja, modificación y consulta conectadas. Los dos recorridos funcionales elegidos son Venta y Devolución.** Socios/préstamos son funciones adicionales; no reemplazan una de las cuatro administraciones. No quedan operaciones administrativas conocidas por implementar en Clientes.

Las implementaciones cuentan con pruebas automatizadas de modelo, servicio/SQLite y geometría headless: la última suite registrada aprobó las nueve entradas el 18/11. **El recorrido interactivo Swing sigue pendiente por bloqueo del control de escritorio.** No hay clics aprobados. La enumeración de ventanas falló antes de observar Inicio; no es evidencia de fallo de la aplicación. [Error exacto y recorrido pendiente](PRUEBAS.md#recorrido-swing).

En esta reorganización se revisaron documentos, código y referencias, sin ejecutar Main ni repetir pruebas. Los resultados anteriores conservan su fecha/contexto y no se presentan como ejecuciones nuevas.

## Fuentes y criterio de cumplimiento

Fuentes: [consigna final](TP_Final_Consignas.docx), checklist de ocho requisitos proporcionado por el grupo y código del árbol abierto. El DOCX fue relevado previamente con esta redacción: **«6 interfaces Swing (4 de alta/baja/modificar y 2 de negocio/funcionales)»**. El checklist agrega explícitamente consulta. No queda inequívocamente definida una entidad por interfaz: **contar una administración completa por entidad y dos recorridos funcionales es una decisión del grupo**, pendiente de aceptación docente; no una exigencia textual inventada.

El [PDF anterior](Sistema%20de%20gestión%20de%20una%20librería%20comercial.pdf) y el [UML original](UML%20-%20TPO.svg) son antecedentes independientes y no amplían el alcance funcional obligatorio. [Teams](Consigna%20Teams.png) corresponde al avance; sus dispensas temporales no eximen el final.

Estados: **implementado** indica código conectado con evidencia, sin equivaler a aprobación visual/docente; **parcial**, material presente con partes pendientes; **faltante**, ausencia confirmada; **pendiente de verificación**, comprobación aún no realizada.

## Matriz de las ocho consignas

| Consigna | Estado vigente | Evidencia concreta | Próxima acción |
|---|---|---|---|
| Código Java funcional que resuelva el negocio | Implementado; aceptación interactiva pendiente | [Main](../src/aplicacion/Main.java), Navegacion y [LibreriaServicio](../src/servicio/LibreriaServicio.java): venta, devolución, catálogo, clientes, socios y préstamos; pruebas Modelo/Servicio | Ejecutar [recorrido completo](PRUEBAS.md#recorrido-swing) con stock e historia y reapertura |
| Diagrama de clases final actualizado, mínimo 12 clases | Parcial | SVG existente desactualizado; el código ya supera 12 clases. Tiene Venta/DetalleVenta/Pago/Descuento/Devolucion/PoliticaDevolucion y MVC/DAO que el original no representa adecuadamente | Actualizar UML sobre [índice actual](GUIA_TPO.md#clases); revisar relaciones/multiplicidades y mínimo |
| Tres patrones o buenas prácticas de clase implementadas | Implementado; selección docente pendiente de verificación | Experto: Venta.calcularTotal y DetalleVenta.calcularSubtotal; Creador: Venta.agregarLibro/conCantidad; MVC: VentaVista → VentaControlador → modelo/servicio. [Explicación](GUIA_TPO.md#practicas) | Confirmar que se presentarán esas tres según material de clase; ensayar defensa. No inventar interfaz Repository |
| Excepción personalizada y al menos tres try/catch | Implementado; mensajes visuales pendientes | StockInsuficienteException y DevolucionNoPermitidaException; bloques independientes PagoControlador.confirmar, CatalogoControlador.editar y DevolucionControlador.confirmarDevolucion | Probar [errores y recuperación](PRUEBAS.md#errores); no confundir tres tipos en un catch con tres bloques |
| Seis interfaces Swing completas: cuatro administrativas y dos funcionales | Implementado según criterio del grupo; validación interactiva/docente pendiente | Cuatro administraciones de tabla siguiente; Venta/Pago y Historial/Devolución conectados por controladores y servicio; suite automatizada registrada | Confirmar cómputo y completar [seis interfaces](PRUEBAS.md#recorrido-swing) más [A/B/M/C](PRUEBAS.md#administraciones) |
| Persistencia en texto o BD | Implementado; paquete final pendiente de verificación | [ConexionSQLite](../src/persistencia/ConexionSQLite.java), ClienteDAO y LibreriaDAO; transacciones de operación/stock, migración activo, reapertura, rollback y concurrencia probados en bases temporales | [Reapertura visual](PRUEBAS.md#recorrido-swing) y [ejecución del paquete](PRUEBAS.md#cierre) |
| Al menos tres usos de diálogos | Implementado; observación interactiva pendiente | LibroFormulario en CatalogoControlador.editar; Hacerse socio en InicioControlador.asociar; confirmar devolución en DevolucionControlador; también cancelación y cierre | Abrir/aceptar/cancelar los [diálogos](PRUEBAS.md#errores) y registrar efectos |
| Documentación final ~10 páginas: propuesta, UML, archivos y capturas | Parcial | Guía y materiales históricos disponibles; PDF anterior no representa alcance vigente. No se dispone de capturas finales acreditadas ni UML actualizado | Tras pruebas, capturar interfaz, actualizar UML y preparar informe con las cuatro partes; [cierre](PRUEBAS.md#cierre) |

## Administraciones y recorridos elegidos

“Conectado” se acredita por lectura y pruebas de lógica/persistencia; no significa prueba manual.

| Entidad / recorrido | Acceso y operaciones actuales | Regla que debe preservarse | Verificación pendiente |
|---|---|---|---|
| Libros | Inicio → Administrar libros; Nuevo libro, Editar libro, búsqueda/listado administrativo | Alta/edición comparten LibroFormulario; baja lógica al desmarcar Activo. Detalles de ventas mantienen título/ISBN/precio históricos | Clics en las cuatro operaciones, cancelación y reapertura |
| Clientes | Inicio → Administrar clientes; Nuevo cliente, Editar cliente, baja, reactivar, listado nombre/DNI/estado | ID y vínculos conservados; DNI texto; altas libres entre activos/inactivos; edición de duplicados históricos por ID. Inactivo no vende/presta, sí devuelve préstamo abierto | Alta/edición/baja/reactivación, selección ordenada, diálogos y errores |
| Categorías | Inicio → Administrar categorías; listado incluso vacío de libros, nueva/editar/eliminar | Nombre con comparación exacta del catálogo; renombrado conserva ID; baja física solo sin referencia de ningún libro, incluidos inactivos | Clics, bloqueo, No/cerrar/Sí y reapertura |
| Editoriales | Inicio → Administrar editoriales; listado incluso sin libros, nueva/editar/eliminar | Igual regla de baja; referencias se vuelven a comprobar dentro de transacción después de confirmar | Dos libros renombrados, referencia inactiva y carrera antes de aceptar baja |
| Venta | Inicio → Nueva venta → seleccionar cliente opcional en efectivo/libros → pago → historial | Descuento de stock y pago atómicos; cliente elegido activo; tarjeta simulada requiere cliente/referencia | Venta en efectivo de una unidad: stock 3→2, datos e importe en historial |
| Devolución | Inicio → Historial de ventas → detalle → Devolver venta → motivo/aptitud → confirmar | Total, hasta +7 días inclusive, medio/importe pagados; repone una sola vez | No mantiene CONFIRMADA/stock 2; Sí cambia DEVUELTA/stock 3 y persiste al reabrir |

Los detalles técnicos y decisiones tienen ubicación principal en [Guía](GUIA_TPO.md#administraciones); los pasos y resultados, en [Pruebas](PRUEBAS.md). La asociación socios conserva cliente_id; no es otra ficha de persona ni otra administración elegida.

## Orden de trabajo para cerrar

| Orden | Trabajo / dependencia | Criterio de cierre y procedimiento |
|---|---|---|
| 1 | Confirmar con cátedra el cómputo elegido de seis interfaces y las tres prácticas | Registrar respuesta; no volver a elegir entidades ya acordadas salvo indicación docente |
| 2 | Ejecutar en IntelliJ el recorrido de seis interfaces con base nueva | Todas las filas [3→2→2→3 y reapertura](PRUEBAS.md#recorrido-swing) observadas, total/DNI/ID de venta anotados |
| 3 | Completar administraciones, errores, cancelaciones y préstamos de cliente inactivo | [Administraciones](PRUEBAS.md#administraciones) y [errores](PRUEBAS.md#errores) con acciones observadas; este paso acredita las operaciones no ejercitadas en el recorrido central |
| 4 | Si aparece un defecto, documentar reproducción y corregirlo en otra iteración | Volver a correr prueba afectada y [suite](PRUEBAS.md#automatizadas), luego repetir clics; no hay un defecto nuevo confirmado por el bloqueo del escritorio |
| 5 | Actualizar UML de implementación final | Código estabilizado, mínimo 12 clases, relaciones/estados y arquitectura reales; original preservado hasta reemplazo acordado |
| 6 | Preparar informe final de ~10 páginas | Depende de UML y capturas reales: propuesta vigente, diagrama, estructura y pantallas, sin reutilizar PDF anecdótico como alcance obligatorio |
| 7 | Verificar paquete y preparar defensa/entrega | [Checklist de cierre](PRUEBAS.md#cierre), instrucciones README reproducibles en otro equipo y [preguntas de defensa](GUIA_TPO.md#defensa) |

## Correcciones de diagnósticos anteriores

El diagnóstico “Cliente sin activo, sin edición y sin administración” fue superado por baja/reactivación, edición y alta del 18/11. También quedaron superadas las propuestas de elegir una cuarta entidad y de contar cuatro operaciones individuales como cuatro administraciones. La guía vieja aún decía DNI sin unicidad: ahora la regla existe en transacciones de alta/cambio, sin UNIQUE global para preservar duplicados históricos. Título/ISBN son editables, SQLite y rehidratación están implementados; no están pendientes como en el modelo de octubre.

Los diagnósticos y resultados originales se preservan en [Historial](HISTORIAL_DE_CAMBIOS.md), rotulados por etapa. No quedan mezclados como tareas vigentes.

## Cierre para el grupo

- **Faltantes confirmados:** UML actualizado e informe final con capturas de la aplicación.
- **Verificaciones pendientes:** todos los recorridos interactivos, mensajes/diálogos, stock/historia tras reapertura visual y paquete en otro entorno. Automatización aprobada anteriormente no sustituye estas acciones.
- **Decisiones/confirmaciones:** aceptación docente del criterio de interfaces y prácticas; responsables del ensayo, UML e informe. Las cuatro entidades y las reglas de DNI/baja ya están acordadas.