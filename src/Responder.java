import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Responder extends Thread {
    private Node node;
    Socket socket;

    public Responder(Node node, Socket socket) {
        this.node = node;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            String requestStr = null;
            if (inputStream != null) {
               BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
               requestStr = br.readLine();
            }
            String responseStr = (requestStr == null) ? null : getResponseStr(requestStr);

            OutputStream outputStream = socket.getOutputStream();
            if (responseStr != null) {
                outputStream.write(responseStr.getBytes());
            }

            // TODO: DO WE NEED TO CLOSE OUTPUTSTREAM? WHAT SHOULD WE DO AFTERWARDS?
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponseStr(String requestStr) {
        return null;
    }

}
