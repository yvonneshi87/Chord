public class FingerTableFixing extends Thread {
    private Node node;
    private boolean active;

    public FingerTableFixing(Node node) {
        this.node = node;
        active = true;
    }

    @Override
    // called periodically. refreshes finger table entries
    public void run() {
        while (active) {
            // TODO:

//            public void fixFingers() {
//                fingerTable[next] = findSuccessor(Util.ringAddition(id, 1 << next));
//                next++;
//                if (next >= M) {
//                    next = 0;
//                }
//            }
        }
    }


}
