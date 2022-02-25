import java.net.DatagramSocket;

public class Message {

  enum MessageType {
    FIND_SUCCESSOR, RETURN_PREDECESSOR, NOTIFY, PING
  }
  
  // Ask targetNode to run findSuccessor(id), return the successor node
  public static Node requestFindSuccessor(long id, Node targetNode) {
    return null;
  }

  // Ask targetNode to return its predecessor, return the predecessor node
  public static Node requestReturnPredecessor(Node targetNode) {
    return null;
  }

  // Ask targetNode to run notify(selfNode)
  public static void requestNotify(Node selfNode, Node targetNode) {

  }

  // Ping targetNode to see if it is alive (return true: OK. false: failed)
  public static boolean requestPing(Node targetNode) {
    return true;
  }

  // Receive one incoming message, parse it, and run the corresponding method on selfNode 
  // (return 0: OK. otherwise: failed)
  public static int receiveIncomingMessage(DatagramSocket socket, Node selfNode) {
    return 0;
  }

}
