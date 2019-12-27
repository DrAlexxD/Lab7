package lab7;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;

public class Proxy {
    public static final int CACHE_MSG = 1;
    public static final int CLIENT_MSG = 0;
    public static final String EMPTY_STRING = "";

    private ZMQ.Poller poller;
    private ZMQ.Socket client;
    private ZMQ.Socket cache;
    Map<ZFrame, CacheIntersections> intersections;

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        proxy.proxyInitialization();
        proxy.waitAndDoRequests();
    }

    private void proxyInitialization() {
        ZContext context = new ZContext();
        cache = context.createSocket(SocketType.ROUTER);
        client = context.createSocket(SocketType.ROUTER);
        cache.setHWM(0);
        client.setHWM(0);
        cache.bind(CacheStorage.DEALER_SOCKET);
        client.bind(Client.CLIENT_SOCKET);

        ZMQ.Poller poller = context.createPoller(2);
        poller.register(client, ZMQ.Poller.POLLIN);
        poller.register(cache, ZMQ.Poller.POLLIN);
    }

    private void waitAndDoRequests() {
        intersections = new HashMap<>();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            if (getClientRequest() == -1)
                break;
            getCacheStorageRequest();
        }
    }

    private int getClientRequest() {
        if (poller.pollin(CLIENT_MSG)) {
            ZMsg msg = ZMsg.recvMsg(client);
            if (msg == null) {
                return -1;
            }
            System.out.println("Get message: " + msg);

            if (intersections.isEmpty()) {
                ZMsg errMsg = new ZMsg();
                errMsg.add(msg.getFirst());
                errMsg.add(EMPTY_STRING);
                errMsg.add("no cache");
                errMsg.send(client);
            } else {
                String[] data = msg.getLast().toString().split(CacheStorage.SPACE_DELIMITER);
                if (data[0].equals(CacheStorage.GET)) {
                    for (Map.Entry<ZFrame, CacheIntersections> map : intersections.entrySet()) {
                        if (map.getValue().isIntersect(data[1])) {
                            ZFrame cacheFrame = map.getKey().duplicate();
                            msg.addFirst(cacheFrame);
                            msg.send(cache);
                        }
                    }
                } else {
                    if (data[0].equals(CacheStorage.PUT)) {
                        for (Map.Entry<ZFrame, CacheIntersections> map : intersections.entrySet()) {
                            if (map.getValue().isIntersect(data[1])) {
                                ZMsg msgCopy = msg;
                                ZFrame cacheFrame = map.getKey();
                                msgCopy.addFirst(cacheFrame);
                                System.out.println("Put message: " + msgCopy);
                                msgCopy.send(cache);
                            }
                        }
                    } else {
                        ZMsg errMsg = new ZMsg();
                        errMsg.add(msg.getFirst());
                        errMsg.add(EMPTY_STRING);
                        errMsg.add("bad message");
                        errMsg.send(client);
                    }
                }
            }
        }
        return 0;
    }

    private void getCacheStorageRequest() {
        if (poller.pollin(CLIENT_MSG)) {
            ZMsg msg = ZMsg.recvMsg(client);
            if (msg == null) {
                return -1;
            }
            System.out.println("Get message: " + msg);

    }
}
