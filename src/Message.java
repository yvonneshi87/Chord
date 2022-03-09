import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Message {
  final static private int NUM_SUCCESSORS = Chord.NUM_SUCCESSORS;
  enum MessageType {
    FIND_SUCCESSOR, RETURN_PREDECESSOR, NOTIFY, PING, RETURN_SUCCESSOR_LIST
  }

  // Ask targetNode to run findSuccessor(id), return the successor's isa
  public static InetSocketAddress requestFindSuccessor(long id, InetSocketAddress targetNodeIsa) {
    InetAddress address = targetNodeIsa.getAddress();
    int port = targetNodeIsa.getPort();
    try {
      Socket socket = new Socket(address, port);
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
      return null;
    }
  }

  // Ask targetNode to return its predecessor, return the predecessor node
  public static InetSocketAddress requestReturnPredecessor(InetSocketAddress targetNodeIsa) {
    String ip = targetNodeIsa.getHostName();
    int port = targetNodeIsa.getPort();

    try {
      Socket socket = new Socket(ip, port);
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(MessageType.RETURN_PREDECESSOR);

      String retIp = in.readLine();
      String retPort = in.readLine();

      in.close();
      out.close();
      socket.close();

      if (retIp.equals("null")) {
        return null;
      } else {
        return new InetSocketAddress(retIp, Integer.parseInt(retPort));
      }

    } catch (IOException e) {
      return null;
    }
  }

  // Ask targetNode to run notify(selfNode)
  public static void requestNotify(InetSocketAddress selfNodeIsa, InetSocketAddress targetNodeIsa) {
    String ip = targetNodeIsa.getHostName();
    int port = targetNodeIsa.getPort();
    String selfIp = selfNodeIsa.getHostName();
    int selfPort = selfNodeIsa.getPort();

    try {
      Socket socket = new Socket(ip, port);
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(MessageType.NOTIFY);
      out.println(selfIp);
      out.println(selfPort);

      in.close();
      out.close();
      socket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Ping targetNode to see if it is alive (return true: OK. false: failed)
  public static boolean requestPing(InetSocketAddress targetNodeIsa) {
    String ip = targetNodeIsa.getHostName();
    int port = targetNodeIsa.getPort();
    try {
      Socket socket = new Socket(ip, port);
      // wait for 3 seconds
      socket.setSoTimeout(3000);
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(MessageType.PING);

      String reply = in.readLine();

      in.close();
      out.close();
      socket.close();

      return reply.equals("OK");
    } catch (IOException e) {
      return false;
    }
  }

  public static InetSocketAddress[] requestReturnSuccessorsList(InetSocketAddress targetNodeIsa) throws IOException {
    String ip = targetNodeIsa.getHostName();
    int port = targetNodeIsa.getPort();

    InetSocketAddress[] successors = new InetSocketAddress[NUM_SUCCESSORS];

      Socket socket = new Socket(ip, port);
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      out.println(MessageType.RETURN_SUCCESSOR_LIST);

      int count = 0;
      while (count < NUM_SUCCESSORS) {
        String retIp = in.readLine();
        String retPort = in.readLine();
        if (retIp.equals("null")) {
          successors[count] = null;
        } else {
          InetSocketAddress newIsa = new InetSocketAddress(retIp, Integer.parseInt(retPort));
          successors[count] = newIsa;
        }
        count++;
      }
      in.close();
      out.close();
      socket.close();
      return successors;
  }

  // Receive one incoming message, parse it, and run the corresponding method on
  // selfNode
  // (return true: OK. false: failed)
  public static boolean receiveIncomingMessage(Socket socket, Node selfNode) {
    try {
      PrintStream out = new PrintStream(socket.getOutputStream());
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String requestType = in.readLine();
      MessageType messageType = MessageType.valueOf(requestType);

      if (messageType == MessageType.FIND_SUCCESSOR) {
        // Requested to run find_successor(id) on selfNode
        long id = Long.parseLong(in.readLine());
        InetSocketAddress retNodeIsa = selfNode.findSuccessor(id);
        String retIp = retNodeIsa.getHostName();
        int port = retNodeIsa.getPort();

        out.println(retIp);
        out.println(port);
        in.close();
        out.close();
        socket.close();
        return true;
      } else if (messageType == MessageType.RETURN_PREDECESSOR) {
        if (selfNode.getPredecessor() != null) {
          InetSocketAddress retNodeIsa = selfNode.getPredecessor();
          String retIp = retNodeIsa.getHostName();
          int port = retNodeIsa.getPort();

          out.println(retIp);
          out.println(port);
        } else {
          out.println("null");
          out.println("null");
        }

        in.close();
        out.close();
        socket.close();
        return true;
      } else if (messageType == MessageType.NOTIFY) {
        String nPrimeIp = in.readLine();
        int nPrimePort = Integer.parseInt(in.readLine());

        InetSocketAddress nPrimeIsa = new InetSocketAddress(nPrimeIp, nPrimePort);
        selfNode.notify(nPrimeIsa);

        in.close();
        out.close();
        socket.close();
        return true;
      } else if (messageType == MessageType.PING) {
        out.println("OK");
        in.close();
        out.close();
        socket.close();
        return true;
      } else if (messageType == MessageType.RETURN_SUCCESSOR_LIST) {
        int count = 0;
        while (count < NUM_SUCCESSORS) {
          if (selfNode.getIthSuccessor(count) == null) {
            out.println("null");
            out.println("null");
          } else {
            InetSocketAddress retNodeIsa = selfNode.getIthSuccessor(count);
            String retIp = retNodeIsa.getHostName();
            int port = retNodeIsa.getPort();

            out.println(retIp);
            out.println(port);
          }
          count++;
        }
        
        in.close();
        out.close();
        socket.close();
        return true; 
      } else {
        in.close();
        out.close();
        socket.close();
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

}
