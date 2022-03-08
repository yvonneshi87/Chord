import Exceptions.ConnectToNodeException;
import Exceptions.FindNodeIsaException;
import Exceptions.ParseArgumentException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Chord2 {
    // Change the settings here for convenience
    public static final int M = 4; // Set M to a smaller value for debugging, or a larger value to avoid clashes.
    public static final int NUM_SUCCESSORS = 3;
    public static final int INTERVAL_MS = 100; // interval to periodically call the functions

    private static List<Node> nodeList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Chord P2P network!");
        printMenu();
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        try {
            while (option != 4) {
                System.out.println("\n");
                switch (option) {
                    case 1:
                        createOrJoinRing();
                        break;

                    case 2:
                        checkInformation();
                        break;

                    case 3:
                        query();
                        break;

                    default:
                        break;
                }
                printMenu();
                option = scanner.nextInt();
            }
        } catch (UnknownHostException e) {
            System.out.println("Fatal error while finding the local ip. Now exit.");
            System.exit(0);
        } catch (ParseArgumentException | FindNodeIsaException | ConnectToNodeException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }

    private static void createOrJoinRing() throws UnknownHostException, ConnectToNodeException,
            ParseArgumentException, FindNodeIsaException {
        try {
            String localIpAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Your local ip is " + localIpAddress);
            System.out.println("Enter the port for the new node: ");
            Scanner scanner = new Scanner(System.in);
            if (!scanner.hasNextInt()) {
                throw new ParseArgumentException();
            }
            String portNum = scanner.nextLine();

            System.out.println("If you want your new node to join an existing ring, " +
                    "enter ip and port for the entry point (hit enter to skip): ");
            String existingRingArgs = scanner.nextLine();
            String[] existingRingArgArr = existingRingArgs.split(" ");
            if (existingRingArgArr.length > 2) {
                throw new ParseArgumentException();
            }

            // Construct a Node instance by passing address and port number
            Node node = new Node(localIpAddress, portNum);

            // Find isa of contact node
            // If user hits enter, the contact node is the node itself. So get isa of the
            // current node
            // If user enters two parameters, calculate the isa based on the two parameters
            InetSocketAddress contactIsa = (existingRingArgs.length() == 0) ? node.getIsa()
                    : Util.getInetSocketAddress(existingRingArgArr[0], existingRingArgArr[1]);
            if (contactIsa == null) {
                throw new FindNodeIsaException("contact node");
            }

            if (node.join(contactIsa)) {
                System.out.println("Joining the Chord ring...");
                System.out.println(node);
                nodeList.add(node);
                System.out.println("\n");
            } else {
                throw new ConnectToNodeException("contact node");
            }
        } catch (UnknownHostException | ParseArgumentException | FindNodeIsaException | ConnectToNodeException e) {
            throw e;
        }
    }

    private static void checkInformation() {
        if (nodeList.size() == 0) {
            System.out.println("You have not created any node. Now go back to menu.");
            return;
        }
        System.out.println("Nodes you have created: ");
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            System.out.println("Node " + i +  " - isa: " + node.getIsa());
        }
        System.out.println("Please enter the index of the node you want to look up: ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        System.out.println(nodeList.get(index));
    }

    private static void query() {

    }

    private static void printMenu() {
        System.out.println("Please choose one of the following options:");
        System.out.println("1 - Create/Join a Chord Ring");
        System.out.println("2 - Check information");
        System.out.println("3 - Query");
        System.out.println("4 - Exit");
    }

}
