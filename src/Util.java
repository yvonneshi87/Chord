import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class Util {

    private static long[] exponentTable = null;

    // Construct InetSocketAddress with ip address and port number
    public static InetSocketAddress getInetSocketAddress(String ipAddress, String portNum) {
        if (ipAddress == null || portNum == null) {
            return null;
        }
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            int port = Integer.parseInt(portNum);
            return new InetSocketAddress(ip, port);

        } catch (UnknownHostException | NumberFormatException e1) {
            System.out.println("Cannot create InetSocketAddress using IP: " + ipAddress
            + " and PORT: " + portNum);
            return null;
        }
    }

    // check if x is in between low and high on the ring
    public static boolean isInInterval(long low, long high, long x) {
        if (low > high) {
            high += Chord.TOTAL;
            if (x < low) {
                x += Chord.TOTAL;
            }
        }

        return (x > low && x < high);
    }

    // add x and y on the ring, and return the sum
    public static long ringAdd(long x, long y) {
        long result = x + y;
        if (result > Chord.TOTAL) {
            result -= Chord.TOTAL;
        }

        return result;
    }
}
