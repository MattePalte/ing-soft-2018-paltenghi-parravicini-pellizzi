package project.ing.soft.exceptions;

public class TimeoutOccurredException extends Exception {
    public TimeoutOccurredException(String s) {
        super(s);
    }

    public TimeoutOccurredException() {
        super("Action cannot be performed because timeout occurred");
    }
}
