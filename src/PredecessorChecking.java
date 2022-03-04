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

        //    public void checkPredecessor() {
//        if (Message.requestPing(this.predecessor) == false) {
//            this.predecessor = null;
//        }
//    }
        
        while (active) {
            // TODO:
        }
    }


}
