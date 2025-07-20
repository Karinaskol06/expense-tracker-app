package com.project.expense_tracker.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
    //constructor takes message that describes what went wrong
    // and passes it to the superclass constructor
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
