# Cómo probar ID3 y grabar la evidencia en Linux o Windows

La guía de la actividad exige mostrar **Eclipse o NetBeans** en el video. Puedes
comprobar los comandos en una terminal antes de grabar, pero en la grabación
muestra el IDE abierto, el proyecto, las clases y su ejecución. Esta guía usa
Eclipse porque el ZIP ya incluye su configuración de importación.

## Preparación común

1. Descarga y descomprime `Proyecto_ID3_Rodrigo_00520621.zip`.
2. Identifica la carpeta interior `Proyecto_ID3_Rodrigo_00520621`. Debe contener
   **`.project`**, `src`, `test`, `data`, `ID3.jar`, `run.sh` y `run.bat`.
   En algunos exploradores `.project` aparece oculto; los otros archivos permiten
   comprobar que estás en el directorio correcto.
3. Instala Java 17 o superior. Para ejecutar `ID3.jar` basta el entorno de Java.
   Para compilar con los scripts se necesita un **JDK** que incluya `javac`.
   Eclipse IDE for Java Developers necesita un JDK configurado para compilar.
4. Abre la terminal **dentro de la carpeta interior**, no en la que contiene el ZIP.

### Linux

En una terminal:

```sh
java -version
javac -version
pwd
ls .project ID3.jar data/proporcionados/data.csv
java -Dfile.encoding=UTF-8 -jar ID3.jar
java -Dfile.encoding=UTF-8 -jar ID3.jar data/proporcionados/play7.csv
java -Dfile.encoding=UTF-8 -jar ID3.jar data/proporcionados/play8.csv
sh run.sh --test
```

Si falta el JDK en Ubuntu o Debian, se puede instalar con el gestor de paquetes
del sistema, por ejemplo `sudo apt install openjdk-21-jdk`. Después verifica otra
vez `java -version` y `javac -version`. El comando `sh run.sh --test` también
compila el código antes de ejecutar las pruebas.

### Windows, PowerShell

Abre PowerShell en la carpeta interior descomprimida; en el Explorador de archivos
puedes escribir `powershell` en la barra de direcciones de esa carpeta.

```powershell
java -version
javac -version
Get-Location
Test-Path .project
Test-Path data\proporcionados\data.csv
java -Dfile.encoding=UTF-8 -jar .\ID3.jar
java -Dfile.encoding=UTF-8 -jar .\ID3.jar data\proporcionados\play7.csv
java -Dfile.encoding=UTF-8 -jar .\ID3.jar data\proporcionados\play8.csv
.\run.bat --test
```

`Test-Path` debe responder `True`. Si Java no aparece, instala un JDK 17 o 21,
cierra y abre PowerShell y repite las comprobaciones. Si ya tienes JDK pero
`javac` no aparece, verifica la configuración de `PATH` para el directorio `bin`
del JDK. `run.bat` configura UTF-8 para que se vean los acentos y símbolos.
En Símbolo del sistema (CMD) usa `run.bat --test`, sin el prefijo `.\`.

### Resultados que debes obtener

| Ejecución | Raíz | Resultado final de entrenamiento |
|---|---|---|
| Sin argumentos: `data.csv` | `Outlook` | `14/14 (100.00%)` |
| `Play.csv` | `Outlook` | `14/14 (100.00%)` |
| `play7.csv` | `Patrons` | `12/12 (100.00%)` |
| `play8.csv` | `Income` | `14/14 (100.00%)` |

La ejecución de pruebas debe terminar con `Resultado: 39 aprobadas, 0 fallidas.`
Para `Play.csv`, pasa `data/proporcionados/Play.csv` en Linux o
`data\proporcionados\Play.csv` en Windows. Respeta exactamente las mayúsculas
en Linux. `data.csv` y `Play.csv` son copias idénticas incluidas en el ZIP original.

## Mostrarlo en Eclipse en cualquiera de los dos sistemas

1. Inicia **Eclipse IDE for Java Developers**. Muestra la ventana, el nombre
   del IDE y, si puedes, **Help > About Eclipse IDE**; indica que usas Java 17
   o superior y que este proyecto no requiere bibliotecas externas.
2. Elige **File > Import > General > Existing Projects into Workspace**.
   Selecciona como raíz la carpeta que contiene `.project` y pulsa **Finish**.
   Si ya aparece importado, ábrelo en **Package Explorer**.
3. Expande `src/id3`, `test/id3`, `data/proporcionados` y `docs`. Muestra que
   Eclipse no presenta errores de compilación. Para ver código, abre `Main`,
   `CsvReader`, `Dataset`, `Entropy`, `Id3Trainer` y `DecisionNode`.
4. En cada archivo, señala una parte concreta: `Main.run` organiza la ejecución;
   `CsvReader.read` carga el CSV; `Dataset` valida registros; `Entropy.of`
   calcula la incertidumbre; `Id3Trainer.informationGain` y `build` eligen el
   atributo y construyen las ramas; `DecisionNode.render` imprime el árbol y
   `predict` permite clasificar un caso.
5. Abre `Main.java` y usa **Run As > Java Application**. La consola debe indicar
   `Archivo: data/proporcionados/data.csv`, `H(S)=0.940286`, `ELEGIDA: Outlook`
   y el resumen `14/14`.
6. Usa **Run > Run Configurations > Java Application > Main > Arguments**.
   Escribe `data/proporcionados/play7.csv` en **Program arguments** y pulsa
   **Run**. Muestra `ELEGIDA: Patrons` y `12/12`. Repite con
   `data/proporcionados/play8.csv`: `ELEGIDA: Income`, tres clases y `14/14`.
   La ruta `data/proporcionados/Play.csv` repite los resultados del primero.
7. Ejecuta `test/id3/Id3Test.java` con **Run As > Java Application** y muestra
   `39 aprobadas, 0 fallidas`. Es una clase Java común, no una prueba JUnit.

Las rutas en **Program arguments** usan `/` también en Windows; Java las acepta.
Si aparece `Error al procesar ...`, abre **Run Configurations > Arguments >
Working directory** y elige **Default** o la raíz del proyecto. Si Eclipse
señala problemas con Java, revisa **Project > Properties > Java Build Path >
Libraries** y selecciona un JDK 17 o superior; el proyecto compila a Java 17.

## Para evitar el problema de la entrega anterior

Antes de ejecutar, dedica tiempo en el propio video a:

1. **Nombrar y mostrar el IDE**. Abre su menú About y di qué versión de Java
   y qué tecnologías usaste: Java, CSV y un algoritmo ID3 implementado desde cero.
2. **Recorrer la estructura visible**. Expande las carpetas `src`, `test`,
   `data/proporcionados` y `docs`; señala cuáles contienen código, pruebas,
   los archivos recibidos y el informe.
3. **Explicar código concreto**. Abre las clases y señala los métodos mencionados
   arriba. Sigue el flujo desde el CSV hasta las predicciones; explica cómo se
   decide una rama con la ganancia de información.
4. **Ejecutar las tres funcionalidades evaluadas**: mostrar resultado correcto,
   cálculo de entropía por variable y ejecución con otros CSV. Detén la grabación
   unos segundos en las partes importantes de la consola para que sean legibles.
5. **Mostrar la prueba**: ejecuta `Id3Test` y enseña el resumen final.

Una captura de la consola sola o la tabla del informe no muestran cómo se
organiza y funciona el código. El comentario del asesor pedía que esa explicación
apareciera **también en el video**.

Puedes seguir el texto sugerido en `docs/guion_video.md`. Graba tu voz y pantalla,
reproduce el archivo final para verificar que se ven el IDE y la consola, y
consulta la fecha de entrega en Brightspace.
