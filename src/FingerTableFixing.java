public class FingerTableFixing extends Thread {
    private Node node;
    private boolean active;
    int next;
    private static final int M = 32; // Number of bits used

    public FingerTableFixing(Node node) {
        this.node = node;
        active = true;
        next = 0;
    }

    @Override
    // called periodically. refreshes finger table entries
    public void run() {
        while (active) {
            node.updateFingerTableEntry(next, node.findSuccessor(Util.ringAddition(node.getId(), 1 << next)));
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
        }
    }

    public void terminate() {
        active = false;
    }


}
