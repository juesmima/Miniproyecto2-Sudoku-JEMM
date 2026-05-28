package com.sudoku.controller;

import com.sudoku.model.Move;
import com.sudoku.model.SudokuBoard;
import com.sudoku.model.SudokuGenerator;
import com.sudoku.model.SudokuHint;
import com.sudoku.model.SudokuHint.Hint;
import com.sudoku.model.SudokuValidator;
import com.sudoku.model.ValidationResult;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 * Controller for the 6x6 Sudoku game.
 *
 * <p>Connects the {@link SudokuBoard} model to the FXML view following the
 * MVC pattern. Handles all user interactions: cell selection via mouse,
 * number input via keyboard and on-screen numpad, undo, hint, erase,
 * and new game.</p>
 *
 * <h2>Cell rendering</h2>
 * <p>Each cell is a {@link Button} added programmatically to {@link #boardGrid}.
 * CSS style classes are applied dynamically to reflect the cell state:
 * fixed, selected, error, or hint.</p>
 *
 * <h2>Stack usage</h2>
 * <ul>
 *   <li><b>Validation stack</b>: {@link SudokuValidator#validate} pushes and pops
 *       three {@code ValidationRule} objects on every input.</li>
 *   <li><b>Undo stack</b>: {@link SudokuBoard#undo()} pops the last {@link Move}
 *       and restores the previous cell value.</li>
 * </ul>
 *
 * @author Sudoku Team
 * @version 1.0
 */
public class SudokuController {

    // -------------------------------------------------------------------------
    // FXML injected fields
    // -------------------------------------------------------------------------

    /** The 6x6 grid pane that holds all cell buttons. */
    @FXML private GridPane boardGrid;

    /** Label showing the number of moves made. */
    @FXML private Label movesLabel;

    /** Label showing validation messages, hints, and game status. */
    @FXML private Label statusLabel;

    // -------------------------------------------------------------------------
    // Model instances
    // -------------------------------------------------------------------------

    /** The Sudoku board model — holds grid values, fixed flags, and move history stack. */
    private final SudokuBoard board = new SudokuBoard();

    /** Generates a valid randomized puzzle on each new game. */
    private final SudokuGenerator generator = new SudokuGenerator();

    /** Provides hint suggestions for empty cells. */
    private final SudokuHint hintProvider = new SudokuHint();

    // -------------------------------------------------------------------------
    // UI state
    // -------------------------------------------------------------------------

    /** 6x6 array of cell Buttons, mirroring the board grid. */
    private Button[][] cells;

    /** Currently selected cell coordinates, or {-1, -1} if none selected. */
    private int selectedRow = -1;
    private int selectedCol = -1;

    /** Last hint provided, used to highlight the hinted cell. */
    private Hint currentHint = null;

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    /**
     * Called automatically by JavaFX after FXML injection.
     * Builds the cell grid and starts a new game.
     */
    @FXML
    public void initialize() {
        buildCellGrid();
        startNewGame();
    }

    /**
     * Builds the 6x6 grid of {@link Button} cells and adds them to {@link #boardGrid}.
     *
     * <p>Each button is assigned a mouse click handler via an anonymous inner class
     * that captures the cell coordinates and calls {@link #selectCell(int, int)}.</p>
     */
    private void buildCellGrid() {
        cells = new Button[SudokuBoard.SIZE][SudokuBoard.SIZE];
        boardGrid.getChildren().clear();

        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                final int row = r;
                final int col = c;

                Button cell = new Button();
                cell.setPrefSize(58, 58);
                cell.setMinSize(58, 58);
                cell.getStyleClass().add("cell");
                applyCellBorderStyle(cell, row, col);

                // Mouse click handler — inner class style
                cell.setOnMouseClicked(event -> selectCell(row, col));

                cells[r][c] = cell;
                boardGrid.add(cell, c, r);
            }
        }
    }

    /**
     * Applies the correct block-border CSS class to a cell based on its position.
     *
     * <p>Cells at column 2 get a thick right border (block separator).
     * Cells at row 1 and row 3 get a thick bottom border.
     * Cells at both positions get a corner class.</p>
     *
     * @param cell the Button to style
     * @param row  the row index (0–5)
     * @param col  the column index (0–5)
     */
    private void applyCellBorderStyle(Button cell, int row, int col) {
        boolean blockRight  = (col == 2);
        boolean blockBottom = (row == 1 || row == 3);

        if (blockRight && blockBottom) {
            cell.getStyleClass().add("cell-block-corner");
        } else if (blockRight) {
            cell.getStyleClass().add("cell-block-right");
        } else if (blockBottom) {
            cell.getStyleClass().add("cell-block-bottom");
        }
    }

    // -------------------------------------------------------------------------
    // Game lifecycle
    // -------------------------------------------------------------------------

    /**
     * Generates a new puzzle, resets selection and UI state, and re-renders the board.
     */
    private void startNewGame() {
        generator.generate(board);
        selectedRow = -1;
        selectedCol = -1;
        currentHint = null;
        refreshAllCells();
        setStatus("", "");
        updateMovesLabel();
    }

    // -------------------------------------------------------------------------
    // Cell interaction
    // -------------------------------------------------------------------------

    /**
     * Selects a cell when the player clicks it.
     *
     * <p>Fixed cells are not selectable. Selecting an already-selected
     * cell deselects it. Clears any active hint on selection change.</p>
     *
     * @param row the row index of the clicked cell (0–5)
     * @param col the column index of the clicked cell (0–5)
     */
    private void selectCell(int row, int col) {
        if (board.isFixed(row, col)) return;

        if (selectedRow == row && selectedCol == col) {
            selectedRow = -1;
            selectedCol = -1;
        } else {
            selectedRow = row;
            selectedCol = col;
        }
        currentHint = null;
        setStatus("", "");
        refreshAllCells();

        // Request focus on the scene root so keyboard events are captured
        boardGrid.getScene().getRoot().requestFocus();
    }

    /**
     * Handles keyboard input on the scene.
     *
     * <p>Keys 1–6 enter a number in the selected cell.
     * Backspace or Delete clears the selected cell.
     * This method is connected to the scene's {@code setOnKeyPressed} in
     * {@link com.sudoku.app.SudokuApp}.</p>
     *
     * @param event the keyboard event fired by the scene
     */
    public void handleKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case DIGIT1, NUMPAD1 -> enterValue(1);
            case DIGIT2, NUMPAD2 -> enterValue(2);
            case DIGIT3, NUMPAD3 -> enterValue(3);
            case DIGIT4, NUMPAD4 -> enterValue(4);
            case DIGIT5, NUMPAD5 -> enterValue(5);
            case DIGIT6, NUMPAD6 -> enterValue(6);
            case BACK_SPACE, DELETE -> eraseSelected();
            default -> { /* ignore */ }
        }
    }

    // -------------------------------------------------------------------------
    // FXML event handlers
    // -------------------------------------------------------------------------

    /**
     * Handles clicks on the on-screen number pad buttons (1–6).
     * Reads the button's {@code userData} attribute to determine the number.
     *
     * @param event the action event fired by the number button
     */
    @FXML
    private void onNumberPressed(javafx.event.ActionEvent event) {
        Button source = (Button) event.getSource();
        int value = Integer.parseInt(source.getUserData().toString());
        enterValue(value);
    }

    /**
     * Handles the Undo button click.
     * Pops the last {@link Move} from the board's history stack and refreshes the UI.
     */
    @FXML
    private void onUndo() {
        Move undone = board.undo();
        if (undone == null) {
            setStatus("No hay movimientos para deshacer.", "status-error");
            return;
        }
        currentHint = null;
        refreshAllCells();
        updateMovesLabel();
        setStatus("Movimiento deshecho.", "status-ok");
    }

    /**
     * Handles the Hint button click.
     *
     * <p>Requests a hint from {@link SudokuHint}, highlights the suggested cell
     * in green, and shows the suggestion in the status label.
     * The hint value is NOT automatically placed — the player decides.</p>
     */
    @FXML
    private void onHint() {
        currentHint = hintProvider.getHint(board);
        if (currentHint == null) {
            setStatus("No hay sugerencias disponibles.", "status-error");
            return;
        }
        selectedRow = -1;
        selectedCol = -1;
        refreshAllCells();
        setStatus("Sugerencia: fila " + (currentHint.row + 1)
                + ", columna " + (currentHint.col + 1)
                + " → " + currentHint.value, "status-hint");
    }

    /**
     * Handles the Erase button click.
     * Clears the value of the currently selected cell if it is not fixed.
     */
    @FXML
    private void onErase() {
        eraseSelected();
    }

    /**
     * Handles the New Game button click.
     * Generates a fresh puzzle and resets all game state.
     */
    @FXML
    private void onNewGame() {
        startNewGame();
    }

    // -------------------------------------------------------------------------
    // Core game logic
    // -------------------------------------------------------------------------

    /**
     * Attempts to place a value in the currently selected cell.
     *
     * <p>Workflow:
     * <ol>
     *   <li>Checks that a non-fixed cell is selected.</li>
     *   <li>Calls {@link SudokuValidator#validate} which runs the stack-based
     *       rule evaluation (Row → Column → Block).</li>
     *   <li>Places the value via {@link SudokuBoard#setValue}, which pushes a
     *       {@link Move} onto the undo stack.</li>
     *   <li>Updates the cell style to reflect valid or error state.</li>
     *   <li>Checks if the board is complete.</li>
     * </ol>
     * </p>
     *
     * @param value the number to place (1–6)
     */
    private void enterValue(int value) {
        if (selectedRow < 0 || selectedCol < 0) {
            setStatus("Selecciona una celda primero.", "status-error");
            return;
        }
        if (board.isFixed(selectedRow, selectedCol)) {
            setStatus("Esta celda es fija.", "status-error");
            return;
        }

        // Stack-based validation: pushes RowRule, ColumnRule, BlockRule and pops them
        ValidationResult result = SudokuValidator.validate(board, selectedRow, selectedCol, value);

        // Place the value regardless — user sees error highlight if invalid
        board.setValue(selectedRow, selectedCol, value);
        currentHint = null;
        refreshAllCells();
        updateMovesLabel();

        if (!result.isValid()) {
            setStatus(result.getMessage(), "status-error");
        } else {
            setStatus("", "");
            if (SudokuValidator.isBoardComplete(board)) {
                setStatus("¡Felicidades! Sudoku completado 🎉", "status-ok");
            }
        }
    }

    /**
     * Clears the value of the currently selected cell.
     * Pushes a {@link Move}(prev, 0) onto the undo stack via {@link SudokuBoard#setValue}.
     */
    private void eraseSelected() {
        if (selectedRow < 0 || selectedCol < 0) {
            setStatus("Selecciona una celda primero.", "status-error");
            return;
        }
        if (board.isFixed(selectedRow, selectedCol)) {
            setStatus("Esta celda es fija.", "status-error");
            return;
        }
        if (board.isEmpty(selectedRow, selectedCol)) {
            setStatus("La celda ya está vacía.", "");
            return;
        }
        board.setValue(selectedRow, selectedCol, 0);
        currentHint = null;
        refreshAllCells();
        updateMovesLabel();
        setStatus("Celda borrada.", "");
    }

    // -------------------------------------------------------------------------
    // UI rendering helpers
    // -------------------------------------------------------------------------

    /**
     * Refreshes the visual state of every cell in the grid.
     *
     * <p>For each cell, clears dynamic style classes and re-applies the correct
     * one based on the current model state:
     * {@code cell-fixed}, {@code cell-selected}, {@code cell-error}, or {@code cell-hint}.</p>
     */
    private void refreshAllCells() {
        for (int r = 0; r < SudokuBoard.SIZE; r++) {
            for (int c = 0; c < SudokuBoard.SIZE; c++) {
                refreshCell(r, c);
            }
        }
    }

    /**
     * Refreshes the visual state of a single cell button.
     *
     * @param row the row index (0–5)
     * @param col the column index (0–5)
     */
    private void refreshCell(int row, int col) {
        Button cell = cells[row][col];
        int value = board.getValue(row, col);

        // Remove all dynamic state classes
        cell.getStyleClass().removeAll(
                "cell-fixed", "cell-selected", "cell-error", "cell-hint"
        );

        // Apply state class and set text
        if (board.isFixed(row, col)) {
            cell.getStyleClass().add("cell-fixed");
            cell.setText(String.valueOf(value));

        } else if (currentHint != null && currentHint.row == row && currentHint.col == col) {
            cell.getStyleClass().add("cell-hint");
            cell.setText(String.valueOf(currentHint.value));

        } else if (row == selectedRow && col == selectedCol) {
            cell.getStyleClass().add("cell-selected");
            cell.setText(value > 0 ? String.valueOf(value) : "");

        } else if (value > 0 && !SudokuValidator.isValid(board, row, col, value)) {
            cell.getStyleClass().add("cell-error");
            cell.setText(String.valueOf(value));

        } else {
            cell.setText(value > 0 ? String.valueOf(value) : "");
        }
    }

    /**
     * Updates the status label with a message and an optional CSS style class.
     *
     * @param message   the text to display (empty string clears the label)
     * @param styleClass one of: {@code "status-error"}, {@code "status-hint"},
     *                   {@code "status-ok"}, or {@code ""} for default
     */
    private void setStatus(String message, String styleClass) {
        statusLabel.getStyleClass().removeAll("status-error", "status-hint", "status-ok");
        statusLabel.setText(message);
        if (!styleClass.isEmpty()) {
            statusLabel.getStyleClass().add(styleClass);
        }
    }

    /**
     * Updates the moves label with the current number of moves from the undo stack.
     */
    private void updateMovesLabel() {
        int count = board.getMoveCount();
        movesLabel.setText(count + (count == 1 ? " movimiento" : " movimientos"));
    }
}
