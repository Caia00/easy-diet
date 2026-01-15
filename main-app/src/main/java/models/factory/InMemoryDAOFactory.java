package models.factory;

import models.dao.*;

public class InMemoryDAOFactory implements DAOFactory {


    @Override
    public ProfileDAO getProfileDAO() {return new InMemoryProfileDAO(); }

    @Override
    public DietPlanDAO getDietPlanDAO() {
        return new InMemoryDietPlanDAO();
    }

    @Override
    public ShoppingListDAO getShoppingListDAO() { return new InMemoryShoppingListDAO(); }



}
