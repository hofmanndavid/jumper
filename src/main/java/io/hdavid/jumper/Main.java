package io.hdavid.jumper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            log.info("launching portal...");
            Portal.main(args);
            return;
        }

        if (args.length == 1) {
            log.info("launching jumper...");
            Jumper.main(args);
            return;
        }

        log.info("To be called with host and port if launching portal, or a config file path if launched as jumper");
    }
}
