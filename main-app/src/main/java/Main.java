

import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import models.factory.*;
import models.services.AppLogger;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static DAOFactory staticDaoFactory;
    private static ViewFactory staticViewFactory;

    public static void main(String[] args) {
        AppLogger.setup();
        logger.info("Verifica parametri di avvio...");

        String daoParam = (args.length >= 1) ? args[0].toLowerCase() : "file";
        String viewParam = (args.length >= 2) ? args[1].toLowerCase() : "gui";

        if (viewParam.equals("cli")) {
            staticDaoFactory = createDaoFactory(daoParam);
            staticViewFactory = new CliViewFactory();

            logger.info("Avvio in modalità CLI...");
            LoginController loginController = new LoginController(staticDaoFactory, staticViewFactory);
            loginController.start();
        } else {
            logger.info("Avvio in modalità GUI...");
            launch(args);
        }
    }

    private static DAOFactory createDaoFactory(String param) {
        return switch (param) {
            case "sql" -> new SqlDAOFactory();
            case "mem" -> new InMemoryDAOFactory();
            default -> new FileSystemDAOFactory();
        };
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            List<String> args = getParameters().getRaw();
            String daoParam = (!args.isEmpty()) ? args.getFirst().toLowerCase() : "file";

            DAOFactory daoFactory = createDaoFactory(daoParam);
            ViewFactory viewFactory = new GuiViewFactory(primaryStage);

            LoginController loginController = new LoginController(daoFactory, viewFactory);
            loginController.start();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Errore fatale durante lo start GUI: " + e.getMessage(), e);
        }
    }


}
