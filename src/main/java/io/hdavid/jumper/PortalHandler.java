package io.hdavid.jumper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Slf4j
public class PortalHandler implements Runnable {
    private final Socket portal;

    @Override
    public void run() {
        JumperSocketDTO js = null;
        try {
            js = Jumper.portalConnections.poll(57, TimeUnit.SECONDS);
            if (js == null) {
                log.info("Portal Handler discarding poratl connection due to no Jumpers available");
                try { portal.close(); } catch (Exception IGNORED) { }
                return;
            } else {

                portal.getOutputStream().write(js.remoteHostAndPort.getBytes(StandardCharsets.US_ASCII));
                portal.getOutputStream().write('\n');
                portal.getOutputStream().flush();
                log.info("Portal Handler just wrote bridge address");
                Jumper.es.execute(new DualIoStream(js.orig, portal));
                log.info("Portal Handler DualIOStream orig to portal");
                Jumper.es.execute(new DualIoStream(portal, js.orig));
                log.info("Portal Handler DualIOStream portal to orig");
            }
        } catch (Exception e){
            e.printStackTrace();
            try { portal.close(); } catch (Exception IGNORED) { }
            try { js.orig.close(); } catch (Exception IGNORED) { }
        }
    }
}
