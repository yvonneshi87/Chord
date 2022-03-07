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


        while (active) {
            InetSocketAddress x = Message.requestReturnPredecessor(node.getSuccessor());
            if (Util.isInInterval(node.getId(), Util.getId(node.getSuccessor()), Util.getId(x))) {
                node.setIthSuccessor(0, x);
            }
            Message.requestNotify(node.getIsa(), node.getSuccessor());
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
