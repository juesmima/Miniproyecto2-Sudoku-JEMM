package com.sudoku.model;

/**
 * Encapsulates the outcome of a stack-based Sudoku validation.
 *
 * <p>After {@link SudokuValidator} pops and evaluates every
 * {@link ValidationRule} from the validation stack, it returns a
 * {@code ValidationResult} that tells the controller:</p>
 * <ul>
 *   <li>Whether the move is valid.</li>
 *   <li>Which rule was violated (if any), so the UI can show a precise message.</li>
 * </ul>
 *
 * <p>This class is immutable. Use the static factory methods
 * {@link #ok()} and {@link #fail(String)} to create instances.</p>
 *
 * @author Sudoku Team
 * @version 1.0
 * @see SudokuValidator#validate(SudokuBoard, int, int, int)
 */
public class ValidationResult {

    /** Whether all rules passed. */
    private final boolean valid;

    /**
     * Name of the rule that was violated, or {@code null} if validation passed.
     * Examples: {@code "Row"}, {@code "Column"}, {@code "Block"}.
     */
    private final String violatedRule;

    /**
     * Private constructor — use {@link #ok()} or {@link #fail(String)}.
     *
     * @param valid       whether validation passed
     * @param violatedRule the name of the violated rule, or {@code null}
     */
    private ValidationResult(boolean valid, String violatedRule) {
        this.valid = valid;
        this.violatedRule = violatedRule;
    }

    /**
     * Creates a successful validation result (all rules passed).
     *
     * @return a {@code ValidationResult} with {@code valid = true}
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    /**
     * Creates a failed validation result identifying the violated rule.
     *
     * @param ruleName the name of the rule that failed (e.g. {@code "Row"})
     * @return a {@code ValidationResult} with {@code valid = false}
     */
    public static ValidationResult fail(String ruleName) {
        return new ValidationResult(false, ruleName);
    }

    /**
     * Returns whether all validation rules passed.
     *
     * @return {@code true} if the move is valid
     */
    public boolean isValid() { return valid; }

    /**
     * Returns the name of the first rule that was violated.
     *
     * @return the violated rule name, or {@code null} if validation passed
     */
    public String getViolatedRule() { return violatedRule; }

    /**
     * Returns a human-readable error message describing the violation,
     * or a success message if validation passed.
     *
     * @return descriptive message for the UI
     */
    public String getMessage() {
        if (valid) return "Valid move.";
        return "Invalid move: duplicate value in " + violatedRule + ".";
    }

    /**
     * Returns a string representation of this result.
     *
     * @return e.g. {@code "ValidationResult[valid=false, rule=Column]"}
     */
    @Override
    public String toString() {
        return "ValidationResult[valid=" + valid + ", rule=" + violatedRule + "]";
    }
}
