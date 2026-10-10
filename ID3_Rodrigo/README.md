# Proyecto ID3 — Actividad 2

**Rodrigo Valdespino Vertiz · ID 00520621**  
Ingeniería en Tecnologías de la Información y Negocios Digitales  
Campus Querétaro · Séptimo semestre  
Curso: Inteligencia artificial con machine learning en Java

Implementación propia de ID3, sin bibliotecas de aprendizaje automático. Lee CSV,
calcula entropía y ganancia de información en cada nodo, construye un árbol de
decisión y compara sus predicciones con las clases de los registros utilizados.

## Abrir y ejecutar en Eclipse

Requisito: Eclipse IDE for Java Developers con Java 17 o superior disponible.
El proyecto incluye `.project`, `.classpath` y ajustes UTF-8; no requiere Maven,
plugins, conexión a Internet, servicios ni bibliotecas externas.

1. Descomprime la entrega en una carpeta local.
2. En Eclipse: **File > Import > General > Existing Projects into Workspace**.
3. Selecciona la carpeta del proyecto que contiene `.project` y pulsa **Finish**.
4. Abre `src/id3/Main.java` y usa **Run As > Java Application**.
5. La consola ejecutará `data/proporcionados/data.csv` y mostrará los cálculos,
   el árbol y los 14 resultados. El directorio de ejecución es la raíz del proyecto,
   que es el predeterminado al ejecutar esta clase en Eclipse.

Para probar otros archivos: **Run > Run Configurations > Java Application > Main >
Arguments > Program arguments**. Escribe, por ejemplo:

```text
data/proporcionados/play7.csv
```

Después repite con `data/proporcionados/Play.csv` y `data/proporcionados/play8.csv`.
Para seleccionar explícitamente una columna objetivo:

```text
data/proporcionados/play8.csv risk
```

Abre `test/id3/Id3Test.java` y ejecútalo como **Java Application** para correr
las 39 pruebas. No es una suite JUnit y no necesita activación de aserciones con `-ea`.
La compilación y las ejecuciones se verificaron en consola con el compilador de
Eclipse; la demostración dentro del IDE y la grabación del video están pendientes.

## Ejecutar desde terminal

Con Java 17 o superior, el JAR incluido permite ejecutar sin compilar. Desde la
carpeta descomprimida del proyecto:

```sh
java -Dfile.encoding=UTF-8 -jar ID3.jar
java -Dfile.encoding=UTF-8 -jar ID3.jar data/proporcionados/play7.csv
java -Dfile.encoding=UTF-8 -jar ID3.jar data/proporcionados/play8.csv
```

Para compilar el código se necesita **JDK 17 o superior**, que incluye `javac`:

```sh
# Linux/macOS
sh run.sh
sh run.sh data/proporcionados/play8.csv
sh run.sh --test
```

```bat
rem Windows
run.bat
run.bat data/proporcionados/play7.csv
run.bat --test
```

Los scripts recompilan antes de ejecutar. Si modificas el código, ejecútalo desde
Eclipse o estos scripts; el JAR entregado corresponde a la versión original.
Para reconstruir el JAR con un JDK, después de compilar:

```sh
jar --create --file ID3.jar --main-class id3.Main -C bin .
```

## Archivos y responsabilidades

| Clase | Propósito |
|---|---|
| `Main` | Recibe el archivo y la columna objetivo; ejecuta ID3, imprime el árbol, la traza y las predicciones; comunica errores. |
| `CsvReader` | Lee UTF-8; detecta coma o punto y coma; interpreta BOM, comillas escapadas y saltos de línea. |
| `Dataset` | Valida y conserva los encabezados, registros, atributos y columna objetivo. |
| `Entropy` | Calcula frecuencias, entropía de Shannon y clase mayoritaria. |
| `Id3Trainer` | Calcula entropías condicionales y ganancias; selecciona atributos y construye el árbol recursivamente. |
| `DecisionNode` | Representa hojas y decisiones; predice una clase y genera la representación textual del árbol. |
| `Id3Test` | Comprueba resultados conocidos, archivos entregados, casos límite y errores de entrada. |

