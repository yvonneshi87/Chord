import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener extends Thread {
    private Node node;
    private ServerSocket serverSocket;
    private boolean active;

    public Listener(Node node) {
        this.node = node;
        openServer();
    }

    // Open server socket
    private void openServer() {
        int port = node.getPort();
        try {
            serverSocket = new ServerSocket(port);
            active = true;
        } catch (IOException e) {
            // TODO: NEED REWRITE EXCEPTION HANDLING HERE.
            throw new RuntimeException("Can't open server port " + port, e);
        }
    }

    private void stopServer(){
        if(serverSocket != null){
            try{
                serverSocket.close();
            }catch (Exception e){
              System.out.println("No active server");
            }
        }

    }

    @Override
    // Run socket to accept
    public void run() {
        while (active) {
            try {
                Socket communicateSocket = serverSocket.accept();
                // TODO: CHECK SCRIPTS
                Responder talker = new Responder(node, communicateSocket);
                // difference between start and run:
                // https://www.geeksforgeeks.org/difference-between-thread-start-and-thread-run-in-java/
                talker.start();
            } catch (IOException e) {
                // TODO: REWRITE EXCEPTION HANDLER
                //throw new RuntimeException("Can't accept message ", e);
                //System.out.println("Server die.");
            }
        }
    }

    public void terminate() {
        active = false;
        stopServer();
        System.out.println("Server terminated");
    }
}
