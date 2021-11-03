package io.hdavid.jumper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@Slf4j
class PortalAcceptor implements Runnable {
    private final Socket origin;
    private final int firstByte;
    private final Socket target = new Socket();

    @SneakyThrows
    @Override
    public void run() {
        if (firstByte < 0)
            return;
        val hostToConnect = getHostToConnectTo();
        log.info("PortalHandler, establishing dualcomm on "+ hostToConnect);
//            this.target.setTcpNoDelay(true);
        this.target.connect(hostToConnect);
        log.info("PortalHandler, connected to"+ hostToConnect);

        log.info("PortalHandler DualIOStream orig to target");
        Portal.es.submit(new DualIoStream(origin, target));
        log.info("PortalHandler DualIOStream target to orig");
        Portal.es.submit(new DualIoStream(target, origin));
    }

    private InetSocketAddress getHostToConnectTo() throws Exception {
        val baos = new ByteArrayOutputStream();
        baos.write(firstByte);
        int readedByte = 0;
        String[] hostPortToConnectArr = null;
        while ((readedByte = origin.getInputStream().read()) >= 0) {
            if (readedByte == '\n') {
                hostPortToConnectArr = new String(baos.toByteArray(), StandardCharsets.US_ASCII).split(":");
                break;
            }
            baos.write(readedByte);
        }
        return new InetSocketAddress(hostPortToConnectArr[0], Integer.parseInt(hostPortToConnectArr[1]));
    }
}
