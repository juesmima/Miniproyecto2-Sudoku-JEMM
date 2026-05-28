package com.sudoku.model;

import java.util.Stack;

/**
 * Represents the 6x6 Sudoku board and maintains a {@link Stack} of {@link Move}
 * objects to support unlimited undo operations.
 *
 * <h2>Undo Stack behavior</h2>
 * <p>Every time the player successfully sets a value in a non-fixed cell,
 * a {@link Move} recording the previous and new values is pushed onto
 * {@link #moveHistory}. Calling {@link #undo()} pops the most recent move
 * and restores the previous value — standard LIFO (Last In, First Out) behavior.</p>
 *
 * <p>Fixed cells (pre-filled during board generation) cannot be edited and
 * do not generate move history entries.</p>
 *
 * <h2>Board layout</h2>
 * <pre>
 *  [0,0][0,1][0,2] | [0,3][0,4][0,5]
 *  [1,0][1,1][1,2] | [1,3][1,4][1,5]
 *  --------------------------------
 *  [2,0][2,1][2,2] | [2,3][2,4][2,5]
 *  [3,0][3,1][3,2] | [3,3][3,4][3,5]
 *  --------------------------------
 *  [4,0][4,1][4,2] | [4,3][4,4][4,5]
 *  [5,0][5,1][5,2] | [5,3][5,4][5,5]
 * </pre>
 *
 * @author Sudoku Team
 * @version 1.0
 */
public class SudokuBoard {

    /** Size of the board (6x6). */
    public static final int SIZE = 6;

    /** Number of rows per 2x3 block. */
    public static final int BLOCK_ROWS = 2;

    /** Number of columns per 2x3 block. */
    public static final int BLOCK_COLS = 3;

    /** Number of fixed (pre-filled) cells per block shown at game start. */
    public static final int FIXED_PER_BLOCK = 2;

    /** Current values on the board. 0 means the cell is empty. */
    private final int[][] grid;

    /** Tracks which cells are fixed and cannot be modified by the player. */
    private final boolean[][] fixed;

    /**
     * Stack that records every player move in chronological order.
     * The top of the stack is always the most recent move.
     * Used to implement unlimited undo functionality.
     */
    private final Stack<Move> moveHistory;

    /**
     * Constructs an empty 6x6 Sudoku board with an empty move history stack.
     */
    public SudokuBoard() {
        grid = new int[SIZE][SIZE];
        fixed = new boolean[SIZE][SIZE];
        moveHistory = new Stack<>();
    }

    // -------------------------------------------------------------------------
    // Cell access
    // -------------------------------------------------------------------------

    /**
     * Returns the current value at the given cell.
     *
     * @param row the row index (0–5)
     * @param col the column index (0–5)
     * @return the cell value (1–6), or 0 if empty
     */
    public int getValue(int row, int col) {
        return grid[row][col];
    }

    /**
     * Sets the value of a non-fixed cell and pushes a {@link Move} onto
     * the undo stack.
     *
     * <p>If the cell is fixed, the method returns {@code false} and no
     * history entry is recorded.</p>
     *
     * @param row   the row index (0–5)
     * @param col   the column index (0–5)
     * @param value the value to set (1–6), or 0 to clear the cell
     * @return {@code true} if the value was set; {@code false} if the cell is fixed
     */
    public boolean setValue(int row, int col, int value) {
        if (fixed[row][col]) return false;

        int previous = grid[row][col];
        grid[row][col] = value;
        moveHistory.push(new Move(row, col, previous, value));
        return true;
    }

    /**
     * Marks a cell as fixed with a given value during board generation.
     * Fixed cells are not editable by the player and do not enter the move history.
     *
     * @param row   the row index (0–5)
     * @param col   the column index (0–5)
     * @param value the fixed value to assign (1–6)
     */
    public void setFixed(int row, int col, int value) {
        grid[row][col] = value;
        fixed[row][col] = true;
    }

    /**
     * Returns whether a cell is fixed (pre-filled and non-editable).
     *
     * @param row the row index (0–5)
     * @param col the column index (0–5)
     * @return {@code true} if the cell is fixed
     */
    public boolean isFixed(int row, int col) {
        return fixed[row][col];
    }

    /**
     * Returns whether a cell is empty (value == 0).
     *
     * @param row the row index (0–5)
     * @param col the column index (0–5)
     * @return {@code true} if the cell has no value
     */
    public boolean isEmpty(int row, int col) {
        return grid[row][col] == 0;
    }

    // -------------------------------------------------------------------------
    // Undo (Stack operations)
    // -------------------------------------------------------------------------

    /**
     * Undoes the most recent player move by popping the top {@link Move}
     * from the history stack and restoring the cell's previous value.
     *
     * <p>This operation directly writes to {@link #grid} without pushing a new
     * entry onto the stack, so the stack only shrinks on undo.</p>
     *
     * @return the {@link Move} that was undone, or {@code null} if the stack is empty
     */
    public Move undo() {
        if (moveHistory.isEmpty()) return null;

        Move last = moveHistory.pop();
        grid[last.getRow()][last.getCol()] = last.getPreviousValue();
        return last;
    }

    /**
     * Returns whether there are any moves available to undo.
     *
     * @return {@code true} if the move history stack is not empty
     */
    public boolean canUndo() {
        return !moveHistory.isEmpty();
    }

    /**
     * Returns the number of moves currently stored in the undo stack.
     * Useful for displaying move count or debugging.
     *
     * @return size of the move history stack
     */
    public int getMoveCount() {
        return moveHistory.size();
    }

    /**
     * Returns a reference to the move history stack.
     * Intended for read-only inspection (e.g. displaying history in the UI).
     *
     * @return the move history {@link Stack}
     */
    public Stack<Move> getMoveHistory() {
        return moveHistory;
    }

    // -------------------------------------------------------------------------
    // Board state
    // -------------------------------------------------------------------------

    /**
     * Clears the entire board: resets all values to 0, all fixed flags to false,
     * and empties the move history stack.
     */
    public void clear() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++) {
                grid[r][c] = 0;
                fixed[r][c] = false;
            }
        moveHistory.clear();
    }

    /**
     * Returns whether all cells on the board are filled (no empty cells).
     *
     * @return {@code true} if no cell has value 0
     */
    public boolean isFull() {
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (grid[r][c] == 0) return false;
        return true;
    }

    /**
     * Returns a deep copy of the current value grid.
     * Used by {@link SudokuGenerator} to snapshot the solved board
     * before selecting fixed cells.
     *
     * @return a 6x6 int array with the current board values
     */
    public int[][] getGridCopy() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++)
            copy[r] = grid[r].clone();
        return copy;
    }
}
