package Exceptions;

public class ParseArgumentException extends Exception {
    public ParseArgumentException() {
        super("Fatal error when parsing arguments! Now exit.");
    }

}
