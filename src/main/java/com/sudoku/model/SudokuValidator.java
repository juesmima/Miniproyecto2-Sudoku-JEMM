package com.sudoku.model;

import java.util.Stack;

/**
 * Validates moves on a 6x6 Sudoku board using a {@link Stack} of {@link ValidationRule} objects.
 *
 * <h2>How the Stack-based validation works</h2>
 * <p>Each time {@link #validate(SudokuBoard, int, int, int)} is called, three rules
 * are pushed onto a fresh stack in this order:</p>
 * <ol>
 *   <li>{@code BlockRule} (pushed first → evaluated last)</li>
 *   <li>{@code ColumnRule}</li>
 *   <li>{@code RowRule} (pushed last → evaluated first, LIFO)</li>
 * </ol>
 * <p>The stack is then popped one rule at a time. If any rule fails, a
 * {@link ValidationResult#fail(String)} is returned immediately with the name
 * of the violated rule. If all rules pass, {@link ValidationResult#ok()} is returned.</p>
 *
 * <h2>Internal rule classes</h2>
 * <p>The three rules are implemented as private static inner classes that implement
 * {@link ValidationRule}, keeping them encapsulated inside the validator.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 */
public class SudokuValidator {

    // -------------------------------------------------------------------------
    // Inner rule classes (implement ValidationRule)
    // -------------------------------------------------------------------------

    /**
     * Validates that the given value does not already appear in the same row,
     * excluding the cell at (row, col) itself.
     */
    private static class RowRule implements ValidationRule {

        /**
         * Checks the entire row for a duplicate of {@code value}.
         *
         * @param board the Sudoku board
         * @param row   the row to scan
         * @param col   the column of the cell being placed (excluded from check)
         * @param value the value to look for
         * @return {@code true} if no duplicate exists in the row
         */
        @Override
        public boolean validate(SudokuBoard board, int row, int col, int value) {
            for (int c = 0; c < SudokuBoard.SIZE; c++)
                if (c != col && board.getValue(row, c) == value) return false;
            return true;
        }

        /**
         * Returns the name of this rule.
         *
         * @return {@code "Row"}
         */
        @Override
        public String getRuleName() { return "Row"; }
    }

    /**
     * Validates that the given value does not already appear in the same column,
     * excluding the cell at (row, col) itself.
     */
    private static class ColumnRule implements ValidationRule {

        /**
         * Checks the entire column for a duplicate of {@code value}.
         *
         * @param board the Sudoku board
         * @param row   the row of the cell being placed (excluded from check)
         * @param col   the column to scan
         * @param value the value to look for
         * @return {@code true} if no duplicate exists in the column
         */
        @Override
        public boolean validate(SudokuBoard board, int row, int col, int value) {
            for (int r = 0; r < SudokuBoard.SIZE; r++)
                if (r != row && board.getValue(r, col) == value) return false;
            return true;
        }

        /**
         * Returns the name of this rule.
         *
         * @return {@code "Column"}
         */
        @Override
        public String getRuleName() { return "Column"; }
    }

    /**
     * Validates that the given value does not already appear in the 2x3 block
     * that contains the cell at (row, col), excluding that cell itself.
     */
    private static class BlockRule implements ValidationRule {

        /**
         * Checks the 2x3 block for a duplicate of {@code value}.
         *
         * @param board the Sudoku board
         * @param row   the row of the target cell
         * @param col   the column of the target cell
         * @param value the value to look for
         * @return {@code true} if no duplicate exists in the block
         */
        @Override
        public boolean validate(SudokuBoard board, int row, int col, int value) {
            int rowStart = (row / SudokuBoard.BLOCK_ROWS) * SudokuBoard.BLOCK_ROWS;
            int colStart = (col / SudokuBoard.BLOCK_COLS) * SudokuBoard.BLOCK_COLS;

            for (int r = rowStart; r < rowStart + SudokuBoard.BLOCK_ROWS; r++)
                for (int c = colStart; c < colStart + SudokuBoard.BLOCK_COLS; c++)
                    if ((r != row || c != col) && board.getValue(r, c) == value)
                        return false;
            return true;
        }

        /**
         * Returns the name of this rule.
         *
         * @return {@code "Block"}
         */
        @Override
        public String getRuleName() { return "Block"; }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Validates a move by pushing three {@link ValidationRule} objects onto a
     * {@link Stack} and popping them one by one.
     *
     * <p>Push order (bottom → top): {@code BlockRule}, {@code ColumnRule}, {@code RowRule}.<br>
     * Pop order (LIFO): {@code RowRule} first, then {@code ColumnRule}, then {@code BlockRule}.</p>
     *
     * <p>Validation stops as soon as one rule fails, returning a
     * {@link ValidationResult} that names the violated rule.</p>
     *
     * @param board the current Sudoku board
     * @param row   the row index of the cell to validate (0–5)
     * @param col   the column index of the cell to validate (0–5)
     * @param value the value being placed (1–6)
     * @return {@link ValidationResult#ok()} if all rules pass;
     *         {@link ValidationResult#fail(String)} with the violated rule name otherwise
     */
    public static ValidationResult validate(SudokuBoard board, int row, int col, int value) {
        Stack<ValidationRule> ruleStack = new Stack<>();

        // Push order: block first (bottom), row last (top)
        ruleStack.push(new BlockRule());
        ruleStack.push(new ColumnRule());
        ruleStack.push(new RowRule());

        // Pop and evaluate: RowRule → ColumnRule → BlockRule
        while (!ruleStack.isEmpty()) {
            ValidationRule rule = ruleStack.pop();
            if (!rule.validate(board, row, col, value))
                return ValidationResult.fail(rule.getRuleName());
        }

        return ValidationResult.ok();
    }

    /**
     * Convenience method that returns {@code true} if a move passes all rules.
     *
     * <p>Internally calls {@link #validate(SudokuBoard, int, int, int)} and
     * checks {@link ValidationResult#isValid()}.</p>
     *
     * @param board the current Sudoku board
     * @param row   the row index (0–5)
     * @param col   the column index (0–5)
     * @param value the value to check (1–6)
     * @return {@code true} if the placement is valid
     */
    public static boolean isValid(SudokuBoard board, int row, int col, int value) {
        return validate(board, row, col, value).isValid();
    }

    /**
     * Validates the entire board and returns {@code true} only if every cell
     * is filled and no rules are violated.
     *
     * @param board the Sudoku board to validate
     * @return {@code true} if the board is complete and fully valid
     */
    public static boolean isBoardComplete(SudokuBoard board) {
        if (!board.isFull()) return false;
        for (int r = 0; r < SudokuBoard.SIZE; r++)
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                int value = board.getValue(r, c);
                if (!isValid(board, r, c, value)) return false;
            }
        return true;
    }
}
