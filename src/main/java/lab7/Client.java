package lab7;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

public class Client {

    public static final String CLIENT_SOCKET = "tcp://localhost:2052";
    private static final String CLIENT_ALIVE = "Client is online";

    public static void main(String[] args){
        ZContext context = new ZContext();
        ZMQ.Socket client = context.createSocket(SocketType.REQ);
        client.setHWM(0);
        client.connect(CLIENT_SOCKET);
        System.out.println(CLIENT_ALIVE);
    }
}
