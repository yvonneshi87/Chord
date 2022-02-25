import java.net.*;

public class Util {
    public static String[] parseArgsIfNeeded(String[] args) {
        // need implementation
        return null;
    }

    public static InetSocketAddress getInetSocketAddress(String ipAddress, String portNum) {
        if (ipAddress == null || portNum == null) {
            return null;
        }

        int port = Integer.parseInt(portNum);
        if (port < 0 || port > 65535) {
            return null;
        }
        // need implemenation
        return new InetSocketAddress(ipAddress, port);
    }


    // hash port + ip to 160 bit String
    // String 20 characters
    // truncated to m bits
    // peer id between 0 - (2^m - 1)
    // id =
    public static long hashIsa(InetSocketAddress isa) {
       // need implementation
        return 0L;
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
