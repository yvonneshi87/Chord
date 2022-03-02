import java.net.*;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;

public class Node {
    private InetSocketAddress isa;
    private long id;
    private Node[] fingerTable;
    private Node predecessor;
    private Node[] successors;
    private int next; // stores the index of the next finger to fix.
    private ServerSocket server;
    private boolean active;

    public Node(InetSocketAddress isa) {
        this.isa = isa;
        id = Util.hashIsa(isa);
        createFingerTable();
        predecessor = null;
        createSuccessors();
        next = 0;
        openServer(); // when build a node, it will open the server on the local port.
    }

    // open server
    private void openServer(){
        int port = this.isa.getPort();
        try{
            server = new ServerSocket(port);
        }catch (IOException e){
            throw new RuntimeException("Can't open server port " + port, e);
        }
    }

    // run socket to accept
    public void runServer(){
        while(active){
            Socket communicator = null;
            try{
                communicator = this.server.accept();
            }catch (IOException e) {
                throw new RuntimeException("Can't accept message ", e);
            }
        }
    }

    // kill the server
    public void killServer(){
        this.active = false;
    }

    // create a new Chord ring
    public void create() {
        predecessor = null;
        successors[0] = this;
    }

    // join a Chord ring containing node n'
    public boolean join(InetSocketAddress isa) {
        Node nPrime = new Node(isa);
        predecessor = null;
        successors[0] = Message.requestFindSuccessor(this.id, nPrime);
        return true;
    }

    // called periodically. verifies this node’s immediate successor, and tells the
    // successor about this node
    public void stabilize() {
        Node x = Message.requestReturnPredecessor(this.successors[0]);
        if (Util.isInInterval(this.id, this.successors[0].id, x.id)) {
            successors[0] = x;
        }
        Message.requestNotify(this, this.successors[0]);
    }

    // nPrime thinks it might be our predecessor
    public void notify(Node nPrime) {
        if (this.predecessor == null || Util.isInInterval(this.predecessor.id, this.id, nPrime.id)) {
            this.predecessor = nPrime;
        }
    }

    // called periodically. refreshes finger table entries
    public void fixFingers() {
        this.fingerTable[next] = findSuccessor(Util.ringAdd(this.id, 1 << next));
        next++;
        if (next >= Chord.M) {
            next = 0;
        }
    }

    // called periodically. checks whether predecessor has failed.
    public void checkPredecessor() {
        if (Message.requestPing(this.predecessor) == false) {
            this.predecessor = null;
        }
    }

    // find the successor of id
    public Node findSuccessor(long id) {
        if (Util.isInInterval(this.id, this.successors[0].id, id)) {
            return this.successors[0];
        } else {
            Node nPrime = closestPrecedingNode(id);
            return Message.requestFindSuccessor(id, nPrime);
        }
    }

    // search the local table for the highest predecessor of id
    public Node closestPrecedingNode(long id) {
        for (int i = Chord.M - 1; i >= 0; i--) {
            if (Util.isInInterval(this.id, id, this.fingerTable[i].id)) {
                return fingerTable[i];
            }
        }

        return this;
    }

    // TODO: need to implement logics for multiple successors
    private void findSuccessors(long id) {
        // for (int i = 0; i < Chord.NUM_SUCCESSORS; i++) {
        // successors[i] = findSuccessor(id);
        // }
    }

    // Create new finger table array
    private void createFingerTable() {
        fingerTable = new Node[Chord.M];
    }

    // create new successors array
    private void createSuccessors() {
        successors = new Node[Chord.NUM_SUCCESSORS];
    }

    public InetSocketAddress getIsa() {
        return isa;
    }
}
