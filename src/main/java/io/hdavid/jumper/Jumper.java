package io.hdavid.jumper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class Jumper {
    public static final BlockingQueue<JumperSocketDTO> portalConnections = new ArrayBlockingQueue(1024);
    public static final ExecutorService es = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        val portalServerPort = Integer.parseInt(args[0]);
        val jumperMappingPath = Paths.get(args[1]);

        List<LiveMappingDTO> mappings = Files.readAllLines(jumperMappingPath).stream()
                .map(s -> new LiveMappingDTO(s.trim().split(" +"))).toList();
        log.info("Read mappings");
        updateLocalHostsFile(mappings);

        for (LiveMappingDTO m : mappings) {
            ServerSocket ms = new ServerSocket(m.jumperPort);
            log.info("opened jumper port "+ m.jumperPort);
            es.execute(new JumperAcceptor(m, ms));
        }

        val ss = new ServerSocket(portalServerPort);
        log.info("opened portal port "+portalServerPort);

        while (!Thread.interrupted()) {
            val portal = ss.accept();
            log.info("accepted portal connection ");
            es.execute(new PortalHandler(portal));
        }
        Thread.sleep(1000L*60*60*24*365);
    }

    private static void updateLocalHostsFile(List<LiveMappingDTO> mappings) {
        // requires elevated privileges and a specific pattern in the file
    }

    @AllArgsConstructor
    public static class JumperAcceptor implements Runnable {
        private final LiveMappingDTO mapping;
        private final ServerSocket ms;

        @Override
        @SneakyThrows
        public void run() {
            while (!Thread.interrupted()) {
                Socket lms = ms.accept();
                log.info("Jumper acceptor got accept request");
                Jumper.es.execute(new JumperAcceptorBridge(mapping, lms));
            }
        }
    }

    @AllArgsConstructor
    public static class JumperAcceptorBridge implements Runnable {
        private final LiveMappingDTO m;
        private final Socket lms;

        @SneakyThrows
        @Override
        public void run() {
            val js = new JumperSocketDTO(lms, m.jumperTargetAddress+":"+m.jumperTargetPort);
            Jumper.portalConnections.put(js);
            log.info("Jumper acceptor Bridge put connection into portal Connections");
            Thread.sleep(2000);
            if (Jumper.portalConnections.remove(js)){
                log.info("Jumper acceptor Bridge REMOVED connection from portal Connections");
                js.orig.close();
            }
        }
    }
}
