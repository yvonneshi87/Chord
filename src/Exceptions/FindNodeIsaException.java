package Exceptions;

public class FindNodeIsaException extends Exception {
    public FindNodeIsaException(String isaInfo) {
        super("Fatal error when finding isa of " + isaInfo);
    }
}
