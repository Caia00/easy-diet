package controller;

import exception.EmailAlreadyRegisteredException;
import models.*;
import view.AuthView;
import java.time.LocalDate;
import models.factory.*;
import view.PatientHomeView;
import view.*;
import java.util.logging.*;


public class LoginController {
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());
    private final DAOFactory daoFactory;
    private final ViewFactory viewFactory;

    private final AuthView view;

    public LoginController(DAOFactory daoFactory, ViewFactory viewFactory) {
        this.daoFactory = daoFactory;
        this.viewFactory = viewFactory;

        this.view = viewFactory.createAuthView();

        this.view.setController(this);
    }

    public void start() {
        view.start();
    }

    //Metodo per il login
    public void login(String email, String password) {

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            view.showErrorMessage("Inserisci email e password.");
            return;
        }

        Profile profile = daoFactory.getProfileDAO().findByEmail(email);


        if (profile != null && profile.getPassword().equals(password)) {

            view.showSuccessMessage("Login effettuato! Benvenuto " + profile.getName());

            navigateToHome(profile);

        } else {
            view.showErrorMessage("Email o password non validi.");
        }
    }

    //Metodo per gestire la navigazione di schermata, in base se l'utente è un user o nutritionist
    private void navigateToHome(Profile profile) {
        view.close();

        if (profile instanceof User) {
            logger.info("Accesso come PAZIENTE: " + profile.getEmail());

            PatientHomeView homeView = viewFactory.createPatientHomeView();

            new PatientHomeController((User) profile, daoFactory, viewFactory, homeView).start();
        }
        else if (profile instanceof Nutritionist) {
            logger.info("Accesso come NUTRIZIONISTA: " + profile.getEmail());

            NutritionistHomeView docView = viewFactory.createNutritionistHomeView();

            new NutritionistHomeController((Nutritionist) profile, daoFactory, viewFactory, docView).start();
        }
    }

    public void registerPatient(String name, String surname, String email, String password,
                                LocalDate birthDate, double height, double weight, String gender) {
        try {
            checkIfEmailExists(email);

            User newUser = new User(name, surname, email, password, birthDate, height, weight, gender);
            daoFactory.getProfileDAO().save(newUser);

            view.showSuccessMessage("Paziente registrato! Ora puoi effettuare il login.");
            view.switchToLogin();

        } catch (EmailAlreadyRegisteredException e) {
            view.showErrorMessage(e.getMessage());
            view.switchToLogin();
        } catch (Exception e) {
            view.showErrorMessage("Errore durante la registrazione.");
        }
    }

    public void registerNutritionist(String name, String surname, String email, String password,
                                     LocalDate birthDate, String registerId) {
        try {
            checkIfEmailExists(email);

            Nutritionist newDoc = new Nutritionist(name, surname, email, password, birthDate, registerId);
            daoFactory.getProfileDAO().save(newDoc);

            view.showSuccessMessage("Nutrizionista registrato! Ora puoi effettuare il login.");
            view.switchToLogin();

        } catch (EmailAlreadyRegisteredException e) {
            view.showErrorMessage(e.getMessage());
            view.switchToLogin();
        } catch (Exception e) {
            view.showErrorMessage("Errore durante la registrazione.");
        }
    }



    private void checkIfEmailExists(String email) throws EmailAlreadyRegisteredException {
        if (daoFactory.getProfileDAO().findByEmail(email) != null) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}
