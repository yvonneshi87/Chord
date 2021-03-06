public class PredecessorChecking extends Thread {
    private Node node;
    private boolean active;

    public PredecessorChecking(Node node) {
        this.node = node;
        active = true;
    }

    /*
    * This function is called periodically to check whether the node's predecessor has failed.
    **/
    @Override
    public void run() {
        while (active) {
            if (node.getPredecessor() != null && node.getPredecessor() != node.getIsa()
                    && Message.requestPing(node.getPredecessor()) == false) {
                node.setPredecessor(null);
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
