package models.factory;

import models.DAO.*;

public class InMemoryDAOFactory implements DAOFactory {

    private final ProfileDAO profileDAO = new InMemoryProfileDAO();

    @Override
    public ProfileDAO getProfileDAO() {
        return profileDAO;
    }

}
