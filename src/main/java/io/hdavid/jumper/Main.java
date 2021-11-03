package io.hdavid.jumper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length != 3 || args[0] == null || !args[0].matches("^-[jp]$")) {
            log.info("Incorrect parameters");
            log.info("Launching the portal:");
            log.info("java -jar jumper.jar -p host port");
            log.info("Launching the jumper:");
            log.info("java -jar jumper.jar -j port config/file/path ");
            return;
        }

        if (args[0].equals("-p")) {
            log.info("launching portal...");
            Portal.main(new String[]{args[1],args[2]});
            return;
        }

        log.info("launching jumper...");
        Jumper.main(new String[]{args[1],args[2]});
    }
}
