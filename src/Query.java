import java.net.InetSocketAddress;
import java.util.Scanner;

public class Query {
    private  static InetSocketAddress isa;
    private static Util util;
    public static void main(String[] args){
        util = new Util();
        Scanner userinput = new Scanner(System.in);
        while (true) {
            System.out.println("Querry or Exit? (q or e)");
            String cmd = userinput.next();
            if(cmd.startsWith("e")){
                System.exit(0);
            }
            System.out.println("Type in the key you want to find(any format will be fine), eg: test.txt");
            String keyToFind = userinput.next();
            Long keyId = Util.getIdKey(keyToFind);
            String postion = Util.getHexPosition(keyId);

            System.out.println("Type in an active node's ip address for you want to query from: eg. 10.0.0.1:8080. \n");
            String command = userinput.next();
            isa = Util.generate_socket_address(command);
            if (isa == null) {
                continue;
            }

            Boolean isAlive = Message.requestPing(isa);
            if (isAlive == false) {
                System.out.println("Can't connect to the node you entered.");
                continue;
            }

            System.out.println("___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___\n" +
                    "Connecting to the node in ip address: " + isa.toString().split("/")[1] + "\tID and Position: "
                    + Util.getHexPosition(Util.getId(isa)));

            System.out.println(
                    "Start to find the key:                " + keyToFind + "\tID and estimate position: "
                    +Util.getHexPosition(keyId) + "\n");
            InetSocketAddress res = Message.requestFindSuccessor(keyId, isa);
            if (res == null) {
                System.out.println("Cannot connect to the target node, query failed.\n");
                continue;
            }
            System.out.println("Found: Node: " + res.getAddress().toString() + "\t\tPosition: " +
                    Util.getHexPosition(Util.getId(res)) +"\n");
            System.out.println("___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___*___\n\n\n");
        }
        }

}
