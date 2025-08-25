package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static Board board;

    private static final int BOARD_SIZE = 9;
    private static final int MIN_INDEX = 0;
    private static final int MAX_INDEX = 8;
    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 9;

    public static void main(String[] args) {
        final Map<String, String> positions = parsePositions(args);

        while (true) {
            printMenu();
            int option = readIntInRange(1, 8, "Opção inválida. Digite um número entre 1 e 8: ");

            switch (option) {
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> {
                    System.out.println("Encerrando. Até a próxima!");
                    return;
                }
                default -> System.out.println("Opção inválida!");
            }
        }
    }

    private static Map<String, String> parsePositions(String[] args) {
        if (args == null || args.length == 0) return Collections.emptyMap();

        return Stream.of(args)
                .filter(s -> s.contains(";"))
                .collect(toMap(
                        k -> k.split(";", 2)[0].trim(),
                        v -> v.split(";", 2)[1].trim()
                ));
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("Selecione uma das opções:");
        System.out.println("1 - Iniciar novo jogo");
        System.out.println("2 - Colocar um número");
        System.out.println("3 - Remover um número");
        System.out.println("4 - Visualizar jogo atual");
        System.out.println("5 - Verificar status do jogo");
        System.out.println("6 - Limpar jogo");
        System.out.println("7 - Finalizar jogo");
        System.out.println("8 - Sair");
        System.out.print("Opção: ");
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)) {
            System.out.println("O jogo já foi iniciado.");
            return;
        }

        Map<String, String> pos = (positions == null || positions.size() < 81)
                ? buildDefaultPositions()
                : positions;

        List<List<Space>> spaces = new ArrayList<>(BOARD_SIZE);
        for (int col = 0; col < BOARD_SIZE; col++) {
            List<Space> column = new ArrayList<>(BOARD_SIZE);
            for (int row = 0; row < BOARD_SIZE; row++) {
                String key = "%s,%s".formatted(col, row);
                String positionConfig = pos.get(key);
                String[] parts = positionConfig.split(",", 2);
                int expected = Integer.parseInt(parts[0].trim());
                boolean fixed = Boolean.parseBoolean(parts[1].trim());
                column.add(new Space(expected, fixed));
            }
            spaces.add(column);
        }

        board = new Board(spaces);
        System.out.println("O jogo está pronto para começar!");
    }

    private static void inputNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        System.out.print("Coluna (0-8): ");
        int col = readIntInRange(MIN_INDEX, MAX_INDEX, "Coluna inválida: ");
        System.out.print("Linha (0-8): ");
        int row = readIntInRange(MIN_INDEX, MAX_INDEX, "Linha inválida: ");
        System.out.printf("Valor para [%s,%s] (1-9): ", col, row);
        int value = readIntInRange(MIN_VALUE, MAX_VALUE, "Valor inválido: ");
        if (!board.changeValue(col, row, value)) {
            System.out.printf("A posição [%s,%s] é fixa.%n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        System.out.print("Coluna (0-8): ");
        int col = readIntInRange(MIN_INDEX, MAX_INDEX, "Coluna inválida: ");
        System.out.print("Linha (0-8): ");
        int row = readIntInRange(MIN_INDEX, MAX_INDEX, "Linha inválida: ");
        if (!board.clearValue(col, row)) {
            System.out.printf("A posição [%s,%s] é fixa.%n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        board.printBoard();
    }

    private static void showGameStatus() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        System.out.printf("Status: %s%n", board.getStatus().getLabel());
        System.out.println(board.hasErrors() ? "Contém erros." : "Sem erros.");
    }

    private static void clearGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        System.out.print("Limpar jogo? (sim/não): ");
        if (readYesNo()) {
            board.reset();
            System.out.println("Jogo limpo.");
        }
    }

    private static void finishGame() {
        if (isNull(board)) {
            System.out.println("O jogo ainda não foi iniciado.");
            return;
        }
        if (board.gameIsFinished()) {
            System.out.println("Parabéns, você concluiu!");
            board.printBoard();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("O jogo contém erros.");
            board.printBoard();
        } else {
            System.out.println("Ainda há espaços vazios.");
            board.printBoard();
        }
    }

    private static int readIntInRange(int min, int max, String retryMessage) {
        while (true) {
            Integer value = readIntOrNull();
            if (value != null && value >= min && value <= max) {
                return value;
            }
            System.out.print(retryMessage);
        }
    }

    private static Integer readIntOrNull() {
        String token = scanner.nextLine().trim();
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static boolean readYesNo() {
        while (true) {
            String answer = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if (answer.equals("sim") || answer.equals("s")) return true;
            if (answer.equals("não") || answer.equals("nao") || answer.equals("n")) return false;
            System.out.print("Digite 'sim' ou 'não': ");
        }
    }

    private static Map<String, String> buildDefaultPositions() {
        int[][] solution = {
                {5,3,4,6,7,8,9,1,2},
                {6,7,2,1,9,5,3,4,8},
                {1,9,8,3,4,2,5,6,7},
                {8,5,9,7,6,1,4,2,3},
                {4,2,6,8,5,3,7,9,1},
                {7,1,3,9,2,4,8,5,6},
                {9,6,1,5,3,7,2,8,4},
                {2,8,7,4,1,9,6,3,5},
                {3,4,5,2,8,6,1,7,9}
        };

        // Máscara de “dicas” (true = fixo, igual ao puzzle clássico)
        boolean[][] fixed = new boolean[9][9];
        // linha 0
        fixed[0][0] = true; fixed[0][1] = true; fixed[0][4] = true;
        // linha 1
        fixed[1][0] = true; fixed[1][3] = true; fixed[1][4] = true; fixed[1][5] = true;
        // linha 2
        fixed[2][1] = true; fixed[2][2] = true; fixed[2][7] = true;
        // linha 3
        fixed[3][0] = true; fixed[3][4] = true; fixed[3][8] = true;
        // linha 4
        fixed[4][0] = true; fixed[4][3] = true; fixed[4][5] = true; fixed[4][8] = true;
        // linha 5
        fixed[5][0] = true; fixed[5][4] = true; fixed[5][8] = true;
        // linha 6
        fixed[6][1] = true; fixed[6][6] = true; fixed[6][7] = true;
        // linha 7
        fixed[7][3] = true; fixed[7][4] = true; fixed[7][5] = true; fixed[7][8] = true;
        // linha 8
        fixed[8][4] = true; fixed[8][7] = true; fixed[8][8] = true;

        Map<String, String> pos = new LinkedHashMap<>(81);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                pos.put(col + "," + row, solution[row][col] + "," + fixed[row][col]);
            }
        }
        return pos;
        }
    }