package id3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** H(S) = -sum(p * log2(p)); los conteos preservan el orden del archivo. */
public final class Entropy {
    private Entropy() { }

    public static Map<String, Integer> counts(List<List<String>> rows, int column) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (List<String> row : rows) counts.merge(row.get(column), 1, Integer::sum);
        return counts;
    }

    public static double of(List<List<String>> rows, int column) {
        if (rows.isEmpty()) return 0.0;
        double entropy = 0.0;
        for (int count : counts(rows, column).values()) {
            double probability = (double) count / rows.size();
            entropy -= probability * (Math.log(probability) / Math.log(2.0));
        }
        return entropy;
    }

    public static String majority(List<List<String>> rows, int target) {
        if (rows.isEmpty()) throw new IllegalArgumentException("No hay filas para elegir mayoría.");
        String winner = null;
        int maximum = -1;
        for (Map.Entry<String, Integer> entry : counts(rows, target).entrySet()) {
            if (entry.getValue() > maximum) {
                maximum = entry.getValue();
                winner = entry.getKey();
            }
        }
        return winner;
    }
}
