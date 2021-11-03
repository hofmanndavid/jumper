package io.hdavid.jumper;

import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Portal {

    static final ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        while (true) {
            Socket socket = new Socket(host, port);
            log.info("Portal handler socket client opened");
//            socket.setTcpNoDelay(true);
            socket.setSoTimeout(60000);
            int firstByte = -1;
            try {firstByte = socket.getInputStream().read();} catch (Exception IGNORE){}
            log.info("Portal First byte read");
            if (firstByte >= 0) {
                es.execute(new PortalAcceptor(socket, firstByte));
            } else {
                socket.close();
//                break;
            }
        }
    }
}
