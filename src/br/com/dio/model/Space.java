package br.com.dio.model;

import java.util.Objects;

public class Space {

    private Integer actual;
    private final int expected;
    private final boolean fixed;

    public Space(int expected, boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        if (fixed) {
            this.actual = expected;
        }
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(Integer actual) {
        if (!fixed) {
            this.actual = actual;
        }
    }

    public void clearSpace() {
        setActual(null);
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }

    public boolean isCorrect() {
        return Objects.equals(actual, expected);
    }

    @Override
    public String toString() {
        return fixed ? String.format("[%d]", expected)
                : actual == null ? "[ ]" : String.format("[%d]", actual);
    }
}
