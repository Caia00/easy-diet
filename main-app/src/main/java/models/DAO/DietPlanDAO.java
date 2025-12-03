package models.DAO;

import models.DietPlan;

import java.util.List;

public interface DietPlanDAO {
    void save(DietPlan dietPlan, String creatorEmail);

    DietPlan findByOwner(String userEmail);

    List <DietPlan> findAllSummariesByCreator(String nutritionistEmail);

    DietPlan findById(int id);

    void delete(DietPlan dietPlan);


}
