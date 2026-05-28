package com.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Provides hint suggestions for empty cells on a 6x6 Sudoku board.
 *
 * <p>A hint is a valid number that can be placed in an empty cell without
 * immediately violating any Sudoku rule, as determined by
 * {@link SudokuValidator#isValid(SudokuBoard, int, int, int)}.</p>
 *
 * <p>Hints are selected from a randomly shuffled list of empty cells,
 * so repeated requests will typically suggest different positions.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 */
public class SudokuHint {

    /** Random instance used to shuffle empty cells and candidate values. */
    private final Random random;

    /**
     * Constructs a {@code SudokuHint} provider with a default random seed.
     */
    public SudokuHint() {
        this.random = new Random();
    }

    // -------------------------------------------------------------------------
    // Hint inner class
    // -------------------------------------------------------------------------

    /**
     * Encapsulates a hint: the target cell coordinates and the suggested value.
     */
    public static class Hint {

        /** Row index of the suggested cell (0–5). */
        public final int row;

        /** Column index of the suggested cell (0–5). */
        public final int col;

        /** The suggested value to place (1–6). */
        public final int value;

        /**
         * Constructs a {@code Hint}.
         *
         * @param row   the row of the hinted cell
         * @param col   the column of the hinted cell
         * @param value the suggested value
         */
        public Hint(int row, int col, int value) {
            this.row = row;
            this.col = col;
            this.value = value;
        }

        /**
         * Returns a human-readable description of the hint.
         *
         * @return formatted string, e.g. {@code "Row 3, Column 4 → 5"}
         */
        @Override
        public String toString() {
            return "Row " + (row + 1) + ", Column " + (col + 1) + " \u2192 " + value;
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Finds a valid hint for a randomly selected empty cell on the board.
     *
     * <p>Collects all empty cells, shuffles the list, and returns the first
     * cell that has at least one valid candidate. The suggested value is also
     * chosen randomly from the available candidates.</p>
     *
     * @param board the current Sudoku board
     * @return a {@link Hint} with a valid suggestion, or {@code null} if no
     *         empty cell has any valid move (board is stuck or complete)
     */
    public Hint getHint(SudokuBoard board) {
        List<int[]> emptyCells = new ArrayList<>();

        for (int r = 0; r < SudokuBoard.SIZE; r++)
            for (int c = 0; c < SudokuBoard.SIZE; c++)
                if (board.isEmpty(r, c)) emptyCells.add(new int[]{r, c});

        Collections.shuffle(emptyCells, random);

        for (int[] cell : emptyCells) {
            List<Integer> candidates = getCandidates(board, cell[0], cell[1]);
            if (!candidates.isEmpty()) {
                Collections.shuffle(candidates, random);
                return new Hint(cell[0], cell[1], candidates.get(0));
            }
        }

        return null;
    }

    /**
     * Returns all valid values that can be placed at the given cell
     * without violating any Sudoku rule.
     *
     * <p>Uses {@link SudokuValidator#isValid(SudokuBoard, int, int, int)} which
     * internally runs the stack-based rule evaluation.</p>
     *
     * @param board the current Sudoku board
     * @param row   the row index of the cell (0–5)
     * @param col   the column index of the cell (0–5)
     * @return list of valid candidates (1–6); may be empty if no move is possible
     */
    public List<Integer> getCandidates(SudokuBoard board, int row, int col) {
        List<Integer> candidates = new ArrayList<>();
        for (int v = 1; v <= SudokuBoard.SIZE; v++)
            if (SudokuValidator.isValid(board, row, col, v))
                candidates.add(v);
        return candidates;
    }
}
