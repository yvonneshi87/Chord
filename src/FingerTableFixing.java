import java.net.InetSocketAddress;

public class FingerTableFixing extends Thread {
    private Node node;
    private boolean active;
    int next;
    private static final int M = 4; // Number of bits used

    public FingerTableFixing(Node node) {
        this.node = node;
        active = true;
        next = 0;
    }

    @Override
    // called periodically. refreshes finger table entries
    public void run() {
        while (active) {
            InetSocketAddress nextIsa = node.findSuccessor(Util.ringAddition(node.getId(), 1 << next));
            node.updateFingerTableEntry(next, nextIsa);
            
            next++;
            if (next >= M) {
                next = 0;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println(node);
        }
    }

    public void terminate() {
        active = false;
    }


}
