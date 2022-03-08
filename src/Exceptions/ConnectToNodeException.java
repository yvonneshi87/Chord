package Exceptions;

public class ConnectToNodeException extends Exception {
    public ConnectToNodeException(String nodeStr) {
        super("Fatal error when connecting to the node of " + nodeStr);
    }

}
