import java.net.InetSocketAddress;

public class Stabilization extends Thread {
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
//        Node x = Message.requestReturnPredecessor(this.successors[0]);
//        if (Util.isInInterval(this.id, this.successors[0].id, x.id)) {
//            successors[0] = x;
//        }
//        Message.requestNotify(isa, this.successors[0]);

        while (active) {
            // TODO:
        }

    }
}
