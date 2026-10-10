package id3;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/** Entrada de consola; en Eclipse ejecutar Run As > Java Application. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        int status = run(args, System.out, System.err);
        if (status != 0) System.exit(status);
    }

    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            usage(out);
            return 0;
        }
        if (args.length > 2) {
            usage(err);
            return 2;
        }
        Path file = null;
        try {
            file = Path.of(args.length == 0 ? "data/proporcionados/data.csv" : args[0]);
            String target = args.length == 2 ? args[1] : null;
            Dataset data = CsvReader.read(file, target);
            out.println("PROYECTO ID3 — Actividad 2");
            out.println("Rodrigo Valdespino Vertiz | ID: 00520621");
            out.println("Carrera: Ingeniería en Tecnologías de la Información y Negocios Digitales");
            out.println("Archivo: " + file + " | Registros: " + data.rows().size()
                    + " | Objetivo: " + data.targetName());
            out.println("H(S)=-sum(p*log2(p)); H(Y|A)=sum(|Sv|/|S|*H(Sv)); IG=H(S)-H(Y|A)");
            DecisionNode tree = new Id3Trainer(out).train(data);
            out.println("\nÁRBOL DE DECISIÓN");
            out.print(tree.render());
            out.println("\nCOMPROBACIÓN SOBRE DATOS DE ENTRENAMIENTO");
            int correct = 0;
            for (int i = 0; i < data.rows().size(); i++) {
                List<String> row = data.rows().get(i);
                String expected = row.get(data.targetIndex());
                String predicted = tree.predict(data.features(row));
                boolean match = expected.equals(predicted);
                if (match) correct++;
                out.printf("  Registro %d: real=%s, predicción=%s, %s%n",
                        i + 1, expected, predicted, match ? "OK" : "DIFERENTE");
            }
            out.printf(Locale.ROOT, "Aciertos de entrenamiento: %d/%d (%.2f%%)%n", correct,
                    data.rows().size(), 100.0 * correct / data.rows().size());
            out.println("Esta comprobación no mide la capacidad de generalizar a datos nuevos.");
            return 0;
        } catch (IOException | IllegalArgumentException ex) {
            err.println("Error al procesar " + (file == null ? "el archivo" : file) + ": " + ex.getMessage());
            return 1;
        }
    }

    private static void usage(PrintStream out) {
        out.println("Uso: java -cp bin id3.Main [archivo.csv] [columnaObjetivo]");
        out.println("Sin argumentos: data/proporcionados/data.csv. Objetivo por defecto: última columna.");
        out.println("CSV UTF-8 con encabezado, separado por coma o punto y coma; valores categóricos.");
    }
}
