# Faltantes para cumplir TP_Final_Consignas

## Criterio vigente

Este es el archivo de referencia para decidir qué falta en el proyecto. La fuente de requisitos es [TP_Final_Consignas.docx](TP_Final_Consignas.docx), especialmente sus apartados 2 y 3.

Por decisión del grupo, el PDF «Sistema de gestión de una librería comercial» y el UML anterior son antecedentes anecdóticos: **no imponen funcionalidades a desarrollar**. Se rehacerán cuando el código cumpla las consignas. No se requiere implementar compras, proveedores, papelería, roles, permisos ni ABM de empleados por aparecer en esos antecedentes. La captura de Teams describe una instancia parcial; no reemplaza los requisitos finales del DOCX.

Orden de trabajo: primero ajustar y verificar el programa, después generar el UML y el informe que describan ese programa. El alcance funcional actual incluye catálogo de libros, clientes, ventas en efectivo/tarjeta simulada, HOTSALE, devoluciones y socios/préstamos. El DOCX no prescribe esos casos específicos: pide resolver un problema de negocio en Java y cumplir los mínimos técnicos y de entrega.

## Qué falta en el código

**El punto principal por cerrar es acreditar las cuatro interfaces de ABM.** No se detectó en el relevamiento otro requisito técnico del DOCX claramente ausente. Falta además ensayar manualmente los recorridos conectados para comprobar que funcionen desde la aplicación.

La consigna dice: «6 interfaces Swing (4 de alta/baja/modificar y 2 de negocio/funcionales)». No especifica cuatro ventanas independientes, cuatro entidades ni una distribución exacta entre operaciones. Por eso el estado es **parcial**, no una afirmación de que obligatoriamente haya que crear cuatro ventanas nuevas.

Hoy existen consulta de catálogo, alta y modificación de libros mediante el mismo formulario, baja lógica desmarcando «activo» y alta/consulta de clientes. Hay comportamiento ABM, pero no quedó demostrado qué cuatro interfaces concretas se presentarán como cumplimiento. Contar paneles genéricos o pantallas de pago no resuelve ese punto.

### Trabajo propuesto para cerrar ABM

1. Identificar las cuatro interfaces que se van a demostrar y vincular cada una a un caso de alta, baja o modificación. Como alternativa concreta: alta de libro, modificación de libro, baja de libro y alta de cliente; la consulta de catálogo queda como acceso y búsqueda.
2. Revisar [CatalogoVista](../src/vista/CatalogoVista.java), [LibroFormulario](../src/vista/LibroFormulario.java) y [CatalogoControlador.editar](../src/controlador/CatalogoControlador.java). Actualmente alta y modificación reutilizan formulario; se puede mantener reutilización interna, haciendo explícitos sus modos, títulos y acciones. Para una baja identificable, agregar una acción/formulario de baja lógica con selección y confirmación, en vez de depender únicamente del checkbox en edición. Esta es una propuesta para hacer inequívoca la demostración, no una exigencia textual de ventanas separadas.
3. Revisar [ClienteVista](../src/vista/ClienteVista.java) y [ClienteControlador](../src/controlador/ClienteControlador.java) para demostrar el alta ya implementada como cuarta interfaz. No hace falta inventar proveedores o empleados para alcanzar el mínimo.
4. Conectar cualquier nueva acción con [LibreriaServicio](../src/servicio/LibreriaServicio.java) y su persistencia existente; evitar modificar solamente la tabla visual. Para baja, revisar `cambiarActividad` y la actualización de catálogo.
5. Verificar desde UI: alta válida e inválida, modificación persistida tras reinicio, baja que impida nuevas ventas/préstamos y conserve historial, alta de cliente con DNI textual. Registrar las cuatro interfaces y sus resultados. Si el inventario actual ya acredita las cuatro, no agregar pantallas redundantes.

## Matriz contra el DOCX

«Completo» indica evidencia de implementación, no aprobación docente ni prueba exhaustiva. E = pruebas ejecutadas en el relevamiento; L = lectura; M = verificación manual pendiente.

| Requisito del DOCX | Estado | Evidencia actual | Qué falta / cómo cerrarlo |
|---|---|---|---|
| Propuesta del negocio | parcial | Existe un programa con alcance funcional identificable | Redactar propuesta final a partir del programa terminado; PDF anterior no condiciona el código |
| Diagrama de clases, mínimo 12 clases | parcial | Código supera 12 clases propias; diagrama histórico no representa la versión actual | Generar nuevo diagrama después de ajustes, con clases, relaciones y responsabilidades reales |
| Código Java que resuelva el problema de negocio | completo | Venta, pago, stock, devoluciones y préstamos conectados; compila; E modelo/servicio y L listeners | M recorrido integral para confirmar comportamiento visible; no implica agregar funciones del PDF viejo |
| Al menos 3 patrones o buenas prácticas | completo | Experto en Venta/DetalleVenta; Creador en Venta.agregarLibro; MVC/separación de responsabilidades | Explicarlos con enlaces y ejemplos en informe/defensa; no hay interfaz Repository propia |
| Excepción personalizada y al menos 3 try/catch | completo | StockInsuficienteException y DevolucionNoPermitidaException; capturas en controladores de pago, catálogo y devolución | M errores visibles; evidencia L y E de reglas |
| 4 interfaces Swing de alta/baja/modificar | parcial | Catálogo, formulario reutilizado de libros, baja integrada en edición y alta de cliente | Inventariar cuatro interfaces demostrables y ajustar lo necesario según el plan ABM anterior |
| 2 interfaces Swing de negocio/funcionales | completo | Venta y devolución con sus controladores; además pago y préstamos | M venta y devolución completas; L conexión y E servicio |
| Datos guardados en .txt o BD | completo | SQLite mediante ConexionSQLite, ClienteDAO y LibreriaDAO | E bases temporales y reapertura; verificar BD incluida en entrega |
| Al menos 3 usos de dialogs | completo | Cancelar venta, confirmar devolución, formulario de libro, socio y cierre | L; M visualizar al menos tres y comprobar aceptar/cancelar |
| Documentación de aproximadamente 10 páginas | faltante | Hay guías de estudio; el PDF previo se toma como antecedente | Crear informe final PDF/DOC después de terminar ajustes |
| PDF/DOC con propuesta, UML, estructura y capturas | faltante | Material técnico disponible en GUIA_TPO.md | Reunir los cuatro componentes actualizados y capturas de la versión final |
| Código fuente y BD/archivos entregables | parcial | src, test, lib, datos y script presentes; compilación exitosa | Ensayo en carpeta limpia, driver incluido, instrucciones y base de demostración verificada |

