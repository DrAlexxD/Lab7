package lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    private static int leftBorder, rightBorder;
    public static final String DEALER_SOCKET = "tcp://localhost:2050";
    public static final int TIMEOUT = 5000;
    public static final String HEARTBEAT = "Heartbleed";
    public static final String SPACE_DELIMITER = " ";

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
        dealer.connect(DEALER_SOCKET);
        ZMQ.Poller poller = context.createPoller(1);
        poller.register(dealer, ZMQ.Poller.POLLIN);
        long startTime = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                ZMsg m = new ZMsg();
                m.addLast(HEARTBEAT + SPACE_DELIMITER + leftBorder + SPACE_DELIMITER + rightBorder);
                m.send(dealer);
            }
        }
    }
}
