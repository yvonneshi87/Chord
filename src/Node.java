import java.net.*;

public class Node {
    private static final int NUM_SUCCESSORS = 3; // Number of successors to keep in each node
    private static final int M = 32; // Number of bits used

    private String ipAddress;
    private String portNum;
    private InetSocketAddress isa;
    private long id;
    private InetSocketAddress[] fingerTable;
    private InetSocketAddress predecessor;
    private InetSocketAddress[] successors;
    private int next; // stores the index of the next finger to fix.

    private Listener listener;
    private Stabilization stabilization;
    private FingerTableFixing fingerTableFixing;
    private PredecessorChecking predecessorChecking;

    public Node(String ipAddress, String portNum) {
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        isa = Util.getInetSocketAddress(ipAddress, portNum);
        id = Util.getId(isa);
        fingerTable = new InetSocketAddress[M];
        predecessor = null;
        successors = new InetSocketAddress[NUM_SUCCESSORS];
        next = 0;
        listener = new Listener(this);
        stabilization = new Stabilization(this);
        fingerTableFixing = new FingerTableFixing(this);
        predecessorChecking = new PredecessorChecking(this);
    }

    // Join a Chord ring containing node n', meaning n' is the entry point
    // n.join(n′) {
    // predecessor = nil;
    // successor = n′.find successor(n);
    // }
    public boolean join(InetSocketAddress nPrimeIsa) {
        if (!isa.equals(this.isa)) {
            // TODO:
            successors[0] = Message.requestFindSuccessor(id, nPrimeIsa);
            if (successors[0] == null) {
                return false;
            }
        }
        startAllThreads();
        return true;
    }


    // n′ thinks it might be our predecessor.
    // n.notify(n′) {
    //  if (predecessor is nil or n′ ∈ (predecessor, n)) {
    //      predecessor = n′;
    //  }
    // }
    /* Bascially, what's happening in this function is n' believes it is the predecessor of n.
    So it will notify n like 'hey dude, I am your predecessor!'
    Once notified, n will check if n' is null or n' is indeed its predecessor.
    If true then assign n' to predecessor of n.
     */
    public void notify(InetSocketAddress nPrimeIsa) {
//        if (predecessor == null || Util.isInInterval(Util.getId(predecessor), id, Util.getId(nPrimeIsa))) {
//            this.predecessor = nPrimeIsa;
//        }

    }

    // find the successor of id
    public InetSocketAddress findSuccessor(long id) {
        if (Util.isInInterval(this.id, Util.getId(this.successors[0]), id)) {
            return this.successors[0];
        } else {
            InetSocketAddress nPrimeIsa = closestPrecedingNode(id);
            return Message.requestFindSuccessor(id, nPrimeIsa);
        }
    }

    // search the local table for the highest predecessor of id
    public InetSocketAddress closestPrecedingNode(long id) {
        for (int i = M - 1; i >= 0; i--) {
            if (Util.isInInterval(this.id, id, Util.getId(this.fingerTable[i]))) {
                return fingerTable[i];
            }
        }
        return this.isa;
    }

    // TODO: need to implement logics for multiple successors
    private void findSuccessors(long id) {
        // for (int i = 0; i < Chord.NUM_SUCCESSORS; i++) {
        // successors[i] = findSuccessor(id);
        // }
    }

    private void startAllThreads() {
        listener.start();
        stabilization.start();
        fingerTableFixing.start();
        predecessorChecking.start();
    }

    private void terminateAllThreads() {
        listener.terminate();
        stabilization.terminate();
        fingerTableFixing.terminate();
        predecessorChecking.terminate();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*****NODE**INFO*****\n");
        // TODO
        return sb.toString();
    }

    public InetSocketAddress getIsa() {
        return isa;
    }

    public long getId() {
        return id;
    }

    public String getIp(){
        return ipAddress;
    }

    public int getPort() {
        return Integer.parseInt(portNum);
    }

    public InetSocketAddress getPredecessor() {
        return predecessor;
    }
}
