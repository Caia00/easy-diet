package models.DAO;

import models.DietPlan;

import java.util.ArrayList;
import java.util.List;

public class InMemoryDietPlanDAO implements DietPlanDAO {
    private static List<DietPlan> templates = new ArrayList<>();
    private static int idCounter = 1;

    public InMemoryDietPlanDAO() {}

    @Override
    public void save(DietPlan plan, String creatorEmail) {
        if (plan.getDietId() == null) {
            plan.setDietId(idCounter++);
            templates.add(plan);
        } else {
            delete(plan);
            templates.add(plan);
        }
        System.out.println("DEMO DB: Template dieta salvato " + plan.getDietName());
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
    public DietPlan findById(int dietId) {
        return templates.stream()
                .filter(d -> d.getDietId() != null && d.getDietId() == dietId)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(DietPlan plan) {
        templates.removeIf(p -> p.getDietId().equals(plan.getDietId()));
    }
}
