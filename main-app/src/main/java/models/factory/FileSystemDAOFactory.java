package models.factory;

import models.dao.*;

import java.io.File;

public class FileSystemDAOFactory implements DAOFactory {

    private static final String STORAGE_PATH = "data" + File.separator + "profiles.json";

    @Override
    public ProfileDAO getProfileDAO() {
        return new FileSystemProfileDAO(STORAGE_PATH);
    }

    @Override
    public DietPlanDAO getDietPlanDAO() {
        return new FileSystemDietPlanDAO(getProfileDAO());
    }

    @Override
    public ShoppingListDAO getShoppingListDAO() {
        return new FileSystemShoppingListDAO(getProfileDAO());
    }

}
