# Pruebas del TPO

[Inicio](../README.md) · [Estado](ESTADO_Y_PENDIENTES.md) · [Guía](GUIA_TPO.md) · [Historial](HISTORIAL_DE_CAMBIOS.md)

## Cómo interpretar la evidencia

- **Automatizada de modelo/servicio/SQLite:** comprueba reglas, escrituras y reapertura; no acredita pantallas.
- **Headless:** comprueba geometría de componentes, no navegación mediante clics.
- **Lectura:** acredita listeners, métodos y ramas; no es ejecución.
- **Swing interactivo:** requiere observar la ventana y actuar en ella. No hay acciones visuales aprobadas registradas.

Los resultados del 18/11 se conservan como antecedentes; esta reorganización del 19/11 no ejecutó Java, pruebas ni Main. Las rutas y comandos se contrastaron con fuentes, JAR y pruebas presentes. Nunca usar datos/libreria.db para verificar.

<a id="automatizadas"></a>
## Pruebas automatizadas

Requisitos: JDK 17+, dependencia SQLite del repositorio y directorio de trabajo grupo 5, según [README](../README.md). Son nueve clases con main y controles propios (AssertionError); no requieren JUnit ni -ea. Las pruebas de persistencia crean bases temporales y las eliminan al terminar. No tomar la propiedad tpo.db como sustituto de revisar esa preparación.

PowerShell: salida y temporales aislados de la base real. Ajustar la ruta del JDK si es otro equipo. El directorio de salida y el temporal pueden conservarse para inspeccionar una ejecución fallida.

~~~powershell
$jdkTpo = 'C:/Users/facub/.jdks/openjdk-26.0.2.1/bin'
$salidaTpo = Join-Path $pwd 'out/verificacion-documentada'
$temporalTpo = Join-Path $salidaTpo 'temporales'
New-Item -ItemType Directory -Force $salidaTpo,$temporalTpo | Out-Null
$fuentesTpo = @(Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object FullName)
& "$jdkTpo/javac.exe" -encoding UTF-8 -cp 'lib/*' -d $salidaTpo $fuentesTpo
if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación' }
$casosTpo = @('PruebasModelo','PruebasSQLite','PruebasServicio','PruebasCategorias',
    'PruebasEditoriales','PruebasClientesEstado','PruebasClientesEdicion','PruebasClientesAlta','PruebasVistas')
foreach ($casoTpo in $casosTpo) {
    & "$jdkTpo/java.exe" --enable-native-access=ALL-UNNAMED "-Djava.io.tmpdir=$temporalTpo" -Djava.awt.headless=true -cp "$salidaTpo;lib/*" "pruebas.$casoTpo"
    if ($LASTEXITCODE -ne 0) { throw "Falló $casoTpo" }
}
~~~

