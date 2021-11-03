package io.hdavid.jumper;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Portal {

    static final ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        while (true) {
            Socket socket = new Socket(host, port);
            L.log("Portal handler socket client oppened");
//            socket.setTcpNoDelay(true);
            socket.setSoTimeout(60000);
            int firstByte = -1;
            try {firstByte = socket.getInputStream().read();} catch (Exception IGNORE){}
            L.log("Portal First byte read");
            if (firstByte < 0)
                break;
            es.execute(new PortalAcceptor(socket, firstByte));
        }
    }
}
