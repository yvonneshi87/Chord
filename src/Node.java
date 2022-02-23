import java.net.*;
import java.util.*;

public class Node {
    private static final int M = 32;
    private InetSocketAddress isa;
    private long id;
    private Node[] fingerTable;
    private Node predecessor;
    private Node[] successors;


    public Node(InetSocketAddress isa) {
        this.isa = isa;
        id = Util.hashIsa(isa);
        createFingerTable();
        predecessor = null;
        successors = null;
    }


    // join a Chord ring containing node n'
    public boolean join(InetSocketAddress isa) {
        // need implementation
        return true;
    }
    public boolean join(Node existingNode) {
        // id = -1 means null node
        predecessor = null;
        findSuccessors(id);
        return true;
    }

    private void stabilize() {
        // implement
    }

    private void findSuccessors(long id) {
        successors = new Node[3];
        for (int i = 0; i < 3; i++) {
            successors[i] = findSuccessor(id);
        }
    }

    private void createFingerTable() {
        fingerTable = new Node[M];
        // implement
    }

    public Node findSuccessor(long id) {
        // implement
        return null;
    }

    // search the local table for the highest predecessor of id
    public Node closestPrecedingNode(long id) {
        return null;
    }

    public InetSocketAddress getIsa() {
        return isa;
    }
}
