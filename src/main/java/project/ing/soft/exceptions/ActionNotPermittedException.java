package project.ing.soft.exceptions;

public class ActionNotPermittedException extends Exception {
    public ActionNotPermittedException(String s) {
        super(s);
    }

    public ActionNotPermittedException() {
        super("You do not have permissions to do that");
    }
}
