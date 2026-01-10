package models.dao;

import models.DietPlan;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class InMemoryDietPlanDAO implements DietPlanDAO {

    private static final Logger logger = Logger.getLogger(InMemoryDietPlanDAO.class.getName());
    private static List<DietPlan> templates = new ArrayList<>();
    private int idCounter = 1;

    public InMemoryDietPlanDAO() {
        //Creatore vuoto in quanto non ci sarà bisogno di inizializzare l'oggetto
    }

    @Override
    public void save(DietPlan plan, String creatorEmail) {
        if (plan.getDietId() == null) {
            plan.setDietId(idCounter++);
            templates.add(plan);
        } else {
            delete(plan, "");
            templates.add(plan);
        }
        logger.info("DEMO DB: Template dieta salvato " + plan.getDietName());
    }

    @Override
    public DietPlan findByOwner(String userEmail) {
        return null;
    }

    @Override
    public List<DietPlan> findAllSummariesByCreator(String nutritionistEmail) {
        return new ArrayList<>(templates);
    }

    @Override
    public void loadPlanDetails(DietPlan plan) {
        //Metodo vuoto in quanto trovandoci nella versione Demo inMemory se si provasse ad aprire un DietPlan precedentemente creato questo sarà già presente in memoria
    }

    @Override
    public void delete(DietPlan plan, String email) {
        templates.removeIf(p -> p.getDietId().equals(plan.getDietId()));
        logger.info("DEMO DB: Template dieta eliminato " + plan.getDietName());
    }
}
