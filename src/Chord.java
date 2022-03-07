import Exceptions.*;
import java.net.*;
import java.util.Scanner;

public class Chord {
    // Change the settings here for convenience
    public static final int M = 4; // Set M to a smaller value for debugging, or a larger value to avoid clashes.
    public static final int NUM_SUCCESSORS = 3;
    public static final int INTERVAL_MS = 50; // interval to periodically call the functions

    public static void main(String[] args) {
        System.out.println("Welcome to the Chord P2P network!");
        try {
            String localIpAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Your local ip is : " + localIpAddress + "\n");

            System.out.println("You can choose to create a ring or to join an existing ring.");
            System.out.println("Enter the port for the new node: ");
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextInt()) {
                throw new ParseArgumentException();
            }
            String portNum = scanner.nextLine();

            System.out.println("Enter ip and port if the new node is joining an existing ring " +
                    "(hit Enter if you want to create a ring): ");
            String existingRingArgs = scanner.nextLine();
            String[] existingRingArgArr = existingRingArgs.split(" ");
            if (existingRingArgArr.length > 2) {
                throw new ParseArgumentException();
            }

            // Construct a Node instance by passing address and port number
            Node node = new Node(localIpAddress, portNum);

            // Find isa of contact node
            // If user hit enter, the contact node is the node itself. So get isa of the
            // current node
            // If user entered two parameters, calculate the isa based on the two parameters
            InetSocketAddress contactIsa = (existingRingArgs.length() == 0) ? node.getIsa()
                    : Util.getInetSocketAddress(existingRingArgArr[0], existingRingArgArr[1]);
            if (contactIsa == null) {
                throw new FindNodeIsaException("contact node");
            }

            if (node.join(contactIsa)) {
                System.out.println("Joining the Chord ring...");
                System.out.println(node);
            } else {
                throw new ConnectToNodeException("contact node");
            }

            Scanner userinput = new Scanner(System.in);
            while (true) {
                System.out.println("check info or exit? type( i or e):");
                String command = userinput.next();
                if (command.startsWith("i")) {
                    System.out.println(node);
                } else if (command.startsWith("e")) {
                    node.terminate();
                    System.out.println("This node is leaving the chord.");
                    System.exit(0);
                }
            }

        } catch (UnknownHostException e) {
            System.out.println("Fatal error while finding the local ip. Now exit.");
            System.exit(0);
        } catch (ParseArgumentException | FindNodeIsaException | ConnectToNodeException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }
}