```text
src/id3/                  Código Java de la aplicación (6 clases)
test/id3/Id3Test.java      Suite de 39 pruebas
data/proporcionados/      Los 4 CSV originales del ZIP, sin modificar sus bytes
data/                     Ejemplos adicionales: tenis, frutas, XOR y contradicciones
docs/Informe_ID3.pdf       Informe para entrega con portada y tabla de clases
docs/Informe_ID3.docx      Versión editable del informe
docs/guion_video.md        Pasos y explicación sugerida para la grabación
docs/Guia_grabacion_Linux_Windows.md  Preparación y verificación en ambos sistemas
docs/evidencias/           Salidas reales de consola y resultados de las pruebas
ID3.jar                   Aplicación compilada, ejecutable con Java 17+
run.sh / run.bat           Compilación y ejecución sin dependencias externas
```

## Formato de datos

- Archivo CSV en UTF-8 con una fila de encabezados únicos y registros de igual anchura.
- La **última columna** es la clase objetivo, salvo que se indique otra por nombre.
- Se acepta coma o punto y coma, detectado a partir del encabezado.
- Se permiten espacios, símbolos como `£`, comas entre comillas y comillas escapadas.
- Se eliminan espacios al inicio y al final de cada campo. Las categorías distinguen
  mayúsculas, minúsculas y acentos. Las líneas vacías se omiten.
- Los valores vacíos y `?` se rechazan con un mensaje de error; no se imputan datos.
- Los atributos se interpretan como **categorías**. Un número o un intervalo como
  `30-60` es una etiqueta; esta implementación no busca umbrales continuos ni poda.
- No se elimina ninguna columna automáticamente. Si otro archivo tiene un número
  de identificación que no debe ser atributo, exclúyelo de ese archivo de entrada.

## Cálculo y decisiones de ID3

```text
H(S) = - Σ p(c) log2(p(c))
H(Y | A) = Σ (|Sv| / |S|) H(Sv)
IG(S, A) = H(S) - H(Y | A)
```

`H(S)` mide la incertidumbre de la clase. La consola también muestra `H(A)`, la
entropía de los valores del atributo. La selección utiliza la **ganancia de
información sobre la clase**, no simplemente `H(A)`.

En cada nodo se calcula la ganancia para todos los atributos disponibles, se
elige la máxima y se elimina ese atributo de las siguientes decisiones de esa
rama. Los subconjuntos puros se convierten en hojas. Si no quedan atributos se
devuelve la clase mayoritaria, incluso cuando existen registros contradictorios.
Empates de ganancia dentro de `1e-12` conservan el primer atributo del encabezado;
empates de mayoría conservan la primera clase encontrada en el subconjunto.
Una ganancia cero no detiene el proceso: el ejemplo XOR demuestra por qué.

La API `DecisionNode.predict(Map<String, String>)` permite clasificar casos nuevos.
Si falta un atributo requerido o aparece un valor no visto, usa la mayoría del
nodo alcanzado. La consola muestra las predicciones de los datos de entrenamiento;
esa exactitud **no es una evaluación de generalización**.

## Resultados verificados

| Archivo proporcionado | Filas | Clase | H inicial | Raíz | IG de la raíz | Aciertos de entrenamiento |
|---|---:|---|---:|---|---:|---:|
| `data.csv` | 14 | Play | 0.940286 | Outlook | 0.246750 | 14/14 |
| `Play.csv` | 14 | Play | 0.940286 | Outlook | 0.246750 | 14/14 |
| `play7.csv` | 12 | Wait | 1.000000 | Patrons | 0.540852 | 12/12 |
| `play8.csv` | 14 | risk | 1.530619 | Income | 0.966324 | 14/14 |

`data.csv` y `Play.csv` son idénticos byte a byte. Los encabezados originales,
incluidos `Wait Esttimate` y `Colleteral`, se preservan tal como fueron entregados.

Las 39 pruebas pasan. Los ejemplos adicionales obtienen 14/14 (tenis en español),
6/6 (frutas), 4/4 (XOR) y 2/3 (datos contradictorios). Este último resultado es
esperado: tres registros tienen la misma entrada y distintas clases; se elige
la mayoría en lugar de prometer una separación imposible.

## Entrega conforme a la guía

El informe contiene portada, explicación y tabla de todas las clases. Las salidas
de consola están en `docs/evidencias/`. El guion indica cómo mostrar Eclipse,
estructura, entropías, árbol y ejecución con otros archivos.

Falta grabar el video en Eclipse y realizar la entrega en Brightspace. La fecha
concreta no aparece en el PDF recibido; debe consultarse en la plataforma. La guía
indica las 23:59 del día que allí se establezca.

Para practicar antes de grabar, consulta
[`docs/Guia_grabacion_Linux_Windows.md`](docs/Guia_grabacion_Linux_Windows.md).
