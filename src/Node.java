import java.net.*;

public class Node {
    private static final int NUM_SUCCESSORS = 3; // Number of successors to keep in each node
    private static final int M = 4; // Number of bits used

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
    public synchronized boolean join(InetSocketAddress nPrimeIsa) {
        if (!nPrimeIsa.equals(isa)) {
            successors[0] = Message.requestFindSuccessor(id, nPrimeIsa);
            if (successors[0] == null) {
                return false;
            }
        } else {
            successors[0] = isa;
        }
        startAllThreads();
        return true;
    }

    // n′ thinks it might be our predecessor.
    // n.notify(n′) {
    // if (predecessor is nil or n′ ∈ (predecessor, n)) {
    // predecessor = n′;
    // }
    // }
    /*
     * Bascially, what's happening in this function is n' believes it is the
     * predecessor of n.
     * So it will notify n like 'hey dude, I am your predecessor!'
     * Once notified, n will check if n' is null or n' is indeed its predecessor.
     * If true then assign n' to predecessor of n.
     */
    public synchronized void notify(InetSocketAddress nPrimeIsa) {
        // System.out.println("notify: " + nPrimeIsa);
        if (nPrimeIsa == null) {
            return;
        }
        if (predecessor == null || predecessor == isa
                || Util.isInInterval(Util.getId(predecessor), id, Util.getId(nPrimeIsa))) {
            predecessor = nPrimeIsa;
        }
    }

    /**
     * Notified that the newPredecessor is our predecessor.
     * compare the newPredecessor and oldPredecessor's position, renew the
     * predecessor as the closer one.
     * 
     * @param newPredecessor
     */
    public void notified(InetSocketAddress newPredecessor) {
        if (newPredecessor == null || predecessor.equals(newPredecessor)) {
            predecessor = newPredecessor;
            return;
        }

        long oldPredecessorId = Util.getId(predecessor);
        long newPredecessorId = Util.getId(newPredecessor);

        long relativeId = Util.getRelativeId(id, oldPredecessorId); // id - oldPre
        long newPredecessorRelativeId = Util.getRelativeId(newPredecessorId, oldPredecessorId);

        if (newPredecessorRelativeId < relativeId) { // new predecessor is near to id than old pre
            predecessor = newPredecessor;
        }

    }

    // Find the successor of id
    public InetSocketAddress findSuccessor(long id) {
        if (Util.isInInterval(this.id, Util.getId(successors[0]), id) || id == Util.getId(successors[0])) {
            return this.successors[0];
        } else {
            InetSocketAddress nPrimeIsa = closestPreceding(id);
            if (nPrimeIsa == isa) {
                return isa;
            } else {
                return Message.requestFindSuccessor(id, nPrimeIsa);
            }
        }
    }


    public InetSocketAddress findPredecessor(long id) {
        // TODO

        return null;
    }

    // search the local table for the highest predecessor of id
    public InetSocketAddress closestPreceding(long id) {
        for (int i = M - 1; i >= 0; i--) {
            if (this.fingerTable[i] != null && Util.isInInterval(this.id, id, Util.getId(this.fingerTable[i]))) {
                return fingerTable[i];
            }
        }
        return this.isa;
    }

    // Update the ith entry of the finger table
    // If the newIsa is indeed the new successor, notify it.
    // TODO: WE NEED TO TAKE A CLOSER LOOK AT SYNCHRONIZATION.
    public synchronized void updateFingerTableEntry(int i, InetSocketAddress newIsa) {
        fingerTable[i] = newIsa;

        if (i == 0 && newIsa != null && !newIsa.equals(isa)) {
            notify(newIsa);
        }
    }

    private synchronized void deleteSuccessor() {
        InetSocketAddress successor = fingerTable[0];
        if (successor == null) {
            return;
        }
        int i = 31;
        for (; i >= 0; i--) {
            if (fingerTable[i] != null && fingerTable[i].equals(successor)) {
                break;
            }
        }

        for (int j = i; j >= 0; j--) {
            updateFingerTableEntry(j, null);
        }

        if (predecessor != null && predecessor.equals(successor)) {
            predecessor = null;
        }

        // TODO
    }

    private synchronized void removeNodeFromFingerTable(InetSocketAddress removedIsa) {
        for (int i = 0; i < M; i++) {
            if (fingerTable[i] != null && fingerTable[i].equals(removedIsa)) {
                fingerTable[i] = null;
            }
        }
    }

    // TODO: need to implement logics for multiple successors
    private void findSuccessors(long id) {
        // for (int i = 0; i < Chord.NUM_SUCCESSORS; i++) {
        // successors[i] = findSuccessor(id);
        // }
    }

    /**
     * Start all the threads in the node
     */
    private void startAllThreads() {
        listener.start();
        stabilization.start();
        fingerTableFixing.start();
        predecessorChecking.start();
    }

    /**
     * Terminate all the threads in the node.
     */
    public void terminate(){
        if (listener != null) listener.terminate();
        if (fingerTableFixing != null) fingerTableFixing.terminate();
        if (stabilization != null) stabilization.terminate();
        if (predecessorChecking != null) predecessorChecking.terminate();
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
        sb.append("id: " + String.valueOf(id) + "\n");
        for (int i = 0; i < M; i++) {
            sb.append("Finger " + String.valueOf(i) + ": " + String.valueOf(fingerTable[i]) + "\n");
        }
        return sb.toString();
    }

    public InetSocketAddress getIsa() {
        return isa;
    }

    public long getId() {
        return id;
    }

    public String getIp() {
        return ipAddress;
    }

    public int getPort() {
        return Integer.parseInt(portNum);
    }

    public InetSocketAddress getPredecessor() {
        return predecessor;
    }

    public InetSocketAddress getSuccessor() {
        return successors[0];
    }

    public synchronized void setPredecessor(InetSocketAddress isa) {
        predecessor = isa;
    }

    public synchronized void setIthSuccessor(int i, InetSocketAddress isa) {
        successors[i] = isa;
    }
    public void printNode(){
        System.out.println("___*___*___*___*___*___*___*___Node Information___*___*___*___*___*___*___*___");
        System.out.println("Decription\t\t\t\tIp address\t id  \t location in the chord\n");
        System.out.println("\nLocal :\t\t\t\t"+isa.toString()+"\t"+Util.getHexPosition(Util.getId(isa)));
        if (predecessor != null)
            System.out.println("\nPREDECESSOR:\t\t\t"+predecessor.toString()+"\t"
                    +Util.getHexPosition(Util.getId(predecessor)));
        else
            System.out.println("\nPREDECESSOR:\t\t\tNULL");
        System.out.println("\nFINGER TABLE:\n");
        for (int i = 0; i < M; i++) {
            long ithStartId  = Util.ithStartId(Util.getId(isa),i);
            InetSocketAddress f = fingerTable[i];
            StringBuilder sb = new StringBuilder();
            sb.append(i+"\t"+ Util.longToHex(ithStartId)+"\t\t");
            if (f!= null)
                sb.append(f.toString()+"\t"+Util.getHexPosition(Util.getId(f)));
            else
                sb.append("NULL");
            System.out.println(sb.toString());
        }
        System.out.println("___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___");
    }


}
