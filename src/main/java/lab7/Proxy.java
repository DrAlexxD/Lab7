package lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;

public class Proxy {

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
        Map<ZFrame, CacheCommutator> commutatorMap = new HashMap<>();
        while (!Thread.currentThread().isInterrupted()) {

        }
    }
}
