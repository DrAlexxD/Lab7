package lab7;

import org.zeromq.*;
import sun.java2d.pipe.SpanClipRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CacheStorage {
    public static final String DEALER_SOCKET = "tcp://localhost:2050";
    public static final int TIMEOUT = 5000;
    public static final String HEARTBEAT = "Heartbleed";
    public static final String SPACE_DELIMITER = " ";
    private static final int CACHE = 0;

    private static int leftBorder, rightBorder;
    private ZMQ.Socket dealer;
    private ZMQ.Poller poller;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        leftBorder = in.nextInt();
        rightBorder = in.nextInt();
        CacheStorage cacheStorage = new CacheStorage();
        cacheStorage.cacheInitialization();
        cacheStorage.waitAndDoRequests();
    }

    private void cacheInitialization() {
        Map<Integer, String> cache = new HashMap<>();
        for (int i = leftBorder; i <= rightBorder; i++) {
            cache.put(i, Integer.toString(i));
        }

        ZContext context = new ZContext();
        dealer = context.createSocket(SocketType.DEALER);
        dealer.setHWM(0);
        dealer.connect(DEALER_SOCKET);

        poller = context.createPoller(1);
        poller.register(dealer, ZMQ.Poller.POLLIN);
    }

    private void waitAndDoRequests() {
        long startTime = System.currentTimeMillis();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            if (System.currentTimeMillis() - startTime > TIMEOUT) {
                ZMsg m = new ZMsg();
                m.addLast(HEARTBEAT + SPACE_DELIMITER + leftBorder + SPACE_DELIMITER + rightBorder);
                m.send(dealer);
            }

            if (poller.pollin(CACHE)) {
                ZMsg msg = ZMsg.recvMsg(dealer);
                System.out.println("Get message: " + msg.toString());
                ZFrame content = msg.getLast();
                String[] contentArr = content.toString().split(SPACE_DELIMITER);


        }
    }
}
