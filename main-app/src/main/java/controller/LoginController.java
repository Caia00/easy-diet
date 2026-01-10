package controller;

import exception.EmailAlreadyRegisteredException;
import models.*;
import models.beans.ProfileBean;
import view.AuthView;
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

        if (profile instanceof User user) {
            logger.info(() -> "Accesso come PAZIENTE: " + user.getEmail());

            PatientHomeView homeView = viewFactory.createPatientHomeView();

            new PatientHomeController(user, daoFactory, viewFactory, homeView).start();
        }
        else if (profile instanceof Nutritionist nutritionist) {
            logger.info(() -> "Accesso come NUTRIZIONISTA: " + nutritionist.getEmail());

            NutritionistHomeView docView = viewFactory.createNutritionistHomeView();

            new NutritionistHomeController(nutritionist, daoFactory, viewFactory, docView).start();
        }
    }

    public void registerPatient(ProfileBean bean, double height, double weight, String gender) {
        try {
            checkIfEmailExists(bean.getEmail());

            User newUser = new User(bean, height, weight, gender);
            daoFactory.getProfileDAO().save(newUser);

            view.showSuccessMessage("Paziente registrato! Ora puoi effettuare il login.");
            view.switchToLogin();

        } catch (EmailAlreadyRegisteredException e) {
            view.showErrorMessage(e.getMessage());
            view.switchToLogin();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            view.showErrorMessage("Errore durante la registrazione.");
        }
    }

    public void registerNutritionist(ProfileBean bean, String registerId) {
        try {
            checkIfEmailExists(bean.getEmail());

            Nutritionist newDoc = new Nutritionist(bean.getName(), bean.getSurname(), bean.getEmail(), bean.getPassword(), bean.getBirthdate(), registerId);
            daoFactory.getProfileDAO().save(newDoc);

            view.showSuccessMessage("Nutrizionista registrato! Ora puoi effettuare il login.");
            view.switchToLogin();

        } catch (EmailAlreadyRegisteredException e) {
            view.showErrorMessage(e.getMessage());
            view.switchToLogin();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            view.showErrorMessage("Errore durante la registrazione.");
        }
    }



    private void checkIfEmailExists(String email) throws EmailAlreadyRegisteredException {
        if (daoFactory.getProfileDAO().findByEmail(email) != null) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }
}
