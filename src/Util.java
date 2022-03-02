import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class Util {

    private static long[] exponentTable = null;
    
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
        // need implementation
        return new InetSocketAddress(ipAddress, port);
    }


    // hash port + ip to 160 bit String
    // String 20 characters
    // truncated to m bits
    // peer id between 0 - (2^m - 1)
    // id =
    public static long hashIsa(InetSocketAddress isa) {
        int hc = isa.hashCode();
        return hashing(hc);
    }


    private static long hashing(int hc){
//        byte[] byteArr = new byte[];
//        for (int i = 0; i < 4;i++) {
//            byteArr[i] = (byte) (i >> (24 - 8 * i));
//        }
        return 0L;
    }

    private static String hashing2(String input) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            // not sure if need to reset here
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
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
