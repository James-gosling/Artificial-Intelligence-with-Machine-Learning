package id3;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Nodo inmutable: hoja con clase o decisión con ramas por valor categórico. */
public final class DecisionNode {
    private final String attribute;
    private final String prediction;
    private final Map<String, DecisionNode> children;

    private DecisionNode(String attribute, String prediction, Map<String, DecisionNode> children) {
        this.attribute = attribute;
        this.prediction = prediction;
        this.children = Collections.unmodifiableMap(new LinkedHashMap<>(children));
    }

    public static DecisionNode leaf(String prediction) {
        return new DecisionNode(null, prediction, Map.of());
    }

    public static DecisionNode branch(String attribute, String majority,
            Map<String, DecisionNode> children) {
        return new DecisionNode(attribute, majority, children);
    }

    public boolean isLeaf() { return attribute == null; }
    public String attribute() { return attribute; }
    public String prediction() { return prediction; }
    public Map<String, DecisionNode> children() { return children; }

    /** Un valor desconocido o ausente usa la clase mayoritaria del nodo actual. */
    public String predict(Map<String, String> example) {
        if (isLeaf()) return prediction;
        DecisionNode child = children.get(example.get(attribute));
        return child == null ? prediction : child.predict(example);
    }

    public String render() {
        StringBuilder out = new StringBuilder();
        render(out, "");
        return out.toString();
    }

    private void render(StringBuilder out, String indent) {
        if (isLeaf()) {
            out.append("Clase = ").append(prediction).append('\n');
            return;
        }
        out.append('[').append(attribute).append("]\n");
        for (Map.Entry<String, DecisionNode> entry : children.entrySet()) {
            out.append(indent).append("  ").append(entry.getKey()).append(" -> ");
            entry.getValue().render(out, indent + "  ");
        }
    }
}
