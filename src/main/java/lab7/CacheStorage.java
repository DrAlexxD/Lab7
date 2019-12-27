package lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    private static int leftBorder, rightBorder;
    public static final String DEALER = "tcp://localhost:2050";


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        leftBorder = in.nextInt();
        rightBorder = in.nextInt();
    }

    private void cacheInitialization() {
        Map<Integer, String> cache = new HashMap<>();
        for (int i = leftBorder; i <= rightBorder; i++) {
            cache.put(i, Integer.toString(i));
        }

        ZContext context = new ZContext();
        ZMQ.Socket dealer = context.createSocket(SocketType.DEALER);
        dealer.setHWM(0);
        dealer.connect(DEALER);
        ZMQ.Poller poller = context.createPoller(1);
        poller.register(dealer, ZMQ.Poller.POLLIN);
        
    }
}
