package models.DAO;

import models.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryProfileDAO implements ProfileDAO {
    private static List<Profile> db = new ArrayList<>();

    public InMemoryProfileDAO() {
    }

    @Override
    public void save(Profile profile) {
        delete(profile.getEmail());
        db.add(profile);
        System.out.println("DEMO DB: Salvato profilo " + profile.getEmail());
    }

    @Override
    public Profile findByEmail(String email) {
        return db.stream().filter(p -> p.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }

    @Override
    public boolean assignDiet(String patientEmail, DietPlan plan) {
        Profile p = findByEmail(patientEmail);

        if (p != null && p instanceof User) {
            User patient = (User) p;

            patient.assignDiet(plan);

            System.out.println("DEMO DB: Assegnata dieta " + plan.getDietId() + " a " + patientEmail);
            return true;
        }
        return false;
    }


    @Override
    public void delete(String email) {
        db.removeIf(p -> p.getEmail().equalsIgnoreCase(email));
    }
}
