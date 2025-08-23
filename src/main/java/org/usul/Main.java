package org.usul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.usul.plaiground.backend.app.PlaigroundApp;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  throws Exception {
        log.info("starting");

        Injector injector = Guice.createInjector(); // no module needed
        PlaigroundApp app = injector.getInstance(PlaigroundApp.class);

        try {
            app.run();
        } catch (Exception e) {
            log.error("error happened: ", e);
        }

        log.info("DONE");
    }
}