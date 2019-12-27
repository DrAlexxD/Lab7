package lab7;

import org.zeromq.*;

import java.util.HashMap;
import java.util.Map;

public class Proxy {
    public static final int CACHE_MSG = 1;
    public static final int CLIENT_MSG = 0;

    private ZMQ.Poller poller;
    private ZMQ.Socket toClient;
    private ZMQ.Socket toCache;

    public static void main(String[] args) {
        Proxy proxy = new Proxy();
        proxy.proxyInitialization();
        proxy.waitAndDoRequests();
    }

    private void proxyInitialization() {
        ZContext context = new ZContext();
        ZMQ.Socket toCache = context.createSocket(SocketType.ROUTER);
        ZMQ.Socket toClient = context.createSocket(SocketType.ROUTER);
        toCache.setHWM(0);
        toClient.setHWM(0);
        toCache.bind(CacheStorage.DEALER_SOCKET);
        toClient.bind(Client.CLIENT_SOCKET);

        ZMQ.Poller poller = context.createPoller(2);
        poller.register(toClient, ZMQ.Poller.POLLIN);
        poller.register(toCache, ZMQ.Poller.POLLIN);
    }

    private void waitAndDoRequests() {
        Map<ZFrame, CacheIntersections> intersections = new HashMap<>();
        while (!Thread.currentThread().isInterrupted()) {
            poller.poll(1);
            if (getClientRequest() == -1)
                break;
            getCacheStorageRequest();
        }
    }

    private int getClientRequest() {
        if (poller.pollin(CLIENT_MSG)) {
            ZMsg msg = ZMsg.recvMsg(toClient);
            if (msg == null) {
                return -1;
            }

        }
        return 0;
    }

    private void  getCacheStorageRequest() {

    }
}
