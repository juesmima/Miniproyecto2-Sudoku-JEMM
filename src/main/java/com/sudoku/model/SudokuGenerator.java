package com.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Generates a valid, randomized 6x6 Sudoku puzzle.
 *
 * <h2>Generation process</h2>
 * <ol>
 *   <li>Fills the board with a complete valid solution using randomized
 *       backtracking. At each empty cell, numbers 1–6 are tried in a
 *       shuffled order, ensuring a different solution each time.</li>
 *   <li>Snapshots the solved grid.</li>
 *   <li>Clears the board and re-applies exactly {@link SudokuBoard#FIXED_PER_BLOCK}
 *       randomly chosen cells per 2x3 block as fixed (pre-filled) cells.</li>
 * </ol>
 *
 * <p>The backtracking step uses {@link SudokuValidator#isValid(SudokuBoard, int, int, int)}
 * to check placement, ensuring the generated solution is always valid.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 */
public class SudokuGenerator {

    /** Random instance used for shuffling numbers and selecting fixed cells. */
    private final Random random;

    /**
     * Constructs a {@code SudokuGenerator} with a random seed,
     * producing a unique board on each run.
     */
    public SudokuGenerator() {
        this.random = new Random();
    }

    /**
     * Generates a new randomized Sudoku puzzle and stores it in the given board.
     *
     * <p>Clears the board, solves it with shuffled backtracking, then exposes
     * exactly {@link SudokuBoard#FIXED_PER_BLOCK} fixed cells per block.</p>
     *
     * @param board the {@link SudokuBoard} to populate; will be cleared first
     */
    public void generate(SudokuBoard board) {
        board.clear();
        solve(board);
        applyFixedCells(board);
    }

    /**
     * Fills the board with a complete valid solution using randomized backtracking.
     *
     * <p>Iterates cells in row-major order. For each empty cell, tries numbers
     * 1–6 in a shuffled sequence and recurses. If no number fits, resets the
     * cell to 0 and returns {@code false} to trigger backtracking.</p>
     *
     * @param board the board to solve in-place (uses direct grid writes, no move history)
     * @return {@code true} if the board was successfully completed
     */
    private boolean solve(SudokuBoard board) {
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (board.getValue(row, col) == 0) {
                    for (int num : shuffledNumbers()) {
                        if (SudokuValidator.isValid(board, row, col, num)) {
                            // Use direct grid write during generation (no move history needed)
                            board.setValue(row, col, num);
                            if (solve(board)) return true;
                            board.setValue(row, col, 0);
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Selects {@link SudokuBoard#FIXED_PER_BLOCK} random cells per 2x3 block
     * to be pre-filled, then clears all other cells.
     *
     * <p>Block layout ({@code blockRow} × {@code blockCol}):</p>
     * <pre>
     *   (0,0) (0,1)
     *   (1,0) (1,1)
     *   (2,0) (2,1)
     * </pre>
     * <p>Each block spans rows {@code [br*2 .. br*2+1]} and
     * columns {@code [bc*3 .. bc*3+2]}.</p>
     *
     * @param board the fully solved board from which fixed cells are chosen
     */
    private void applyFixedCells(SudokuBoard board) {
        int[][] solved = board.getGridCopy();
        board.clear();

        int blockRowCount = SudokuBoard.SIZE / SudokuBoard.BLOCK_ROWS; // 3
        int blockColCount = SudokuBoard.SIZE / SudokuBoard.BLOCK_COLS; // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {
                List<int[]> cells = new ArrayList<>();
                int rowStart = br * SudokuBoard.BLOCK_ROWS;
                int colStart = bc * SudokuBoard.BLOCK_COLS;

                for (int r = rowStart; r < rowStart + SudokuBoard.BLOCK_ROWS; r++)
                    for (int c = colStart; c < colStart + SudokuBoard.BLOCK_COLS; c++)
                        cells.add(new int[]{r, c});

                Collections.shuffle(cells, random);

                for (int i = 0; i < SudokuBoard.FIXED_PER_BLOCK; i++) {
                    int r = cells.get(i)[0];
                    int c = cells.get(i)[1];
                    board.setFixed(r, c, solved[r][c]);
                }
            }
        }
    }

    /**
     * Returns a list of integers 1–{@link SudokuBoard#SIZE} in random order.
     *
     * @return shuffled list of valid Sudoku values
     */
    private List<Integer> shuffledNumbers() {
        List<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= SudokuBoard.SIZE; i++) nums.add(i);
        Collections.shuffle(nums, random);
        return nums;
    }
}
