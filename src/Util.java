import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final int M = Chord.M;
    private static final long TWO_TO_M = 1L << M; // Maximum number of nodes
    private static final String ADDRESS_REGEX = "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9][0-9][0-9][0-9]+";
    private static final Pattern ADDRESS_PATTERN = Pattern.compile(ADDRESS_REGEX);

    /**
     * Construct InetSocketAddress with ip address and port number
     */
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

    /**
     * Method overloading
     */
    public static InetSocketAddress getInetSocketAddress(String fullAddress) {
        Matcher matcher = ADDRESS_PATTERN.matcher(fullAddress);
        if (matcher.find()) {
            String[] spiltStr = fullAddress.split(":");
            return getInetSocketAddress(spiltStr[0], spiltStr[1]);
        } else {
            return null;
        }
    }

    /**
     * This method hashes (ip address + port number) to id
     */
    public static long hashIsaToId(InetSocketAddress isa) {
        String ipAddress = isa.getHostName();
        String portNum = "" + isa.getPort();
        String input = ipAddress + ":" + portNum;
        return hashing(input);
    }

    /**
     * This method hashes string key to 160 bit String,
     * hashText is 160 bits long (= 40 hex digits * 4 bit per hex digit)
     * truncates hashText to M bits
     * return peer id between 0 and (2^M - 1) by converting truncatedHashText to a
     * long number
     */
    public static long hashing(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(key.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);
            String truncatedHashText = hashText.substring(0, M / 4);
            return Long.parseLong(truncatedHashText, 16);
        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    /**
     * computer an id's position in 2**M numbers
     */
    public static String getHexPosition(long id) {
        return String.format("%.0f%%", (float) id / TWO_TO_M * 100);
    }

    /**
     * check if x is in between low and high on the ring
     */
    public static boolean isInInterval(long low, long high, long x) {
        if (low > high) {
            high += TWO_TO_M;
            if (x < low) {
                x += TWO_TO_M;
            }
        }
        return (x > low && x < high);
    }

    /**
     *  Add x and y on the ring, and return the sum
     */
    public static long ringAddition(long x, long y) {
        long result = x + y;
        return (result >= TWO_TO_M) ? (result - TWO_TO_M) : result;
    }

    /***
     * calculate (id + 2 ^ i） % 2 ^ M
     */
    public static long ithStartId(long id, int i) {
        long twoToI = 1L << i;
        long sum = twoToI + id;
        return sum % TWO_TO_M;
    }

    public static String convertIsaToAddress(InetSocketAddress isa) {
        return isa.toString().split("/")[1];
    }
}
