package br.com.dio.model;

import br.com.dio.util.BoardTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static br.com.dio.model.GameStatusEnum.*;

public class Board {

    private final List<List<Space>> spaces;

    public Board(List<List<Space>> spaces) {
        this.spaces = spaces;
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
        boolean noMovesMade = spaces.stream()
                .flatMap(Collection::stream)
                .noneMatch(s -> !s.isFixed() && Objects.nonNull(s.getActual()));
        if (noMovesMade) return NON_STARTED;

        boolean hasEmpty = spaces.stream()
                .flatMap(Collection::stream)
                .anyMatch(s -> Objects.isNull(s.getActual()));
        return hasEmpty ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors() {
        if (getStatus() == NON_STARTED) return false;

        return spaces.stream()
                .flatMap(Collection::stream)
                .anyMatch(s -> Objects.nonNull(s.getActual())
                        && (!isSpaceCorrect(s)));
    }

    // Usa Space.isCorrect() se existir; caso contrário, valida por expected
    private boolean isSpaceCorrect(Space s) {
        try {
            // Se a classe Space tiver o método isCorrect()
            return (boolean) Space.class.getMethod("isCorrect").invoke(s);
        } catch (ReflectiveOperationException e) {
            // Fallback compatível com a versão original de Space
            return Objects.equals(s.getActual(), s.getExpected());
        }
    }

    public boolean changeValue(int col, int row, int value) {
        return updateSpace(col, row, s -> s.setActual(value));
    }

    public boolean clearValue(int col, int row) {
        return updateSpace(col, row, Space::clearSpace);
    }

    private boolean updateSpace(int col, int row, Consumer<Space> action) {
        Space space = spaces.get(col).get(row);
        if (space.isFixed()) return false;

        action.accept(space);
        return true;
    }

    public void reset() {
        spaces.forEach(column -> column.forEach(Space::clearSpace));
    }

    public boolean gameIsFinished() {
        return getStatus() == COMPLETE && !hasErrors();
    }

    public void printBoard() {
        System.out.print(BoardTemplate.render(spaces));
    }



    @Override
    public String toString() {
        return BoardTemplate.render(spaces);
    }
}