Alternativa shell Unix, después de compilar según [Guía: SQLite](GUIA_TPO.md#sqlite):

~~~sh
mkdir -p out/temporales
for caso in PruebasModelo PruebasSQLite PruebasServicio PruebasCategorias PruebasEditoriales PruebasClientesEstado PruebasClientesEdicion PruebasClientesAlta PruebasVistas
do
  java --enable-native-access=ALL-UNNAMED "-Djava.io.tmpdir=$PWD/out/temporales" -Djava.awt.headless=true -cp 'out:lib/*' "pruebas.$caso" || exit 1
done
~~~

En IntelliJ ejecutar el main de cada clase de test/pruebas con el mismo JAR; para PruebasVistas usar -Djava.awt.headless=true. La configuración Main del recorrido visual debe permanecer sin headless.

### Resultados registrados, no repetidos el 19/11

Última suite completa registrada: cierre del alta de Clientes, 18/11/2026, OpenJDK 26.0.2.1, árbol de trabajo con cambios locales de Clientes. Compilación UTF-8 y todas las entradas terminaron con código 0. [Registro detallado y archivos afectados](HISTORIAL_DE_CAMBIOS.md#alta-clientes).

| Clase | Resultado registrado | Alcance |
|---|---|---|
| PruebasModelo | 88 verificaciones, código 0 | Importes/estados/validaciones; modelo sin efectos de stock por confirmar/devolver |
| PruebasSQLite | 9 verificaciones, código 0 | Clientes y reapertura SQLite |
| PruebasServicio | 86 verificaciones, código 0 | Venta, devolución, catálogo, préstamos, rollback y reapertura |
| PruebasCategorias | 14 verificaciones, código 0 | Administración y bloqueo por referencias |
| PruebasEditoriales | 20 verificaciones, código 0 | Renombrado, referencias y baja transaccional |
| PruebasClientesEstado | 20 verificaciones, código 0 | Migración anterior, baja con préstamo, devolución y reactivación |
| PruebasClientesEdicion | 20 verificaciones, código 0 | DNI, estado, historia y duplicados antiguos preparados directamente en SQL |
| PruebasClientesAlta | 17 verificaciones, código 0 | DNI con ceros, duplicados activos/inactivos, concurrencia y Hacerse socio |
| PruebasVistas | 12 vistas, tamaños inicial/ampliado, pestañas y formulario; código 0 | Distribución headless; ningún clic acreditado |

Los resultados anteriores por etapa, incluida la cobertura detallada de los 88 controles originales, están en [Historial](HISTORIAL_DE_CAMBIOS.md#modelo-20261031). El diagnóstico inicial registró 13 advertencias serialVersionUID con -Xlint:all y un intento fallido por escritura en TEMP; no confundirlo con la compilación del modelo de octubre, sin advertencias, ni afirmar ausencia de warnings de toda la aplicación actual.
<a id="recorrido-swing"></a>
## Recorrido de seis interfaces y registro del intento del 18/11/2026

**Resultado general: pendiente por bloqueo del control nativo. No aprobado ni fallido como recorrido del producto.** Rama `tpo-grupo5`, referencia `ba6bebcc79403c977a0231c59b3dee2ed9755394`, con cambios locales de Clientes y pruebas de las iteraciones anteriores. Se conservaron; el único archivo editado en esta verificación es este Markdown.

Se volvió a intentar Computer Use, inicializando `@oai/sky` y llamando `sky.list_windows()`. Resultado exacto:

```text
Computer Use native pipe is unavailable: failed to connect native pipe: El sistema no puede encontrar el archivo especificado. (os error 2)
```

**Último paso observado:** ninguno de la aplicación; el fallo ocurrió al enumerar ventanas, antes de observar Inicio. No se ejecutó Main, no se creó la base propuesta ni se interactuó con ninguna pantalla. No se atribuye el error del entorno a un defecto del TPO. No hubo comprobaciones visuales de stock, total, cliente o historial; tampoco existe un ID de venta observado. No se detectaron defectos funcionales nuevos porque el recorrido no pudo comenzar.

Las pruebas anteriores de servicio/SQLite y geometría siguen siendo antecedentes, no resultados de este recorrido. No se repitieron ni se usaron como sustituto de clics. `datos/libreria.db` quedó reservado para el proyecto y no se abrió en esta iteración.

### Configuración lista para IntelliJ

Crear una configuración de ejecución Java/Application:

- Clase principal: `aplicacion.Main`.
- SDK: JDK 17 o superior; el usado en las verificaciones anteriores fue OpenJDK 26.0.2.1.
- Classpath: módulo con `src` y `lib/sqlite-jdbc-3.50.3.0.jar`.
- Directorio de trabajo: la carpeta `grupo 5` del proyecto abierto (la que contiene src, lib y datos).
- Opciones de VM: `-Dtpo.db=datos/recorrido-swing-20261118.db --enable-native-access=ALL-UNNAMED`.
- Argumentos del programa: ninguno. No activar headless.

Se comprobó con Test-Path que `datos/recorrido-swing-20261118.db` no existía al preparar este registro. Debe seguir siendo nueva al primer arranque. Si alguien ya la usó, elegir otro nombre de archivo nuevo para todo el caso, sin borrar una base existente. **Al reabrir en el paso 6 usar exactamente la misma configuración, base y directorio de trabajo.** No ejecutar el ejemplo de consola ni precargar estos datos por SQL/servicio: se deben ingresar desde Swing.

### Datos de prueba planificados, aún no ingresados

| Dato | Valor |
|---|---|
| Categoría | Categoría prueba recorrido |
| Editorial | Editorial prueba recorrido |
| Título | Libro prueba recorrido |
| ISBN-13 | `9781234567897` (dígito verificador válido; confirmar que esté libre en el catálogo de prueba) |
| Autor | Autor prueba recorrido |
| Precio / stock / actividad | `1000` / `3` / activo |
| Cliente | Cliente prueba recorrido |
| DNI propuesto | `001887766` (texto; debe conservar ambos ceros iniciales; confirmar disponibilidad en alta) |
| Cantidad vendida | 1 |
| Descuento | Ninguno; no aplicar HOTSALE |
| Medio / recibido | Efectivo / `1000` |
| Motivo de devolución | Devolución de prueba del recorrido |
| Condición | Marcar «Todos los libros están sin uso y aptos para reventa» |

DNI efectivamente usado: **pendiente: __________**. Total efectivamente mostrado: **pendiente: __________**. ID de venta mostrado: **pendiente: __________**. Nombre de base efectivamente utilizada: **pendiente: __________**. Operador y fecha/hora: **__________**.

Los valores anteriores son entradas planificadas y resultados esperados, no observaciones. Si ISBN/DNI ya están ocupados, anotarlo y elegir otro libre antes de continuar; registrar el valor realmente usado.

### Tabla de aceptación a completar mediante Swing

Cambiar el estado a aprobado solo después de observar el resultado en esa pantalla. Si difiere, marcar fallido y registrar dato observado, acción exacta y efecto; no corregir el código en esta iteración.

| Paso | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| 0. Arranque Main con base nueva | Inicio visible; base de prueba seleccionada por configuración | No ejecutado: enumeración nativa bloqueada antes de observar ventanas. Completar: ______ | pendiente |
| 1a. Administrar categorías → Nueva categoría | «Categoría prueba recorrido» aparece en listado antes de crear libro | Sin interacción. Completar: ______ | pendiente |
| 1b. Administrar editoriales → Nueva editorial | «Editorial prueba recorrido» aparece en listado antes de crear libro | Sin interacción. Completar: ______ | pendiente |
| 2. Administrar libros → Nuevo libro → volver al catálogo | Título/ISBN/autor/categoría/editorial correctos, activo, precio $1.000 y stock 3 | Sin interacción. Completar datos y stock observados: ______ | pendiente |
| 3. Administrar clientes → Nuevo cliente | Cliente listado activo con DNI `001887766`, conservando ceros | Sin interacción. Completar DNI/estado: ______ | pendiente |
| 4a. Nueva venta → buscar cliente → agregar una unidad | Cliente correcto, libro/cantidad 1, total $1.000 sin descuento | Sin interacción. Completar total: ______ | pendiente |
| 4b. Ir al pago → efectivo 1000 → Confirmar pago y venta → historial/detalle | ID visible, cliente/libro correctos, cantidad 1, importe $1.000 y CONFIRMADA | Sin interacción. Completar ID/detalle/estado: ______ | pendiente |
| 4c. Volver al catálogo | Stock 2 | Sin interacción. Completar stock: ______ | pendiente |
| 5a. Misma venta → Devolver venta → motivo/aptitud → Confirmar devolución total → No | Venta sigue CONFIRMADA, sin reintegro registrado | Sin interacción. Completar ID/estado: ______ | pendiente |
| 5b. Catálogo después de No | Stock sigue 2 | Sin interacción. Completar stock: ______ | pendiente |
| 5c. Misma venta → repetir devolución → Sí | Venta DEVUELTA, reintegro $1.000 por EFECTIVO | Sin interacción. Completar ID/estado/reintegro: ______ | pendiente |
| 5d. Catálogo después de Sí | Stock 3 | Sin interacción. Completar stock: ______ | pendiente |
| 6a. Cerrar/reabrir con la misma base → Historial → mismo ID | Venta sigue DEVUELTA; cliente/detalles/reintegro conservados | Sin interacción. Completar ID/estado: ______ | pendiente |
| 6b. Catálogo tras reapertura | Libro activo, mismas referencias y stock 3 | Sin interacción. Completar stock/datos: ______ | pendiente |
| 6c. Administrar clientes tras reapertura | Mismo cliente activo y DNI con ceros iniciales | Sin interacción. Completar DNI/estado: ______ | pendiente |

### Recorrido operativo para completar la tabla

1. Ejecutar la configuración indicada. Desde Inicio abrir Administrar categorías, pulsar Nueva categoría, guardar el nombre indicado y observar su fila. Volver a Inicio y repetir en Administrar editoriales. Todavía no crear el libro.
2. Inicio → Administrar libros → Nuevo libro. Completar todos los datos de la tabla; mantener activo y stock 3. Guardar y localizarlo en el catálogo por título o ISBN. Registrar lo que muestra la fila, no solo lo escrito en el formulario.
3. Inicio → Administrar clientes → Nuevo cliente. Completar nombre/DNI y guardar. Observar estado Activo y ceros en la tabla.
4. Inicio → Nueva venta → Buscar cliente: buscar por el DNI efectivamente usado, seleccionar y usarlo. Buscar el libro, seleccionar cantidad 1 y agregar. Anotar total visible. Ir al pago → Efectivo → recibido igual al total → Confirmar pago y venta. Desde Historial de ventas abrir la operación y anotar ID, cliente, libro, cantidad, importe y CONFIRMADA. Volver a catálogo y anotar stock 2.
5. Historial de ventas → seleccionar **el mismo ID** → detalle → Devolver venta. Escribir motivo, marcar aptos y pulsar Confirmar devolución total. En confirmación elegir **No**. Volver al detalle para observar CONFIRMADA y al catálogo para observar 2. Reabrir esa misma venta y repetir motivo/aptitud si hiciera falta; confirmar con **Sí**. Observar DEVUELTA y reintegro $1.000/EFECTIVO, luego stock 3 en catálogo.
6. Cerrar la ventana. Ejecutar otra vez la misma configuración sin cambiar la ruta de base. Abrir el mismo ID en historial y observar DEVUELTA/reintegro; consultar libro con stock 3 y cliente con DNI intacto desde sus administraciones.

Aprobación global: todas las filas del caso observadas y coincidentes. Un resultado pendiente impide marcar el recorrido completo aprobado. Defecto encontrado, si corresponde: **paso/acción: ______; esperado: ______; observado: ______; efecto en stock/historial: ______; ID/base: ______**.

### Alcance que acredita este caso

Al completarse, ejercita altas y consultas de Categorías, Editoriales, Libros y Clientes; venta en efectivo y devolución total con cancelación inicial y persistencia tras reapertura. **No acredita edición/baja de categorías/editoriales, edición/baja/reactivación de clientes ni edición/baja de libros**, porque esas acciones no forman parte del caso. Actualmente no se acredita ninguna acción visual nueva: todas permanecen pendientes por el bloqueo registrado.

<a id="administraciones"></a>
## Verificaciones administrativas pendientes

Usar una base nueva para cada conjunto independiente, con Main y configuración del recorrido anterior. Reabrir cada conjunto con su misma base. Usar nombres/DNI libres diferentes de la precarga. Las acciones siguientes están conectadas por lectura y respaldadas por pruebas automáticas, pero **ninguna fila acredita clics**. Registrar ID si se muestra, valores antes/después, operador, fecha y archivo de base. Para un defecto registrar pasos mínimos, esperado, observado y efecto en stock/historia.

### Libros

| Acción | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| Inicio → Administrar libros → Nuevo libro; completar todos los campos | Alta activa, referencias y stock visibles en catálogo | No observado desde Swing | pendiente |
| Buscar por título/ISBN, consultar activo e inactivo | Consulta administrativa, no solo selector de venta | No observado desde Swing | pendiente |
| Ordenar tabla, seleccionar fila → Editar libro; corregir precio/título/ISBN/autores | Se edita la fila elegida; detalles de ventas previas preservados | No observado desde Swing | pendiente |
| Editar → desmarcar Activo; consultar y reabrir | Baja lógica persistente, visible en catálogo, excluido de nuevas ventas/préstamos | No observado desde Swing | pendiente |
| Editar → activar; cancelar/cerrar una edición posterior | Reactivación persiste; la edición cancelada no escribe | No observado desde Swing | pendiente |

### Categorías

| Acción | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| Inicio → Administrar categorías → Nueva categoría «Novlea prueba» | Listada antes de asociar libros | No observado desde Swing | pendiente |
| Probar nombre vacío y otro ya existente (por ejemplo Novela si figura en precarga) | Rechazo sin nueva fila; mismo criterio exacto que formulario de libro | No observado desde Swing | pendiente |
| Asignar a libro activo; ordenar listado y renombrar a «Novela prueba» | Mismo ID; catálogo refleja corrección en sesión y reapertura | No observado desde Swing | pendiente |
| Eliminar con referencia activa; desactivar libro y repetir | Ambos bloqueados; mensaje de reasignación y vínculo preservado | No observado desde Swing | pendiente |
| Reasignar libro a otra categoría; eliminar → No y luego cerrar confirmación | Categoría libre sigue listada sin escritura | No observado desde Swing | pendiente |
| Repetir baja → Sí; reabrir | Categoría ausente y libro conserva nueva asignación | No observado desde Swing | pendiente |

### Editoriales

| Acción | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| Inicio → Administrar editoriales → Nueva editorial con nombre exclusivo de prueba | Listada aun sin libros | No observado desde Swing | pendiente |
| Nombre vacío/duplicado y cancelar/cerrar formulario | Rechazo o descarte sin escritura | No observado desde Swing | pendiente |
| Asignar dos libros, ordenar tabla y editar nombre; volver a catálogo y reabrir | Mismo ID; ambos libros muestran el nombre nuevo | No observado desde Swing | pendiente |
| Baja con libro activo asociado; reasignar ese y dejar solo otro inactivo | Baja bloqueada en ambos escenarios sin romper referencias | No observado desde Swing | pendiente |
| Reasignar último libro; baja → No/cerrar | Editorial conservada | No observado desde Swing | pendiente |
| Baja → Sí y reabrir | Editorial ausente; nuevas asignaciones de libros intactas | No observado desde Swing | pendiente |
| Dos instancias, misma base de prueba: abrir confirmación de baja libre, asignar libro desde la otra antes de aceptar | La transacción vuelve a verificar y rechaza la baja | No observado desde Swing | pendiente |

### Clientes, asociación y préstamos

| Acción | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| Inicio → Administrar clientes → Nuevo cliente con DNI libre que comienza con 0 | Activo, ceros conservados; solo ficha, sin socio ni venta; reapertura igual | No observado desde Swing | pendiente |
| Nuevo cliente → Cancelar o cerrar | Ninguna ficha creada | No observado desde Swing | pendiente |
| Alta con DNI activo/inactivo desde Administración y desde Venta | Mensaje buscar existente/reactivar; sin duplicación ni pérdida del borrador | No observado desde Swing | pendiente |
| Nueva venta → Nuevo cliente → Guardar y usar con DNI libre | Cliente activo creado y asignado al borrador | No observado desde Swing | pendiente |
| Hacerse socio: DNI de un único activo; repetir | Asociación por mismo ID; segundo intento informa ya socio | No observado desde Swing | pendiente |
| Hacerse socio: DNI libre y nombre válido / nombre vacío | Crea ficha+asociación juntas / rechaza sin creación parcial | No observado desde Swing | pendiente |
| Hacerse socio: DNI de inactivo | Indica reactivación; no activa ni asocia implícitamente | No observado desde Swing | pendiente |
| En base histórica de prueba con DNI repetido, intentar Hacerse socio | Ambigüedad explícita, no elige la primera ficha | No observado desde Swing | pendiente |
| Cliente activo asociado → Préstamos y devoluciones → prestar libro | Stock −1 y préstamo abierto | No observado desde Swing | pendiente |
| Administrar clientes → baja → No/cerrar; repetir → Sí con préstamo abierto | Cancelar conserva activo; confirmar inactiva con mismo ID, socio y préstamo | No observado desde Swing | pendiente |
| Reabrir; buscar al inactivo para nueva venta/préstamo | Excluido de selectores; no admite nueva operación | No observado desde Swing | pendiente |
| Venta en preparación con cliente; desactivarlo desde otra sesión; confirmar pago | Rechazo claro, borrador corregible; stock e historia sin cambios | No observado desde Swing | pendiente |
| Venta efectivo sin cliente | Sigue permitida | No observado desde Swing | pendiente |
| Préstamos y devoluciones → historial → préstamo original del inactivo → devolver; repetir | Accesible aunque no figure en selector; repone stock una sola vez | No observado desde Swing | pendiente |
| Administrar clientes → reactivar → nuevo préstamo | Mismo ID/asociación habilitados; préstamo nuevo permitido | No observado desde Swing | pendiente |
| Ordenar tabla → seleccionar socio con venta/préstamo previos → Editar cliente | Formulario de la fila correcta, nombre y DNI precargados | No observado desde Swing | pendiente |
| Corregir nombre y DNI a uno libre con ceros; volver a listados/historial y reabrir | Mismo ID/vínculos; datos nuevos visibles; estado conservado | No observado desde Swing | pendiente |
| Editar ambos campos con DNI ocupado por activo y luego por inactivo | Rechazo atómico: ambos campos originales intactos | No observado desde Swing | pendiente |
| Editar solo nombre de ficha con DNI duplicado histórico; luego corregir DNI a uno libre | Permitidos por ID sin afectar otra ficha ni historia | No observado desde Swing | pendiente |
| Editar cliente inactivo; cancelar/cerrar otra edición | Guarda sin activarlo; cancelar/cerrar descarta propuesta | No observado desde Swing | pendiente |

Duplicados históricos: no prepararlos mediante alta normal (ahora debe rechazarlos). [PruebasClientesEdicion](../test/pruebas/PruebasClientesEdicion.java) prepara registros directamente en su base temporal. Para ensayo manual usar una copia preparada exclusivamente para pruebas, preservando todos los IDs. La migración de esquema anterior y dos altas simultáneas tienen evidencia automatizada; las tablas anteriores no convierten esas pruebas en interacción.

<a id="errores"></a>
## Negocio, errores y diálogos adicionales

| Acción | Resultado esperado | Resultado observado | Estado |
|---|---|---|---|
| Efectivo sin cliente, importe mayor al total | Venta confirmada, vuelto correcto y stock −cantidad | No observado desde Swing | pendiente |
| Efectivo insuficiente, venta vacía o cantidad sin stock | Error claro, operación no confirmada y stock intacto | No observado desde Swing | pendiente |
| Tarjeta sin cliente o referencia vacía; corregir con cliente activo y referencia DEMO-01 | Rechaza incompleto; acepta simulación válida sin cobrar banco real | No observado desde Swing | pendiente |
| Libro precio 10.05, HOTSALE; repetir código, quitar y reaplicar | Total 9.04; no acumula descuento; quitar/restaurar recalcula | No observado desde Swing | pendiente |
| Devolver venta con descuento y efectivo entregado de más | Reintegro igual a total pagado con descuento, no al entregado; mismo medio | No observado desde Swing | pendiente |
| Devolución con motivo vacío, no aptos o segundo intento sobre DEVUELTA | Mensaje sin doble reintegro ni reposición | No observado desde Swing | pendiente |
| Devolución fuera de 7 días en base de prueba preparada | Rechazo por plazo sin cambiar estado/stock; límites día 7/8 cubiertos por modelo | No observado desde Swing | pendiente |
| Libro con ISBN duplicado, precio inválido o stock inválido | Error y formulario corregible sin fila nueva | No observado desde Swing | pendiente |
| Cancelar venta → No/cerrar, luego Sí; reabrir historial | Elegir No conserva borrador; Sí guarda CANCELADA sin mover stock | No observado desde Swing | pendiente |
| Cerrar ventana con borrador → No y luego Sí | Elegir No mantiene sesión; Sí descarta borrador no persistido | No observado desde Swing | pendiente |
| Abrir alta/edición de libro, Hacerse socio y confirmación de devolución | Tres diálogos observados; aceptar/cancelar con efectos correctos | No observado desde Swing | pendiente |

<a id="cierre"></a>
## Verificación del paquete de entrega

1. En otra configuración o equipo, abrir la raíz y seguir README con JDK/JAR declarados y base nueva.
2. Ejecutar la suite; registrar fecha, rama/commit, cambios locales, JDK, comando, salida y código real.
3. Completar recorrido de seis interfaces y tablas administrativas/errores. Pendiente no equivale a aprobado ni a defecto.
4. Ante discrepancia, registrar reproducción y revisar la corrección en otra iteración.
5. Capturar las pantallas definitivas durante recorridos aprobados para el informe; comparar UML con el índice y código de la guía. Captura de Teams no es captura de la aplicación.
6. Revisar informe de aproximadamente diez páginas con propuesta actual, UML de al menos doce clases, estructura y capturas; confirmar interpretación de seis interfaces y tres prácticas con la cátedra.