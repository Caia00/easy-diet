package models.services;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static boolean configured = false;

    public static void setup() {
        if (configured) return;

        try {
            Logger rootLogger = Logger.getLogger("");
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
            System.err.println("Impossibile inizializzare il logger: " + e.getMessage());
        }
    }
}
