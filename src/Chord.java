import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Chord {

    public static void main(String[] args) {
        // Get User input and check its validity
        if (args == null || args.length == 2 || args.length > 3) {
            System.out.println("Fatal error when parsing arguments! Now exit.");
            System.exit(0);
        }

        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            String portNum = args[0];

            // Construct a Node instance by passing address and port number
            Node node = new Node(ipAddress, portNum);

            // Find isa of contact node
            // If arg len = 1, the contact node is the node itself. So get isa of the current node
            // If arg len = 3, calculate the isa based on the last two parameters
            InetSocketAddress contactIsa = (args.length == 1) ? node.getIsa() : Util.getInetSocketAddress(args[1], args[2]);

            if (contactIsa == null) {
                System.out.println("Fatal error when finding isa of contact node. Now exit.");
                System.exit(0);
            } else {
                if (node.join(contactIsa)) {
                    System.out.println("Joining the Chord ring...");
                    System.out.println(node);
                } else {
                    System.out.println("Fatal error when connecting to the contact node. Now exit");
                    System.exit(0);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
