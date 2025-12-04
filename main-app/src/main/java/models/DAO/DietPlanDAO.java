package models.DAO;

import models.DietPlan;

import java.util.List;

public interface DietPlanDAO {
    void save(DietPlan dietPlan, String creatorEmail);

    DietPlan findByOwner(String userEmail);

    List <DietPlan> findAllSummariesByCreator(String nutritionistEmail);

    void loadPlanDetails(DietPlan plan);

    void delete(DietPlan dietPlan);


}