## Orden de trabajo

1. **Cerrar el requisito de cuatro interfaces ABM.** Usar el plan concreto anterior. Es el ajuste de implementación prioritario; no comenzar por funciones del PDF histórico.
2. **Validar manualmente el programa actual.** Seguir los recorridos de [GUIA_TPO.md](GUIA_TPO.md) con `-Dtpo.db=datos/practica.db`: efectivo sin cliente, tarjeta, HOTSALE, cancelación, devolución, catálogo y préstamos. Probar también errores, aceptar/cancelar dialogs y reapertura. Si falla algún paso, corregir su controlador/servicio/DAO y volver a probar. Los tests existentes no sustituyen clics reales.
3. **Congelar el alcance que efectivamente funciona.** Enumerar casos de uso y cuatro interfaces ABM/dos funcionales, tres prácticas y tres dialogs con evidencia. No hay obligación de implementar compras, proveedores, papelería o permisos.
4. **Rehacer UML e informe final.** Una vez estable el código, redactar propuesta, diagrama con al menos 12 clases, estructura de archivos y capturas; producir PDF/DOC de aproximadamente 10 páginas. Preservar los originales como antecedentes hasta reemplazarlos expresamente.
5. **Ensayar el paquete de entrega.** Compilar y ejecutar con instrucciones desde una carpeta limpia, incluir JAR y datos, verificar persistencia tras reinicio y registrar resultados.

### Mejoras que no son exigencias explícitas del DOCX

Fecha efectiva de compra frente a borradores abiertos varios días, unicidad del DNI, advertencias serialVersionUID y respuesta de Swing durante SQL son revisiones de calidad del programa. Evaluarlas si afectan los recorridos elegidos, sin presentarlas como requisitos textuales adicionales. Vencimientos, multas y nuevas entidades no se agregan por defecto.


## Verificación ejecutada

JDK usado: `C:/Users/facub/.jdks/openjdk-26.0.2.1/bin`. Primero se intentó compilar con `-Xlint:all` en `%TEMP%/tpo-auditoria-...`: falló la escritura de EjemploSQLite.class y reportó 13 warnings de serialVersionUID en vistas. No se interpretó como defecto de negocio. Se reintentó en una carpeta temporal dentro del workspace:

```powershell
$j = 'C:/Users/facub/.jdks/openjdk-26.0.2.1/bin'
$o = 'Documentacion/.verificacion'
New-Item -ItemType Directory -Force $o | Out-Null
$f = @(Get-ChildItem src,test -Recurse -Filter *.java | ForEach-Object FullName)
& "$j/javac.exe" -encoding UTF-8 -cp 'lib/*' -d $o $f
foreach ($t in 'PruebasModelo','PruebasSQLite','PruebasServicio','PruebasVistas') {
    & "$j/java.exe" --enable-native-access=ALL-UNNAMED '-Djava.awt.headless=true' "-Djava.io.tmpdir=$((Resolve-Path $o).Path)" -cp "$o;lib/*" "pruebas.$t"
}
```

Compilación exitosa; las cuatro ejecuciones finalizaron con código 0:

| Prueba | Salida / alcance |
|---|---|
| PruebasModelo | OK: 88 verificaciones del modelo |
| PruebasSQLite | OK: 9 verificaciones de SQLite |
| PruebasServicio | OK: 86 verificaciones del servicio |
| PruebasVistas | OK: 9 vistas en tamaño inicial y ampliado; pestañas y formulario de libros |

PruebasSQLite y PruebasServicio usan Files.createTempDirectory y rutas explícitas; sus bases se eliminan en finally. Se redirigió java.io.tmpdir al workspace. No se ejecutó Main ni el script contra datos/libreria.db. Se eliminaron los artefactos auxiliares de este relevamiento después de verificar. No se ejecutó recorrido manual Swing ni se inspeccionaron filas de la base real. No afirmar que las pruebas cubren permisos, compras o toda la interacción visual.

Se leyeron DOCX mediante XML, PDF con pdftotext, captura Teams visualmente y textos SVG. Los siete documentos solicitados pudieron leerse; el PDF y SVG no tuvieron revisión visual completa. No se modificaron originales.


## Referencia del relevamiento

Rama: tpo-grupo5. Commit: ad55cbc2d74e5e697c6f42335bf05c90e75d3fc3. Los resultados anteriores corresponden al relevamiento del 17/11/2026; en esta actualización documental no se repitieron pruebas ni se modificó código. El PDF y SVG se conservan únicamente como antecedentes. Para estudiar el funcionamiento, consultar [GUIA_TPO.md](GUIA_TPO.md).

