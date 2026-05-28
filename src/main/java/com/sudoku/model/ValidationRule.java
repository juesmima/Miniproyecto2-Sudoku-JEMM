package com.sudoku.model;

/**
 * Represents a single Sudoku validation rule that can be pushed onto
 * the validation {@link java.util.Stack} inside {@link SudokuValidator}.
 *
 * <p>Each concrete implementation checks one constraint: row uniqueness,
 * column uniqueness, or 2x3 block uniqueness. Rules are stacked and
 * evaluated in sequence — if any rule fails, validation stops immediately
 * and the failing rule name is reported.</p>
 *
 * <p>This interface follows the <b>Strategy</b> pattern: each rule
 * encapsulates one validation algorithm, making it easy to add or
 * remove rules without modifying the validator.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 * @see SudokuValidator
 */
public interface ValidationRule {

    /**
     * Evaluates this rule against the given board for a specific cell and value.
     *
     * <p>The method must ignore the current value at (row, col) itself,
     * so that re-validating an already-filled cell works correctly.</p>
     *
     * @param board the Sudoku board to check
     * @param row   the row index of the cell being validated (0–5)
     * @param col   the column index of the cell being validated (0–5)
     * @param value the value to validate (1–6)
     * @return {@code true} if the rule is satisfied; {@code false} if violated
     */
    boolean validate(SudokuBoard board, int row, int col, int value);

    /**
     * Returns the human-readable name of this rule.
     * Used in error messages when the rule is violated.
     *
     * @return rule name, e.g. {@code "Row"}, {@code "Column"}, {@code "Block"}
     */
    String getRuleName();
}
