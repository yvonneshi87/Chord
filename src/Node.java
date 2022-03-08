import java.net.*;

public class Node {
    private static final int NUM_SUCCESSORS = Chord.NUM_SUCCESSORS; // Number of successors to keep in each node
    private static final int M = Chord.M; // Number of bits used

    private String portNum;
    private InetSocketAddress isa;
    private long id;
    private InetSocketAddress[] fingerTable;
    private InetSocketAddress predecessor;
    private InetSocketAddress[] successors;

    private Listener listener;
    private Stabilization stabilization;
    private FingerTableFixing fingerTableFixing;
    private PredecessorChecking predecessorChecking;

    /**
     * Constructor
     */
    public Node(String ipAddress, String portNum) {
        this.portNum = portNum;
        isa = Util.getInetSocketAddress(ipAddress, portNum);
        id = Util.hashIsaToId(isa);
        fingerTable = new InetSocketAddress[M];
        predecessor = null;
        successors = new InetSocketAddress[NUM_SUCCESSORS];
        listener = new Listener(this);
        stabilization = new Stabilization(this);
        fingerTableFixing = new FingerTableFixing(this);
        predecessorChecking = new PredecessorChecking(this);
    }

    /**
     * Join a Chord ring containing node n', meaning n' is the entry point
     * n.join(n′) {
     *      predecessor = nil;
     *      successor = n′.find successor(n);
     * }
     */
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

    /**
     * n′ thinks it might be our predecessor.
     * n.notify(n′) {
     *      if (predecessor is nil or n′ ∈ (predecessor, n)) {
     *          predecessor = n′;
     *      }
     * }
     *
     * What's happening in this function is n' believes it is the predecessor of n.
     * So n' will notify n like 'hey dude, I am your predecessor!'
     * Once notified, n will check if its current predecessor is null or n' is indeed its predecessor.
     * If true then assign n' to predecessor of n.
     */
    public synchronized void notify(InetSocketAddress nPrimeIsa) {
        if (nPrimeIsa == null) {
            return;
        }
        if (predecessor == null || predecessor.equals(isa)
                || Util.isInInterval(Util.hashIsaToId(predecessor), id, Util.hashIsaToId(nPrimeIsa))) {
            // If this is the only node in the ring, set its predecessor to null
            if (nPrimeIsa.equals(isa)) {
                predecessor = null;
            } else {
                predecessor = nPrimeIsa;
            }
        }
    }

    /**
     * Find the successor of id
     */
    public InetSocketAddress findSuccessor(long id) {
        if (successors[0] != null && (Util.isInInterval(this.id, Util.hashIsaToId(successors[0]), id)
                || id == Util.hashIsaToId(successors[0]))) {
            return this.successors[0];
        } else {
            InetSocketAddress nPrimeIsa = closestPreceding(id);
            if (nPrimeIsa.equals(isa)) {
                return isa;
            } else {
                return Message.requestFindSuccessor(id, nPrimeIsa);
            }
        }
    }

    /**
     * search the local table for the highest predecessor of id
     */
    public InetSocketAddress closestPreceding(long id) {
        int m = M - 1; // current entry to try in the finger table
        int r = NUM_SUCCESSORS - 1; // current entry to try in the successor list
        while (m >= 0 || r >= 0) {
            if (m >= 0 && !(this.fingerTable[m] != null
                    && Util.isInInterval(this.id, id, Util.hashIsaToId(this.fingerTable[m])))) {
                // This finger table entry is not before id, or it is null, try next
                m--;
                continue;
            }

            if (r >= 0 && !(this.successors[r] != null
                    && Util.isInInterval(this.id, id, Util.hashIsaToId(this.successors[r])))) {
                // This successor entry is not before id, or it is null, try next
                r--;
                continue;
            }

            boolean fingerIsBetter = true;
            if (m < 0 && r < 0) {
                // could not find a suitable entry in either, just return this node itself
                return this.isa;
            }
            if (m < 0) {
                fingerIsBetter = false;
            } else if (r < 0) {
                fingerIsBetter = true;
            } else {
                // Check who is closer to id
                if (Util.isInInterval(this.id, Util.hashIsaToId(fingerTable[m]), Util.hashIsaToId(successors[r]))) {
                    fingerIsBetter = true;
                } else {
                    fingerIsBetter = false;
                }
            }

            // If the closest is not responding, then we try to find the next closest
            if (fingerIsBetter) {
                if (Message.requestPing(fingerTable[m])) {
                    return fingerTable[m];
                } else {
                    m--;
                }
            } else {
                if (Message.requestPing(successors[r])) {
                    return successors[r];
                } else {
                    r--;
                }
            }

        }
        return this.isa;
    }

