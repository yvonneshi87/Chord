import java.math.BigInteger;
import java.net.*;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        listener.start();
        stabilization.start();
        return true;
    }


    // nPrime thinks it might be our predecessor
    public void notify(InetSocketAddress nPrimeIsa) {
        if (this.predecessor == null || Util.isInInterval(Util.getId(this.predecessor), this.id, Util.getId(nPrimeIsa))) {
            this.predecessor = nPrimeIsa;
        }
    }


    // called periodically. checks whether predecessor has failed.
    public void checkPredecessor() {
        if (Message.requestPing(this.predecessor) == false) {
            this.predecessor = null;
        }
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
