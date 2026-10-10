# Guion sugerido para el video — 7 a 9 minutos

La guía pide evidencia en Eclipse o NetBeans. Usa Eclipse con el proyecto
importado; graba tu pantalla y tu explicación. Este guion no sustituye el video.

## 1. Presentación e IDE (45 segundos)

Muestra la portada del informe y di:

> Mi nombre es Rodrigo Valdespino Vertiz, ID 00520621. Estudio Ingeniería en
> Tecnologías de la Información y Negocios Digitales en campus Querétaro,
> séptimo semestre. Esta es la Actividad 2 de Inteligencia artificial con
> machine learning en Java: implementación del algoritmo ID3.

Mantén **Eclipse visible** mientras hablas. Abre **Help > About Eclipse IDE**
y di cuál es el IDE y qué JDK 17 o superior tienes instalado. Señala la vista
**Package Explorer**, el editor Java y la **Console**. Explica que el algoritmo
está programado desde cero en Java, lee CSV y no depende de bibliotecas de
aprendizaje automático. Los scripts de terminal son opcionales.

## 2. Estructura y funcionamiento del código (2 minutos)

Expande `src/id3`, `test/id3`, `data/proporcionados` y `docs` en **Package
Explorer**. Di que contienen, respectivamente, el algoritmo, sus pruebas,
los cuatro CSV originales y el informe. Abre la tabla de clases del informe;
después recorre las clases **en el editor** y señala estos métodos:

| Archivo abierto | Método o parte visible | Explicación en voz alta |
|---|---|---|
| `Main.java` | `run` | Recibe el archivo y objetivo, entrena y muestra el resultado. |
| `CsvReader.java` | `read` / `parse` | Carga CSV y separa columnas y registros. |
| `Dataset.java` | Constructor | Valida encabezados, registros y columna que se intenta predecir. |
| `Entropy.java` | `of` | Calcula frecuencias y entropía de Shannon. |
| `Id3Trainer.java` | `informationGain` / `build` | Compara atributos por ganancia y crea ramas recursivamente. |
| `DecisionNode.java` | `render` / `predict` | Dibuja el árbol en texto y clasifica usando las ramas. |
| `Id3Test.java` | `main` | Verifica resultados conocidos y los archivos de prueba. |

Sigue con el cursor el flujo del programa mientras resumes:

> Main recibe la ruta del CSV y llama a CsvReader. Dataset comprueba que los
> datos estén completos. Id3Trainer usa Entropy para calcular qué atributo
> separa mejor las clases y construye DecisionNode hasta crear hojas. Por
> último, Main imprime el árbol y compara sus predicciones con las clases.
> Id3Test comprueba cálculos y casos límite.

## 3. Primera ejecución y entropía (90 segundos)

Abre `Main.java` y selecciona **Run As > Java Application**.
Muestra el nombre `data/proporcionados/data.csv` en la salida y los 14 registros.
Busca el primer bloque `NODO RAÍZ`.

> Hay nueve clases Yes y cinco No. La entropía inicial es 0.940286 bits.
> Para Outlook, se ponderan las entropías de Sunny, Overcast y Rain.
> El resultado es 0.693536 y la ganancia es 0.246750.
> Se comparan las ganancias de todas las variables; Outlook tiene la mayor.

Señala las entropías de Temperature, Humidity y Wind. Distingue `H(A)` de
`H(Play | A)`: para decidir se resta esta última de `H(S)`.

Muestra el árbol:

```text
Outlook
  Sunny    -> Humidity: High -> No; Normal -> Yes
  Overcast -> Yes
  Rain     -> Wind: Weak -> Yes; Strong -> No
```

Muestra el cierre de la ejecución: **14/14** y ausencia de excepciones.
Aclaración para decir en voz alta:

> Esta coincidencia comprueba los registros con los que se construyó el árbol;
> no representa una prueba estadística de exactitud en datos desconocidos.

## 4. Otros archivos originales (90 segundos)

En **Run > Run Configurations > Java Application > Main > Arguments**, cambia
`Program arguments` y pulsa **Run** en cada caso:

1. `data/proporcionados/Play.csv`: raíz Outlook, 14/14. Explica que es una copia
   idéntica de `data.csv` incluida en el material de prueba.
2. `data/proporcionados/play7.csv`: raíz Patrons, entropía inicial 1 y ganancia
   0.540852. Muestra el árbol de restaurante y los 12/12 registros.
3. `data/proporcionados/play8.csv`: raíz Income, entropía inicial 1.530619,
   ganancia 0.966324 y 14/14. Menciona las tres clases high, moderate y low.

Las rutas son relativas a la raíz del proyecto. Los CSV se usan sin modificar.

## 5. Pruebas y cierre (60 segundos)

Ejecuta `Id3Test.java` como Java Application. Muestra el resumen:
**39 aprobadas, 0 fallidas**.

Si hay tiempo, ejecuta `data/xor.csv` para explicar que ID3 debe continuar aunque
la ganancia inicial sea cero. También puedes mostrar `data/contradictorios.csv`:
su resultado esperado es 2/3 porque existen entradas iguales con clases distintas.

> El proyecto calcula las entropías y ganancias en cada nodo, produce el árbol
> y acepta distintos archivos categóricos, incluidos los cuatro proporcionados.

Antes de entregar, comprueba que el video muestra el IDE y texto legible, que
incluye todas las ejecuciones indicadas y que abre correctamente. Consulta la
fecha y el medio de entrega en Brightspace; la guía no incluye una fecha concreta.
La demostración debe incluir **tu explicación oral del IDE, la estructura y el
código**, además de las ejecuciones: son los puntos señalados en la entrega pasada.
Si tienes dudas al preparar Linux o Windows, consulta
`docs/Guia_grabacion_Linux_Windows.md`.