    /**
     * Update the ith entry of the finger table
     * If the newIsa is indeed the new successor, notify it.
     */
    public synchronized void updateFingerTableEntry(int i, InetSocketAddress newIsa) {
        fingerTable[i] = newIsa;

        if (i == 0 && newIsa != null && !newIsa.equals(isa)) {
            notify(newIsa);
        }
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
     * Redefine print of node's format
     * @return
     * eg. an output format:
     * ___*___*___*___*___*___*___*___NODE___INFO___*___*___*___*___*___*___*___*___*___*___
     * Decription      |       Ip_address       |      id       |      location_in_the_chord
     * Local :                 10.0.0.34:9887          14              88%
     * PREDECESSOR:            10.0.0.34:9867          13              81%
     * SUCCESSOR LIST:
     * 0       0000000f        10.0.0.34:9888          12              75%
     * 1       00000000        10.0.0.34:9888          12              75%
     * 2       00000002        10.0.0.34:9888          12              75%
     * FINGER TABLE:
     * 0       0000000f        10.0.0.34:9888          12              75%
     * 1       00000000        10.0.0.34:9888          12              75%
     * 2       00000002        10.0.0.34:9888          12              75%
     * 3       00000006        10.0.0.34:9888          12              75%
     * ___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("___*___*___*___*___*___*___*___NODE___INFO___*___*___*___*___*___*___*___*___*___*___\n");
        sb.append("Decription\t|\tIp_address\t |\tid\t |\tlocation_in_the_chord\n");
        sb.append("Local :\t\t\t" + isa.toString().split("/")[1] +"\t\t"+Util.getHexPosition(Util.hashIsaToId(isa))+"\n");
        if (predecessor != null)
            sb.append("PREDECESSOR:\t\t"+predecessor.toString().split("/")[1]+"\t\t"
                  +Util.getHexPosition(Util.hashIsaToId(predecessor)) +"\n");
        else
            sb.append("PREDECESSOR:\t\tNULL\n");

        sb.append("\nSUCCESSOR LIST:\n");
        for (int i = 0; i < NUM_SUCCESSORS; i++) {
            long ithStartId  = Util.ithStartId(Util.hashIsaToId(isa),i);
            InetSocketAddress f = successors[i];
            sb.append(i+"\t"+ Util.convertLongToHex(ithStartId)+"\t");
            if (f!= null)
                sb.append(f.toString().split("/")[1]+"\t\t"+Util.getHexPosition(Util.hashIsaToId(f)) + "\n");
            else
                sb.append("NULL\n");
        }
        sb.append("\nFINGER TABLE:\n");
        for (int i = 0; i < M; i++) {
            long ithStartId  = Util.ithStartId(Util.hashIsaToId(isa),i);
            InetSocketAddress f = fingerTable[i];
            sb.append(i+"\t"+ Util.convertLongToHex(ithStartId)+"\t");
            if (f!= null)
                sb.append(f.toString().split("/")[1]+"\t\t"+Util.getHexPosition(Util.hashIsaToId(f)) + "\n");
            else
                sb.append("NULL\n");
        }
        sb.append("___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___\n");
        return sb.toString();
    }

    /**
     * Getters and setters
     */

    public InetSocketAddress getIsa() {
        return isa;
    }

    public long getId() {
        return id;
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

    public InetSocketAddress getIthSuccessor(int i) {
        return successors[i];
    }

    public synchronized void setPredecessor(InetSocketAddress isa) {
        predecessor = isa;
    }

    public synchronized void setIthSuccessor(int i, InetSocketAddress isa) {
        successors[i] = isa;
    }

    /**
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
     */
}
