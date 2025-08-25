package br.com.dio.util;

import br.com.dio.model.Space;
import java.util.List;
import java.util.Objects;

public final class BoardTemplate {

    // Códigos ANSI
    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String CYAN   = "\u001B[36m";
    private static final String WHITE  = "\u001B[37m";
    private static final String YELLOW = "\u001B[33m"; // Para cabeçalho

    private static final boolean SUPPORTS_ANSI = detectAnsiSupport();

    private BoardTemplate() {}

    public static String render(List<List<Space>> spaces) {
        StringBuilder sb = new StringBuilder();

        // Cabeçalho colorido das colunas
        sb.append("    ");
        for (int col = 0; col < 9; col++) {
            String val = String.valueOf(col);
            sb.append(SUPPORTS_ANSI ? YELLOW + " " + val + " " + RESET : " " + val + " ");
            if (col == 2 || col == 5) sb.append("|");
        }
        sb.append("\n");
        sb.append("  +---+---+---+---+---+---+---+---+---+\n");

        for (int row = 0; row < 9; row++) {
            // Índice da linha com cor
            sb.append(SUPPORTS_ANSI ? YELLOW + row + RESET : row).append(" |");

            for (int col = 0; col < 9; col++) {
                Space s = spaces.get(col).get(row);
                String val = SUPPORTS_ANSI ? colorize(s) : plainValue(s);
                sb.append(" ").append(val).append(" |");

                if (col == 2 || col == 5) sb.append("|");
            }
            sb.append("\n");

            if (row == 2 || row == 5) {
                sb.append("  +---+---+---+---+---+---+---+---+---+\n");
            }
        }
        sb.append("  +---+---+---+---+---+---+---+---+---+\n");

        return sb.toString();
    }

    private static String plainValue(Space s) {
        return s.getActual() == null ? " " : String.valueOf(s.getActual());
    }

    private static String colorize(Space s) {
        Integer actual = s.getActual();
        if (actual == null) return " ";
        if (s.isFixed()) return CYAN + actual + RESET;
        boolean correct = Objects.equals(actual, s.getExpected());
        return correct ? WHITE + actual + RESET : RED + actual + RESET;
    }

    private static boolean detectAnsiSupport() {
        String os = System.getProperty("os.name", "").toLowerCase();
        boolean isWindows = os.contains("win");
        boolean noColor = System.getenv("NO_COLOR") != null;
        return !noColor && (!isWindows || System.console() != null);
    }
}

