import Exceptions.ConnectToNodeException;
import Exceptions.FindNodeIsaException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Chord {
    public static final int M = 8; // Set M to a smaller value for debugging, or a larger value to avoid clashes.
    public static final int NUM_SUCCESSORS = 3;
    public static final int INTERVAL_MS = 100; // interval to periodically call the functions
    public static final int NUM_SIMULATION = 500; // Number of runs to simulate
    private static final List<Node> nodeList = new ArrayList<>();
    private static final List<String> status = new ArrayList<>();
    private static final HashSet<String> idSet = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Welcome to the Chord P2P network!");
        printMenu();
        Scanner scanner = new Scanner(System.in);

        int option = scanner.nextInt();

        try {
            while (option != 6) {
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
                        stopNode();
                        break;

                    case 5:
                        simulateQueryTime();
                        break;

                    default:
                        break;
                }
                printMenu();
                option = scanner.nextInt();
            }
            System.exit(0);
        } catch (ConnectToNodeException e) {
            System.out.println(e.getMessage());
            System.out.println("Now exit.");
            System.exit(0);
        }
    }

    private static void printMenu() {
        System.out.println("Please choose one of the following options:");
        System.out.println("1 - Create/Join a Chord Ring");
        System.out.println("2 - Check information");
        System.out.println("3 - Query");
        System.out.println("4 - Stop a node");
        System.out.println("5 - Run query simulation");
        System.out.println("6 - Exit");
    }

    private static void createOrJoinRing() throws ConnectToNodeException {
        try {
            String localIpAddress = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Your local ip is " + localIpAddress);
            System.out.println("Enter the port for the new node: ");
            Scanner scanner = new Scanner(System.in);
            String portNum = scanner.nextLine();

            // Check if the id clashes with existing nodes
            InetSocketAddress isa = Util.getInetSocketAddress(localIpAddress, portNum);
            long id = Util.hashIsaToId(isa);
            if (idSet.contains(id)) {
                System.out.println("The hashed id of the node you want to create already exists. " +
                        "Please try a different port.\n");
                return;
            }

            System.out.println("Enter ip and port (FORMAT: 127.0.0.1:8000) for the entry point or hit enter to skip:");
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
                status.add("ALIVE");
                System.out.println("\n");
            } else {
                throw new ConnectToNodeException(localIpAddress + " and " + portNum);
            }
        } catch (UnknownHostException | FindNodeIsaException e) {
            System.out.println(e.getMessage());
            System.out.println("Now going back to menu...");
        } catch (ConnectToNodeException e) {
            throw e;
        }
    }

    private static void checkInformation() {
        displayCreatedNode("check");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        System.out.println(nodeList.get(index));
    }

    private static void query() {
        System.out.println("Please enter the key you want to search for: ");
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
            long tmpId = Util.hashIsaToId(isa);
            System.out.println("ID: " + tmpId + "\tposition:" + Util.getHexPosition(tmpId) + "\n");

            System.out.println("Start finding the key:" + key);
            System.out.println("ID: " + id + "\tposition:" + Util.getHexPosition(id) + "\n");

            InetSocketAddress nodeIsa = Message.requestFindSuccessor(id, isa);
            if (nodeIsa == null) {
                throw new ConnectToNodeException(userInput);
            }
            System.out.println("Node is found! ");
            System.out.println("Ip address and port: " + Util.convertIsaToAddress(nodeIsa));
            long resId = Util.hashIsaToId(nodeIsa);
            System.out.println("ID: " + resId + "\tposition:" + Util.getHexPosition(resId) + "\n");
            System.out.println("\n");
        } catch (FindNodeIsaException | ConnectToNodeException e) {
            System.out.println(e.getMessage());
            System.out.println("Now going back to menu...");
        }
    }

    private static void stopNode() {
        displayCreatedNode("stop");
        Scanner scanner = new Scanner(System.in);
        int index = scanner.nextInt();
        nodeList.get(index).terminateAllThreads();
        status.set(index, "STOPPED");
    }

    private static void simulateQueryTime() {
        Random rand = new Random();
        if (nodeList.size() == 0) {
            System.out.println("You have not created any node. Now going back to menu.");
            return;
        }
        System.out.println("Simulating random queries for " + NUM_SIMULATION + " times.");
        long total_time = 0;
        int count = 0;
        while (count < NUM_SIMULATION) {
            int rand_node = rand.nextInt(nodeList.size());
            String rand_key = rand.ints('a', 'z').limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
            long rand_id = Util.hashing(rand_key);

            long start = System.nanoTime();
            Message.requestFindSuccessor(rand_id, nodeList.get(rand_node).getIsa());
            long end = System.nanoTime();

            total_time += end - start;
            count++;
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Fatal error when simulating query time. Now back to to menu.");
                return;
            }
        }
        double avg_time = (double) total_time / NUM_SIMULATION;
        System.out.println("Simulation completed.");
        System.out.println("Average query time is " + avg_time + " nano seconds");
        System.out.println("\n");        
    }

    private static void displayCreatedNode(String action) {
        if (nodeList.size() == 0) {
            System.out.println("You have not created any node. Now going back to menu.");
            return;
        }
        System.out.println("Nodes you have created: ");
        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            System.out.println("Node " + i + " - isa: " + Util.convertIsaToAddress(node.getIsa()) + " " + status.get(i));
        }
        System.out.println("Please enter the index of the node you want to " + action + ": ");
    }

}
