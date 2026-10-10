package id3;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** ID3 multiclase: maximiza IG, elimina el atributo elegido y aprende recursivamente. */
public final class Id3Trainer {
    private final PrintStream trace;

    public Id3Trainer(PrintStream trace) { this.trace = trace; }

    public DecisionNode train(Dataset data) {
        return build(data, data.rows(), data.attributes(), "RAÍZ");
    }

    public static Map<String, List<List<String>>> partition(List<List<String>> rows, int attribute) {
        Map<String, List<List<String>>> groups = new LinkedHashMap<>();
        for (List<String> row : rows) {
            groups.computeIfAbsent(row.get(attribute), key -> new ArrayList<>()).add(row);
        }
        return groups;
    }

    public static double conditionalEntropy(List<List<String>> rows, int attribute, int target) {
        double result = 0;
        for (List<List<String>> group : partition(rows, attribute).values()) {
            result += (double) group.size() / rows.size() * Entropy.of(group, target);
        }
        return result;
    }

    public static double informationGain(List<List<String>> rows, int attribute, int target) {
        return Entropy.of(rows, target) - conditionalEntropy(rows, attribute, target);
    }

    private DecisionNode build(Dataset data, List<List<String>> rows,
            List<Integer> attributes, String path) {
        int target = data.targetIndex();
        double parentEntropy = Entropy.of(rows, target);
        String majority = Entropy.majority(rows, target);
        log("%nNODO %s | n=%d | clases=%s | H(S)=%.6f bits%n",
                path, rows.size(), Entropy.counts(rows, target), parentEntropy);
        if (Entropy.counts(rows, target).size() == 1) {
            log("Hoja pura: %s%n", majority);
            return DecisionNode.leaf(majority);
        }
        if (attributes.isEmpty()) {
            log("Sin atributos: clase mayoritaria = %s%n", majority);
            return DecisionNode.leaf(majority);
        }

        int bestAttribute = attributes.get(0);
        double bestGain = Double.NEGATIVE_INFINITY;
        for (int attribute : attributes) {
            String name = data.headers().get(attribute);
            log("  Variable %s | H(%s)=%.6f bits%n", name, name, Entropy.of(rows, attribute));
            for (Map.Entry<String, List<List<String>>> group : partition(rows, attribute).entrySet()) {
                double weight = (double) group.getValue().size() / rows.size();
                double subsetEntropy = Entropy.of(group.getValue(), target);
                log("    %s=%s: n=%d, clases=%s, H(Sv)=%.6f, peso=%d/%d, aporte=%.6f%n",
                        name, group.getKey(), group.getValue().size(),
                        Entropy.counts(group.getValue(), target), subsetEntropy,
                        group.getValue().size(), rows.size(), weight * subsetEntropy);
            }
            double conditional = conditionalEntropy(rows, attribute, target);
            double gain = parentEntropy - conditional;
            log("    H(%s | %s)=%.6f | IG=%.6f - %.6f = %.6f bits%n",
                    data.targetName(), name, conditional, parentEntropy, conditional, gain);
            // Empates numéricos: conservar el primer atributo del encabezado.
            if (gain > bestGain + 1e-12) {
                bestGain = gain;
                bestAttribute = attribute;
            }
        }
        String selected = data.headers().get(bestAttribute);
        log("  ELEGIDA: %s (IG=%.6f)%n", selected, bestGain);
        List<Integer> remaining = new ArrayList<>(attributes);
        remaining.remove(Integer.valueOf(bestAttribute));
        Map<String, DecisionNode> children = new LinkedHashMap<>();
        // No detener por IG=0: una combinación posterior puede separar los datos (XOR).
        for (Map.Entry<String, List<List<String>>> group : partition(rows, bestAttribute).entrySet()) {
            children.put(group.getKey(), build(data, group.getValue(), remaining,
                    path + " / " + selected + "=" + group.getKey()));
        }
        return DecisionNode.branch(selected, majority, children);
    }

    private void log(String format, Object... args) {
        if (trace != null) trace.printf(Locale.ROOT, format, args);
    }
}
