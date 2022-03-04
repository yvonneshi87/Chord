import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Message {

  enum MessageType {
    FIND_SUCCESSOR, RETURN_PREDECESSOR, NOTIFY, PING
  }

  // Ask targetNode to run findSuccessor(id), return the successor node
  public static InetSocketAddress requestFindSuccessor(long id, InetSocketAddress targetNodeIsa) {
    String ip = targetNodeIsa.getHostString();
    int port = targetNodeIsa.getPort();
    try {
      Socket socket = new Socket(ip, port);
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(MessageType.FIND_SUCCESSOR);
      out.println(id);

      String retIp = in.readLine();
      String retPort = in.readLine();

      in.close();
      out.close();
      socket.close();

      return new InetSocketAddress(retIp, Integer.parseInt(retPort));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      return null;
    }
  }

  // Ask targetNode to return its predecessor, return the predecessor node
  public static Node requestReturnPredecessor(InetSocketAddress targetNodeIsa) {
    return null;
  }

  // Ask targetNode to run notify(selfNode)
  public static void requestNotify(InetSocketAddress selfNode, InetSocketAddress targetNode) {

  }

  // Ping targetNode to see if it is alive (return true: OK. false: failed)
  public static boolean requestPing(Node targetNode) {
    return true;
  }

  // Receive one incoming message, parse it, and run the corresponding method on
  // selfNode
  // (return true: OK. false: failed)
  public static boolean receiveIncomingMessage(Socket socket, InetSocketAddress selfNodeIsa) {
    try {
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String requestType = in.readLine();
      MessageType messageType = MessageType.valueOf(requestType);

      if (messageType == MessageType.FIND_SUCCESSOR) {
        // Requested to run find_successor(id) on selfNode
        long id = Long.valueOf(in.readLine());
        Node retNode = selfNode.findSuccessor(id);
        String retIp = retNode.getIsa().getHostString();
        int port = retNode.getIsa().getPort();

        out.println(retIp);
        out.println(String.valueOf(port));
        in.close();
        out.close();
        socket.close();
        return true;
      } else if (messageType == MessageType.RETURN_PREDECESSOR) {
        // TODO
      } else if (messageType == MessageType.NOTIFY) {
        // TODO
      } else if (messageType == MessageType.PING) {
        // TODO
      } else {
        in.close();
        out.close();
        socket.close();
        return false;
      }

    
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return true;
  }

}
