package project.ing.soft.exceptions;

public class ConnectionRefusedException extends Exception {
    private Exception cause;

    public ConnectionRefusedException(Exception cause){
        this.cause = cause;
    }

    public Exception getCause(){
        return cause;
    }

    @Override
    public String getMessage(){
        return cause.getMessage();
    }
}
