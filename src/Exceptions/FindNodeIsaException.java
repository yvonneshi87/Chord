package Exceptions;

public class FindNodeIsaException extends Exception {
    public FindNodeIsaException(String nodeStr) {
        super("Fatal error when finding isa of " + nodeStr + ". Now exit.");
    }
}
