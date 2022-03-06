import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class Util {
    private static final int M = 32;
    private static final int TOTAL = 1 << M; // Maximum number of nodes
    private static long TWO_TO_M;

    private Util() {
        long tmp = 1L;
        for (int i = 0; i < M; i++) {
            tmp *= 2;
        }
        TWO_TO_M = tmp;
    }

    // Construct InetSocketAddress with ip address and port number
    public static InetSocketAddress getInetSocketAddress(String ipAddress, String portNum) {
        if (ipAddress == null || portNum == null) {
            return null;
        }
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);
            int port = Integer.parseInt(portNum);
            return new InetSocketAddress(ip, port);

        } catch (UnknownHostException | NumberFormatException e) {
            System.out.println("Cannot create InetSocketAddress using IP: " + ipAddress
            + " and PORT: " + portNum);
            return null;
        }
    }

    // This method hashes (ip address + port number) to 160 bit String
    // hashText is 160 bits long (= 40 hex digits * 4 bit per hex digit)
    // truncates hashText to 32 bits
    // gets peer id between 0 and (2^m - 1) by converting truncatedHashText to a long number
    public static long getId(InetSocketAddress isa) {
        String ipAddress = isa.getHostString();
        String portNum = String.valueOf(isa.getPort());
        String input = ipAddress + ":" + portNum;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            String truncatedHashText = hashText.substring(0, M / 4);
            return Long.parseLong(truncatedHashText, 16);
        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    // check if x is in between low and high on the ring
    public static boolean isInInterval(long low, long high, long x) {
        if (low > high) {
            high += TOTAL;
            if (x < low) {
                x += TOTAL;
            }
        }
        return (x > low && x < high);
    }

    // Add x and y on the ring, and return the sum
    public static long ringAddition(long x, long y) {
        long result = x + y;
        if (result > TOTAL) {
            result -= TOTAL;
        }
        return result;
    }

    public static long getRelativeId(long universalId, long localId) {
        long relativeId = universalId - localId;
        return (relativeId >= 0) ? relativeId : (relativeId + TWO_TO_M);
    }
}
