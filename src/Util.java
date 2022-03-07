import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
    private static final int M = Chord.M;
    private static final long TWO_TO_M = 1L << M; // Maximum number of nodes

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

    /**
     * This method hashes (ip address + port number) to 160 bit String,
     * hashText is 160 bits long (= 40 hex digits * 4 bit per hex digit)
     * truncates hashText to M bits
     * return peer id between 0 and (2^M - 1) by converting truncatedHashText to a
     * long number
     * 
     * @param isa
     * @return
     *         eg. InetSocketAddress id = InetSocketAddress("10.0.0.1", 9000)
     *         input: id
     *         output: 1297906143
     */
    public static long getId(InetSocketAddress isa) {
        String ipAddress = isa.getHostName();
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
    } // end of getId

    /**
     * computer an id's position in 2**M numbers
     * 
     * @return
     *         eg: input: 1297906143
     *         output : 1297906143 30%
     */
    public static String getHexPosition(long id) {
        return Long.toString(id) + "\t\t" + String.format("%.0f%%", (float) id / Math.pow(2, M) * 100);
    }

    // check if x is in between low and high on the ring
    public static boolean isInInterval(long low, long high, long x) {
        if (low > high) {
            high += TWO_TO_M;
            if (x < low) {
                x += TWO_TO_M;
            }
        }
        return (x > low && x < high);
    }

    // Add x and y on the ring, and return the sum
    public static long ringAddition(long x, long y) {
        long result = x + y;
        if (result >= TWO_TO_M) {
            result -= TWO_TO_M;
        }
        return result;
    }

    public static long getRelativeId(long universalId, long localId) {
        long relativeId = universalId - localId;
        return (relativeId >= 0) ? relativeId : (relativeId + TWO_TO_M);
    }

    /**
     * generate InetSocketAddress from a string that contains socket address
     * information.
     * 
     * @param addressInfo
     * @return :
     *         eg. input: "my address is: 10.0.0.11:8000")
     *         output: /10.0.0.11:8000
     */
    public static InetSocketAddress generate_socket_address(String addressInfo) {
        String s = "[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9][0-9][0-9][0-9]+";
        Pattern p = Pattern.compile(s);
        Matcher matcher = p.matcher(addressInfo);
        boolean found = matcher.find();
        String addressFound = null;
        if (found == false) {
            System.out.println("No validated ip address information founded");
            return null;
        } else {
            addressFound = matcher.group(0);
            String[] split = addressFound.split(":");
            InetAddress ip = null;
            try {
                ip = InetAddress.getByName(split[0]);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("Can't generate an ip address.");
                return null;
            }
            int port = Integer.parseInt(split[1]);
            return new InetSocketAddress(ip, port);
        }
    } // end of generate_socket_address

    /***
     * calculate (id + 2 ^ i） % 2 ^ M
     * 
     * @param id
     * @param i
     * @return
     *         eg. input: 1297906143, 3
     *         output : 1297906151
     */
    public static long ithStartId(long id, int i) {
        return (long) ((id + Math.pow(2, i)) % Math.pow(2, M));
    }

    /**
     * @param
     * @return
     *         eg. input: 1297906151
     *         output: "4d5c79e7"
     */
    public static String longToHex(long l) {
        String hex = Long.toHexString(l);
        int left = 8 - hex.length();
        StringBuilder builder = new StringBuilder();
        for (int i = left; i > 0; i--) {
            builder.append("0");
        }
        builder.append(hex);
        return builder.toString();
    }

}
