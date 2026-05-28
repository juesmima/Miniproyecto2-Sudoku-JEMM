package com.sudoku.model;

/**
 * Represents a single player move on the Sudoku board.
 *
 * <p>Each {@code Move} stores the cell position and both the previous
 * and new values, allowing the action to be fully undone by restoring
 * the previous state. Instances are pushed onto the undo stack
 * inside {@link SudokuBoard} every time the player edits a cell.</p>
 *
 * <p>This class is immutable.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 * @see SudokuBoard#setValue(int, int, int)
 * @see SudokuBoard#undo()
 */
public class Move {

    /** Row index of the affected cell (0–5). */
    private final int row;

    /** Column index of the affected cell (0–5). */
    private final int col;

    /** Value that was in the cell before this move (0 = empty). */
    private final int previousValue;

    /** Value placed in the cell by this move (0 = deletion). */
    private final int newValue;

    /**
     * Constructs a {@code Move} recording a change in a board cell.
     *
     * @param row           the row index of the cell (0–5)
     * @param col           the column index of the cell (0–5)
     * @param previousValue the value before the move (0 if the cell was empty)
     * @param newValue      the value after the move (0 if the cell was cleared)
     */
    public Move(int row, int col, int previousValue, int newValue) {
        this.row = row;
        this.col = col;
        this.previousValue = previousValue;
        this.newValue = newValue;
    }

    /**
     * Returns the row index of the cell affected by this move.
     *
     * @return row index (0–5)
     */
    public int getRow() { return row; }

    /**
     * Returns the column index of the cell affected by this move.
     *
     * @return column index (0–5)
     */
    public int getCol() { return col; }

    /**
     * Returns the value the cell held before this move.
     *
     * @return previous cell value (0 if it was empty)
     */
    public int getPreviousValue() { return previousValue; }

    /**
     * Returns the value placed in the cell by this move.
     *
     * @return new cell value (0 if the cell was cleared)
     */
    public int getNewValue() { return newValue; }

    /**
     * Returns a human-readable description of this move.
     *
     * @return string in the format: "Move[row=1, col=3, prev=0, new=4]"
     */
    @Override
    public String toString() {
        return "Move[row=" + row + ", col=" + col
                + ", prev=" + previousValue + ", new=" + newValue + "]";
    }
}
