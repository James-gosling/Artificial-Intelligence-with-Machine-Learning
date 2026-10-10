package id3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/** Pruebas reproducibles sin dependencias: fallar una comprobación devuelve código 1. */
public final class Id3Test {
    private static int passed;
    private static int failed;

    private Id3Test() { }

    public static void main(String[] args) throws IOException {
        Dataset tennis = CsvReader.read(Path.of("data/jugar_tenis.csv"), null);
        Dataset xor = CsvReader.read(Path.of("data/xor.csv"), null);
        Dataset fruits = CsvReader.read(Path.of("data/frutas.csv"), null);
        Dataset conflicts = CsvReader.read(Path.of("data/contradictorios.csv"), null);
        Dataset provided = CsvReader.read(Path.of("data/proporcionados/data.csv"), null);
        Dataset play = CsvReader.read(Path.of("data/proporcionados/Play.csv"), null);
        Dataset restaurant = CsvReader.read(Path.of("data/proporcionados/play7.csv"), null);
        Dataset credit = CsvReader.read(Path.of("data/proporcionados/play8.csv"), null);

        check("Archivo proporcionado data.csv: raíz Outlook y 14/14", () -> {
            equal("Outlook", train(provided).attribute());
            equal(14, correct(provided));
        });
        check("Archivo proporcionado Play.csv: mismos datos, 14/14", () -> {
            equal(provided.headers(), play.headers());
            equal(provided.rows(), play.rows());
            equal(14, correct(play));
        });
        check("Archivo proporcionado play7.csv: Patrons, IG y 12/12", () -> {
            equal("Patrons", train(restaurant).attribute());
            near(1.0, Entropy.of(restaurant.rows(), restaurant.targetIndex()));
            near(0.5408520829727552, Id3Trainer.informationGain(restaurant.rows(), 4, 10));
            equal(12, correct(restaurant));
            equal("£££", restaurant.rows().get(0).get(5));
        });
        check("Archivo proporcionado play8.csv: Income, tres clases y 14/14", () -> {
            equal("Income", train(credit).attribute());
            equal(3, Entropy.counts(credit.rows(), credit.targetIndex()).size());
            near(1.5306189948485174, Entropy.of(credit.rows(), credit.targetIndex()));
            near(0.966323671284937, Id3Trainer.informationGain(credit.rows(), 3, 4));
            equal(14, correct(credit));
        });

        check("Entropía inicial de tenis: 0.940286", () ->
                near(0.9402859586706311, Entropy.of(tennis.rows(), tennis.targetIndex())));
        check("Ganancia de Clima: 0.246750", () ->
                near(0.24674981977443933, Id3Trainer.informationGain(tennis.rows(), 0, 4)));
        check("Ganancia de Temperatura: 0.029223", () ->
                near(0.02922256565895487, Id3Trainer.informationGain(tennis.rows(), 1, 4)));
        check("Ganancia de Humedad: 0.151836", () ->
                near(0.15183550136234159, Id3Trainer.informationGain(tennis.rows(), 2, 4)));
        check("Ganancia de Viento: 0.048127", () ->
                near(0.04812703040826949, Id3Trainer.informationGain(tennis.rows(), 3, 4)));
        check("Árbol de tenis completo coincide con solución conocida", () -> {
            String expected = "[Clima]\n"
                    + "  Soleado -> [Humedad]\n"
                    + "    Alta -> Clase = No\n"
                    + "    Normal -> Clase = Sí\n"
                    + "  Nublado -> Clase = Sí\n"
                    + "  Lluvia -> [Viento]\n"
                    + "    Débil -> Clase = Sí\n"
                    + "    Fuerte -> Clase = No\n";
            equal(expected, train(tennis).render());
        });
        check("Tenis: 14/14 predicciones de entrenamiento", () -> equal(14, correct(tennis)));
        check("Combinación no incluida en tenis sigue regla conocida", () ->
                equal("No", train(tennis).predict(Map.of("Clima", "Soleado", "Temperatura", "Baja",
                        "Humedad", "Alta", "Viento", "Fuerte"))));
        check("XOR: ganancias iniciales cero", () -> {
            near(0, Id3Trainer.informationGain(xor.rows(), 0, 2));
            near(0, Id3Trainer.informationGain(xor.rows(), 1, 2));
        });
        check("XOR: 4/4 pese a ganancia inicial cero", () -> equal(4, correct(xor)));
        check("Empate de atributos respeta encabezado", () -> equal("A", train(xor).attribute()));
        check("CSV con punto y coma, multiclase: 6/6", () -> {
            near(Math.log(3) / Math.log(2), Entropy.of(fruits.rows(), 2));
            equal(6, correct(fruits));
        });
        check("Datos contradictorios terminan con mayoría: 2/3", () -> equal(2, correct(conflicts)));
        check("Clase pura produce una hoja", () -> {
            DecisionNode tree = train(CsvReader.parse("A,Y\nx,Sí\ny,Sí\n", null));
            equal(true, tree.isLeaf());
            equal("Sí", tree.prediction());
        });
        check("Sin atributos y empate: primera clase encontrada", () -> {
            DecisionNode tree = train(CsvReader.parse("Clase\nNo\nSí\n", null));
            equal(true, tree.isLeaf());
            equal("No", tree.prediction());
        });
        check("Valor desconocido usa mayoría del nodo", () ->
                equal("Sí", train(tennis).predict(Map.of("Clima", "Nevado"))));
        check("Atributo ausente usa mayoría del nodo", () ->
                equal("Sí", train(tennis).predict(Map.of())));
        check("Objetivo en primera columna por nombre", () -> {
            Dataset data = CsvReader.parse("Clase,A\nNo,x\nSí,y\n", "Clase");
            equal(0, data.targetIndex());
            equal(2, correct(data));
        });
        check("CSV BOM, CRLF, comillas escapadas y coma interna", () -> {
            Dataset data = CsvReader.parse("\uFEFFA,B,Clase\r\n\"rojo, azul\",\"dijo \"\"sí\"\"\",Sí\r\n", null);
            equal("rojo, azul", data.rows().get(0).get(0));
            equal("dijo \"sí\"", data.rows().get(0).get(1));
        });
        check("CSV admite salto de línea entre comillas", () ->
                equal("rojo\nazul", CsvReader.parse("A,Y\n\"rojo\nazul\",Sí\n", null).rows().get(0).get(0)));
        check("CSV ignora líneas vacías y admite final sin salto", () ->
                equal(2, CsvReader.parse("\nA;Y\n\nx;Sí\n\ny;No", null).rows().size()));
        check("Rechaza CSV vacío", () -> invalid("", null));
        check("Rechaza archivo sin registros", () -> invalid("A,Y\n", null));
        check("Rechaza encabezados duplicados", () -> invalid("A,A\nx,y\n", null));
        check("Rechaza encabezado vacío", () -> invalid("A,\nx,y\n", null));
        check("Rechaza anchura de fila incorrecta", () -> invalid("A,Y\nx,y,z\n", null));
        check("Rechaza valores faltantes", () -> invalid("A,Y\n,Sí\n", null));
        check("Rechaza interrogación como dato faltante", () -> invalid("A,Y\n?,Sí\n", null));
        check("Rechaza comillas abiertas", () -> invalid("A,Y\n\"x,Sí", null));
        check("Rechaza texto tras cierre de comillas", () -> invalid("A,Y\n\"x\"z,Sí", null));
        check("Rechaza objetivo inexistente", () -> invalid("A,Y\nx,Sí\n", "NoExiste"));
        check("Traza incluye entropías, pesos y decisión", () -> {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            new Id3Trainer(new PrintStream(bytes, true, StandardCharsets.UTF_8)).train(tennis);
            String trace = bytes.toString(StandardCharsets.UTF_8);
            for (String text : List.of("H(S)=0.940286", "H(Clima)=", "H(Temperatura)=", "H(Humedad)=",
                    "H(Viento)=", "peso=5/14", "H(Jugar | Clima)=0.693536", "ELEGIDA: Clima")) {
                if (!trace.contains(text)) throw new AssertionError("Falta en traza: " + text);
            }
        });
        check("CLI ejecuta el archivo por defecto", () -> {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream out = new PrintStream(bytes, true, StandardCharsets.UTF_8);
            equal(0, Main.run(new String[0], out, out));
            if (!bytes.toString(StandardCharsets.UTF_8).contains("14/14 (100.00%)")) {
                throw new AssertionError("No se ejecutaron los 14 registros.");
            }
        });
        check("CLI devuelve error por archivo inexistente", () -> {
            PrintStream quiet = new PrintStream(new ByteArrayOutputStream());
            equal(1, Main.run(new String[] {"data/archivo_que_no_existe.csv"}, quiet, quiet));
        });
        check("CLI informa uso para argumentos inválidos", () -> {
            PrintStream quiet = new PrintStream(new ByteArrayOutputStream());
            equal(2, Main.run(new String[] {"a", "b", "c"}, quiet, quiet));
            equal(0, Main.run(new String[] {"--help"}, quiet, quiet));
        });
        System.out.printf("%nResultado: %d aprobadas, %d fallidas.%n", passed, failed);
        if (failed != 0) System.exit(1);
    }

    private static DecisionNode train(Dataset data) { return new Id3Trainer(null).train(data); }

    private static int correct(Dataset data) {
        DecisionNode tree = train(data);
        int count = 0;
        for (List<String> row : data.rows()) {
            if (row.get(data.targetIndex()).equals(tree.predict(data.features(row)))) count++;
        }
        return count;
    }

    private static void invalid(String csv, String target) {
        try {
            CsvReader.parse(csv, target);
        } catch (IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Se esperaba un rechazo del CSV.");
    }

    private static void equal(Object expected, Object actual) {
        if (!expected.equals(actual)) throw new AssertionError("Esperado: " + expected + "; obtenido: " + actual);
    }

    private static void near(double expected, double actual) {
        if (!Double.isFinite(actual) || Math.abs(expected - actual) > 1e-9) {
            throw new AssertionError("Esperado: " + expected + "; obtenido: " + actual);
        }
    }

    private static void check(String name, Runnable test) {
        try {
            test.run();
            passed++;
            System.out.println("OK: " + name);
        } catch (AssertionError | RuntimeException ex) {
            failed++;
            System.err.println("FALLÓ: " + name + " | " + ex.getMessage());
        }
    }
}
