package models.dao;

import models.DietPlan;
import models.Profile;

public interface ProfileDAO {
    void save(Profile profile);

    Profile findByEmail(String email);

    boolean assignDiet(String patientEmail, DietPlan plan);

    void delete(String email);
}
