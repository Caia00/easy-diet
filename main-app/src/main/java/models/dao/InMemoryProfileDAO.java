package models.dao;

import models.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class InMemoryProfileDAO implements ProfileDAO {

    private static final Logger logger = Logger.getLogger(InMemoryProfileDAO.class.getName());
    private static List<Profile> db = new ArrayList<>();

    public InMemoryProfileDAO() {
        //Creatore vuoto in quanto non ci sarà bisogno di inizializzare l'oggetto
    }

    @Override
    public void save(Profile profile) {
        delete(profile.getEmail());
        db.add(profile);
        logger.info("DEMO DB: Salvato profilo " + profile.getEmail());
    }

    @Override
    public Profile findByEmail(String email) {
        return db.stream().filter(p -> p.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    @Override
    public boolean assignDiet(String patientEmail, DietPlan plan) {
        Profile p = findByEmail(patientEmail);

        if (p instanceof User patient) {

            patient.assignDiet(plan);

            logger.info(() -> "DEMO DB: Assegnata dieta " + plan.getDietName() + " a " + patientEmail);
            return true;
        }
        return false;
    }


    @Override
    public void delete(String email) {
        db.removeIf(p -> p.getEmail().equalsIgnoreCase(email));
        logger.info(() -> "DEMO DB: Eliminato profilo di: " + email);
    }
}
