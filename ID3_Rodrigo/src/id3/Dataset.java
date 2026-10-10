package id3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Tabla categórica validada. La clase objetivo puede estar en cualquier columna. */
public final class Dataset {
    private final List<String> headers;
    private final List<List<String>> rows;
    private final int targetIndex;

    public Dataset(List<String> headers, List<List<String>> rows, String target) {
        if (headers == null || headers.isEmpty()) {
            throw new IllegalArgumentException("El CSV necesita encabezados.");
        }
        if (headers.stream().anyMatch(s -> s == null || s.isBlank())
                || new HashSet<>(headers).size() != headers.size()) {
            throw new IllegalArgumentException("Los encabezados deben ser únicos y no vacíos.");
        }
        this.headers = List.copyOf(headers);
        targetIndex = target == null ? headers.size() - 1 : headers.indexOf(target);
        if (targetIndex < 0) {
            throw new IllegalArgumentException("No existe la columna objetivo: " + target);
        }
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("El CSV necesita al menos una fila de datos.");
        }
        List<List<String>> copy = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row == null || row.size() != headers.size()) {
                throw new IllegalArgumentException("Registro " + (i + 2)
                        + ": número de columnas incorrecto.");
            }
            for (String value : row) {
                if (value == null || value.isBlank() || value.equals("?")) {
                    throw new IllegalArgumentException("Registro " + (i + 2)
                            + ": no se admiten valores vacíos ni '?'.");
                }
            }
            copy.add(List.copyOf(row));
        }
        this.rows = List.copyOf(copy);
    }

    public List<String> headers() { return headers; }
    public List<List<String>> rows() { return rows; }
    public int targetIndex() { return targetIndex; }
    public String targetName() { return headers.get(targetIndex); }

    public List<Integer> attributes() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < headers.size(); i++) {
            if (i != targetIndex) result.add(i);
        }
        return result;
    }

    public Map<String, String> features(List<String> row) {
        Map<String, String> result = new LinkedHashMap<>();
        for (int i : attributes()) result.put(headers.get(i), row.get(i));
        return result;
    }
}
