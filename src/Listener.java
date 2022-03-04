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
    private void openServer(){
        int port = node.getPort();
        try {
            serverSocket = new ServerSocket(port);
            active = true;
        } catch (IOException e){
            // TODO: NEED REWRITE EXCEPTION HANDLING HERE.
            throw new RuntimeException("Can't open server port " + port, e);
        }
    }

    @Override
    // Run socket to accept
    public void run() {
        // TODO: CAN'T FIGURE OUT super.run()
        super.run();

        while(active){
            try{
                Socket communicator = serverSocket.accept();
                // TODO: CONSTRUCT A SPEAKER
            }catch (IOException e) {
                // TODO: REWRITE EXCEPTION HANDLER
                throw new RuntimeException("Can't accept message ", e);
            }
        }
    }

    public void terminate() {
        active = false;
    }
}
