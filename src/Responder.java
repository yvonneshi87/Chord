import java.net.Socket;

public class Responder extends Thread {
    private Node node;
    Socket socket;

    public Responder(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
    }

    @Override
    public void run() {
        Message.receiveIncomingMessage(socket, node);
    }

}
