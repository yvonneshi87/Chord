import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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

    /**
     * return response for a request.
     * @param requestStr
     * @return
     */
    private String getResponseStr(String requestStr) {
        if ( requestStr== null){
            return null;
        }
        InetSocketAddress result;
        requestStr = normalize_request(requestStr);

        switch (requestStr){
            case ("FIND_MY_SUC") :{
                long id = Long.parseLong(requestStr.split(" ")[1]); // information must contain an id
                result = node.findSuccessor(id);
                return "YOUR_SUC_IS_" +
                        result.getAddress().toString() + ":" + result.getAddress();
            }
            case ("FIND_MY_CLOSET_PRE_FINGER") : {
                long id = Long.parseLong(requestStr.split(" ")[1]);
                result = node.closestPreceding(id);
                return "YOUR_CLOSET_PRE_IN_MY_FINGER_IS_" +
                        result.getAddress().toString() + ":" + result.getPort();
            }
            case ("TELL_ME_YOUR_SUC") : {
                result = node.getSuccessor();
                if(result != null){
                    String ip = result.getAddress().toString();
                    int port = result.getPort();
                    return "MY_SUCCESSOR_" + ip + ":" + port;
                }
                else{
                    return "MY_SUCCESSOR_IS_NULL";
                }
            }
            case ("TELL_ME_YOUR_PRE") : {
                result = node.getPredecessor();
                if(result != null){
                    return "MY_PRE_IS_" +
                            result.getAddress().toString() + ":" + result.getPort();
                }else{
                    return "MY_PRE_IS_NULL";
                }
            }
            case ("ARE_YOU_ALIVE") : {
                return "ALIVE";

            }
            case ("I_AM_YOUR_PRE") : {
                InetSocketAddress pre = generate_socket_address(requestStr.split(":")[1]);
                node.notified(pre); // set pre as node's predecessor.
                return "NOTIFIED";
            }

        }


        return null;
    }

    private String normalize_request(String requestStr){
        String requestType = null;
        if (requestStr.startsWith("FIND_MY_SUC")){ //ask the server to give me my successor
            return requestType = "FIND_MY_SUC";
        }
        else if (requestStr.startsWith("FIND_MY_CLOSEST_PRE_FINGER")){ // ask the server to tell me what is my closest
            // pre finger of the server
            return requestType = "CLOSEST_PRE_FINGER";
        }
        else if (requestStr.startsWith("TELL_ME_YOUR_SUC") || requestStr.startsWith("YOUR_SUCC")){
            return requestType = "TELL_ME_YOUR_SUC"; //ask the server to tell me the server's successor
        }
        else if (requestStr.startsWith("TELL_ME_YOUR_PRE")) { // ask the server to tell me what's the server's predecessor
            return requestType = "TELL_ME_YOUR_PRE";
        }
        else if (requestStr.startsWith("ARE_YOU_ALIVE")){ // ask the server to tell whether the server is alive
            return requestType = "ARE_YOU_ALIVE";
        }
        else if (requestStr.startsWith("I_AM_YOUR_PRE")){ // tell the server that I am your predecessor
            return requestType = "I_AM_YOUR_PRE";
        }
        return requestType;
    }

    /**
     * generate InetSocketAddress from a string that contains socket address information.
     * @param addressInfo
     * @return :
     * eg. input:  "my address is: 10.0.0.11:8000")
     *     output:  /10.0.0.11:8000
     */
    public static InetSocketAddress generate_socket_address(String addressInfo){
        String s = "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9][0-9][0-9][0-9]+";
        Pattern p = Pattern.compile(s);
        Matcher matcher = p.matcher(addressInfo);
        boolean found = matcher.find();
        String addressFound = null;
        if(found == false){
            System.out.println("No validated ip address information founded");
            return null;
        }else{
            addressFound = matcher.group(0);
            String[] split = addressFound.split(":");
            InetAddress ip = null;
            try{
                ip = InetAddress.getByName(split[0]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Can't generate an ip address.");
                return null;
            }
            int port = Integer.parseInt(split[1]);
            return new InetSocketAddress(ip, port);
        }
    } // end of generate_socket_address

}
