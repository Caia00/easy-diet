package models.factory;

import models.DAO.*;

public class SqlDAOFactory implements DAOFactory {

    @Override
    public ProfileDAO getProfileDAO() { return new SqlProfileDAO(); }

    @Override
    public DietPlanDAO getDietPlanDAO() {
        return new SqlDietPlanDAO();
    }

    @Override
    public ShoppingListDAO getShoppingListDAO() { return new SqlShoppingListDAO(); }



}
