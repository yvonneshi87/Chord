import java.net.InetSocketAddress;

public class Stabilization extends Thread {
    final static private int NUM_SUCCESSORS = Chord.NUM_SUCCESSORS;
    private Node node;
    private boolean active;

    public Stabilization(Node node) {
        this.node = node;
        active = true;
    }

    @Override
    // Called periodically.
    // Verify this node’s immediate successor, and tell its successor about itself
    public void run() {
        while (active) {
            int count = 0;
            while (count < NUM_SUCCESSORS) {
                try {
                    // Get successor's successor list, and update the list of this node
                    InetSocketAddress[] successors = Message.requestReturnSuccessorsList(node.getSuccessor());
                    for (int i = 1; i < NUM_SUCCESSORS; i++) {
                        node.setIthSuccessor(i, successors[i - 1]);
                    }
                    break;
                } catch (Exception e) {
                    // If successor[0] is not alive, remove it from the list, and try again
                    for (int i = 0; i < NUM_SUCCESSORS - 1; i++) {
                        node.setIthSuccessor(i, node.getIthSuccessor(i+1));
                    }
                    node.setIthSuccessor(NUM_SUCCESSORS - 1, null);
                    count++;
                }
            }
            if (count == NUM_SUCCESSORS) {
                // Too many failures, nothing we can do to recover...
                return;
            }

            InetSocketAddress x; // Get the predecessor of my successor
            if (node.getSuccessor() != node.getIsa()) {
                x = Message.requestReturnPredecessor(node.getSuccessor());
            } else {
                x = node.getPredecessor();
            }
            // update successor if necessary
            if (x != null && (node.getSuccessor() == null || node.getSuccessor().equals(node.getIsa())
                    || Util.isInInterval(node.getId(), Util.getId(node.getSuccessor()), Util.getId(x)))) {
                node.setIthSuccessor(0, x);
            }
            // notify succcessor that I am its predecessor
            if (node.getSuccessor() != node.getIsa()) {
                Message.requestNotify(node.getIsa(), node.getSuccessor());
            }

            try {
                Thread.sleep(Chord.INTERVAL_MS);
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
