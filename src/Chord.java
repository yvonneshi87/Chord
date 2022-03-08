import Exceptions.ConnectToNodeException;
import Exceptions.FindNodeIsaException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Chord {
    public static final int M = 4; // Set M to a smaller value for debugging, or a larger value to avoid clashes.
    public static final int NUM_SUCCESSORS = 3;
    public static final int INTERVAL_MS = 100; // interval to periodically call the functions
    private static final List<Node> nodeList = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Chord P2P network!");
        printMenu();
        Scanner scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        try {
            while (option != 5) {
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

                    case 4:
                        killNode();
                        break;
                    case 5:
                        System.out.println("Kill all node and exit the Chord");
                        System.exit(0);
                    default:
                        break;
                }
                printMenu();
                option = scanner.nextInt();
            }
        } catch (ConnectToNodeException e) {
            System.out.println(e.getMessage());
            System.out.println("Now exit.");
            System.exit(0);
        }
    }

    private static void killNode() {
        if (nodeList.size() == 0) {
            System.out.println("You have not created any node. Now go back to menu.");
            return;
        }
        System.out.println("Nodes you have created: ");
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            System.out.println("Node " + i +  " - isa: " + node.getIsa());
        }
        System.out.println("Please enter the index of the node you want to kill: ");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        try{
            System.out.println(nodeList.get(index));
        }catch (Exception e){
            System.out.println("Can't catch the node with the index you entered");
        }
        nodeList.get(index).killAllThreads();
        nodeList.remove(index);

    }

    private static void createOrJoinRing() throws ConnectToNodeException {
        try {
            String localIpAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Your local ip is " + localIpAddress);
            System.out.println("Enter the port for the new node: ");
            Scanner scanner = new Scanner(System.in);
            String portNum = scanner.nextLine();

            System.out.println("Enter ip and port (FORMAT: 127.0.0.1:8000 )for the entry point or hit enter to skip)");
            String existingRingArgs = scanner.nextLine();

            // Construct a Node instance by passing address and port number
            Node node = new Node(localIpAddress, portNum);

            // Find isa of contact node
            // If user hits enter, the contact node is the node itself. So get isa of the
            // current node
            // If user enters two parameters, calculate the isa based on the two parameters
            InetSocketAddress contactIsa = (existingRingArgs.length() == 0) ? node.getIsa()
                    : Util.getInetSocketAddress(existingRingArgs);
            if (contactIsa == null) {
                throw new FindNodeIsaException(localIpAddress + " and " + portNum);
            }

            if (node.join(contactIsa)) {
                System.out.println("Joining the Chord ring...");
                System.out.println(node);
                nodeList.add(node);
                System.out.println("\n");
            } else {
                throw new ConnectToNodeException(localIpAddress + " and " + portNum);
            }
        } catch (UnknownHostException | FindNodeIsaException e) {
            System.out.println(e.getMessage());
            System.out.println("Now going back to menu...");
        } catch(ConnectToNodeException e) {
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
        try{
            System.out.println(nodeList.get(index));
        }catch (Exception e){
            System.out.println("Can't catch the node with the index you entered");
        }

    }

    private static void query() {
        System.out.println("Please enter the key you want to search: ");
        Scanner scanner = new Scanner(System.in);
        String key = scanner.nextLine();
        Long id = Util.hashing(key);

        System.out.println("Please enter ip and port for the node you want to query (FORMAT: 127.0.0.1:8000):");
        String userInput = scanner.nextLine();

        InetSocketAddress isa = Util.getInetSocketAddress(userInput);
        try {
            if (isa == null) {
                throw new FindNodeIsaException(userInput);
            }
            if (!Message.requestPing(isa)) {
                throw new ConnectToNodeException(userInput);
            }

            System.out.println("\nConnecting to the node in " + userInput);
            System.out.println("ID and Position: " + Util.getHexPosition(Util.hashIsaToId(isa)) + "\n");

            System.out.println("Start finding the key:" + key);
            System.out.println("ID and position: " + Util.getHexPosition(id) + "\n");

            InetSocketAddress nodeIsa = Message.requestFindSuccessor(id, isa);
            if (nodeIsa == null) {
                throw new ConnectToNodeException(userInput);
            }
            System.out.println("Node is found! ");
            System.out.println("Ip address and port: " + nodeIsa.getAddress().toString() + ":" + nodeIsa.getPort());
            System.out.println("ID and Position: " + Util.getHexPosition(Util.hashIsaToId(nodeIsa)));
            System.out.println("\n");
        } catch (FindNodeIsaException | ConnectToNodeException e){
            System.out.println(e.getMessage());
            System.out.println("Now going back to menu...");
        }
    }

    private static void printMenu() {
        System.out.println("Please choose one of the following options:");
        System.out.println("1 - Create/Join a Chord Ring");
        System.out.println("2 - Check information");
        System.out.println("3 - Query");
        System.out.println("4 - Kill");
        System.out.println("5 - Exit");
    }

}
