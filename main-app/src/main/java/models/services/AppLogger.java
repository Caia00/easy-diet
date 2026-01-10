package models.services;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static boolean configured = false;

    private AppLogger() {
        //Costruttore vuoto usato solo per nascondere quello pubblico
    }

    public static void setup() {
        if (configured) return;

        Logger rootLogger = Logger.getLogger("");

        try {
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            FileHandler fileHandler = new FileHandler("easydiet.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.INFO);
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            consoleHandler.setLevel(Level.OFF);

            rootLogger.addHandler(fileHandler);
            rootLogger.addHandler(consoleHandler);
            rootLogger.setLevel(Level.INFO);

            configured = true;
            Logger.getLogger(AppLogger.class.getName()).info("Sistema di Logging inizializzato.");

        } catch (IOException e) {
            rootLogger.log(Level.SEVERE, e, () -> "Impossibile inizializzare il logger: " + e.getMessage());
        }
    }
}
