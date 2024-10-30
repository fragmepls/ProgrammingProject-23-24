package it.scheduleplanner.planner;

/**
 * Custom exception thrown when there are not enough employees to cover the required shifts.
 */
public class InsufficientEmployeesException extends Exception {
    /**
     * Constructs a new InsufficientEmployeesException with the specified detail message.
     *
     * @param message The detail message.
     */
    public InsufficientEmployeesException(String message) {
        super(message);
    }
}
