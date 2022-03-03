import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class Util {

    private static long[] exponentTable = null;
    
    public static String[] parseArgsIfNeeded(String[] args) {
        // need implementation
        return null;
    }

    // Construct InetSocketAddress with ip address and port number
    public static InetSocketAddress getInetSocketAddress(String ipAddress, String portNum) {
        if (ipAddress == null || portNum == null) {
            return null;
        }
        // NOT SURE IF THE CHECK HERE IS CORRECT
        int port = Integer.parseInt(portNum);
        if (port < 0 || port > 65535) {
            return null;
        }
        // need implementation
        return new InetSocketAddress(ipAddress, port);
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
