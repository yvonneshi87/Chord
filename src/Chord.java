import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Chord {
    public static final int NUM_SUCCESSORS = 3; // Number of successors to keep in each node
    public static final int M = 32; // Number of bits used
    public static final int TOTAL = 1 << M; // Maximum number of nodes

    public static void main(String[] args) {
        String[] parsedArgs = Util.parseArgsIfNeeded(args);
        if (parsedArgs == null || parsedArgs.length == 2 || parsedArgs.length > 3) {
            System.out.println("Fatal error when parsing arguments!");
            System.exit(0);
        }

        try {
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            InetSocketAddress isa = Util.getInetSocketAddress(ipAddress, args[0]);
            Node node = new Node(isa);
            InetSocketAddress contactIsa = (args.length == 1) ? node.getIsa()
                    : Util.getInetSocketAddress(args[1], args[2]);
            if (contactIsa == null) {
                System.out.println("Having difficulty finding the contact's isa.");
                System.exit(0);
            } else {
                if (node.join(contactIsa)) {
                    System.out.println("Joining the Chord");
                    // need implementation
                } else {
                    System.out.println("Having difficulty connecting to the contact node.");
                    System.exit(0);
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
