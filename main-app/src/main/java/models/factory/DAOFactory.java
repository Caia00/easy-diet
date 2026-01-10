package models.factory;

import models.dao.*;

public interface DAOFactory {
    ProfileDAO getProfileDAO();
    DietPlanDAO getDietPlanDAO();
    ShoppingListDAO getShoppingListDAO();
}
