package id3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Lector UTF-8 sin bibliotecas externas: comas/punto y coma, BOM y comillas CSV. */
public final class CsvReader {
    private CsvReader() { }

    public static Dataset read(Path path, String target) throws IOException {
        return parse(Files.readString(path, StandardCharsets.UTF_8), target);
    }

    public static Dataset parse(String text, String target) {
        if (text.startsWith("\uFEFF")) text = text.substring(1);
        char delimiter = detectDelimiter(text);
        List<List<String>> records = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        boolean closedQuote = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (quoted) {
                if (c == '"') {
                    if (i + 1 < text.length() && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                        closedQuote = true;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == delimiter || c == '\n' || c == '\r') {
                boolean blankLine = row.isEmpty() && field.toString().isBlank() && !closedQuote;
                row.add(field.toString().trim());
                field.setLength(0);
                closedQuote = false;
                if (c != delimiter) {
                    if (!blankLine) records.add(List.copyOf(row));
                    row.clear();
                    if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') i++;
                }
            } else if (c == '"' && field.length() == 0 && !closedQuote) {
                quoted = true;
            } else if (c == '"' || (closedQuote && !Character.isWhitespace(c))) {
                throw new IllegalArgumentException("CSV inválido: comillas mal ubicadas.");
            } else {
                field.append(c);
            }
        }
        if (quoted) throw new IllegalArgumentException("CSV inválido: comillas sin cerrar.");
        if (!row.isEmpty() || field.length() > 0 || closedQuote) {
            row.add(field.toString().trim());
            records.add(List.copyOf(row));
        }
        if (records.isEmpty()) throw new IllegalArgumentException("El archivo CSV está vacío.");
        return new Dataset(records.get(0), records.subList(1, records.size()), target);
    }

    private static char detectDelimiter(String text) {
        boolean quoted = false;
        int commas = 0;
        int semicolons = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') quoted = !quoted;
            if (!quoted) {
                if (c == ',') commas++;
                if (c == ';') semicolons++;
                if ((c == '\n' || c == '\r') && (commas + semicolons > 0)) break;
            }
        }
        return semicolons > commas ? ';' : ',';
    }
}
