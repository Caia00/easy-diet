package models.DAO;

import models.Profile;

import java.util.List;

public interface ProfileDAO {
    void save(Profile profile);

    Profile findByEmail(String email);

    void delete(String email);
}
