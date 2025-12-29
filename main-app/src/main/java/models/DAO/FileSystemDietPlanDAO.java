package models.DAO;

import models.DietPlan;
import models.Nutritionist;
import models.Profile;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class FileSystemDietPlanDAO implements DietPlanDAO {
    private final ProfileDAO profileDAO;

    public FileSystemDietPlanDAO(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    @Override
    public void save(DietPlan dietPlan, String creatorEmail) {
        Profile profile = profileDAO.findByEmail(creatorEmail);

        if (profile instanceof Nutritionist) {
            Nutritionist nutritionist = (Nutritionist) profile;

            //Logica update, rimuovo vecchio e aggiungo nuovo
            nutritionist.deleteDietTemplate(dietPlan.getDietName());
            nutritionist.getDietTemplates().add(dietPlan);

            profileDAO.save(nutritionist);
        }
    }

    @Override
    public DietPlan findByOwner(String userEmail) {
        Profile profile = profileDAO.findByEmail(userEmail);
        if (profile instanceof User) {
            return ((User) profile).getDietPlan();
        }
        return null;
    }

    @Override
    public List<DietPlan> findAllSummariesByCreator(String nutritionistEmail) {
        Profile profile = profileDAO.findByEmail(nutritionistEmail);
        if (profile instanceof Nutritionist) {
            return ((Nutritionist) profile).getDietTemplates();
        }
        return new ArrayList<>();
    }

    @Override
    public void loadPlanDetails(DietPlan plan) {
        //Nessuna operazione dal json vengono ricaricati gli oggetti completi
    }

    @Override
    public void delete(DietPlan dietPlan, String ownerEmail) {
        Profile profile = profileDAO.findByEmail(ownerEmail);

        if (profile instanceof Nutritionist) {
            Nutritionist n = (Nutritionist) profile;
            n.deleteDietTemplate(dietPlan.getDietName());
            profileDAO.save(n);
        }
    }
}
