public class PredecessorChecking extends Thread {
    private Node node;
    private boolean active;

    public PredecessorChecking(Node node) {
        this.node = node;
        active  = true;
    }


    @Override
    // called periodically. checks whether predecessor has failed.
    public void run() {

        while (active) {
            if (node.getPredecessor() != null && node.getPredecessor() != node.getIsa() && Message.requestPing(node.getPredecessor()) == false) {
                node.setPredecessor(null);
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
