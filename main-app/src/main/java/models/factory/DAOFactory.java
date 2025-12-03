package models.factory;

import models.DAO.*;

public interface DAOFactory {
    ProfileDAO getProfileDAO();
    DietPlanDAO getDietPlanDAO();
}
