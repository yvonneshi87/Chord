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
}
