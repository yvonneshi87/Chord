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
    private Node predecessor;
    private InetSocketAddress[] successors;
    private int next; // stores the index of the next finger to fix.
    private Listener listener;

    public Node(String ipAddress, String portNum) {
        this.ipAddress = ipAddress;
        this.portNum = portNum;
        isa = Util.getInetSocketAddress(ipAddress, portNum);
        assignId();
        fingerTable = new InetSocketAddress[M];
        predecessor = null;
        successors = new InetSocketAddress[NUM_SUCCESSORS];
        next = 0;
        listener = new Listener(this);
    }

    // This method hashes (ip address + port number) to 160 bit String
    // hashText is 160 bits long (= 40 hex digits * 4 bit per hex digit)
    // truncates hashText to 32 bits
    // gets peer id between 0 and (2^m - 1) by converting truncatedHashText to a long number
    private void assignId() {
        String input = ipAddress + ":" + portNum;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            // TODO: NOT SURE IF TRUNCATION IS CORRECT
            String truncatedHashText = hashText.substring(0, 9);
            id = Long.parseLong(truncatedHashText, 16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
        if (next >= M) {
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
        for (int i = M - 1; i >= 0; i--) {
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
}
