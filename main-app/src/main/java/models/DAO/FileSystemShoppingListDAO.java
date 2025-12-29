package models.DAO;

import models.Profile;
import models.ShoppingList;
import models.User;

import java.util.ArrayList;
import java.util.List;

public class FileSystemShoppingListDAO implements ShoppingListDAO {
    private final ProfileDAO profileDAO;

    public FileSystemShoppingListDAO(ProfileDAO profileDAO) {
        this.profileDAO = profileDAO;
    }

    @Override
    public void save(ShoppingList list, String ownerEmail) {
        Profile profile = profileDAO.findByEmail(ownerEmail);

        if (profile instanceof User) {
            User user = (User) profile;

            //Evitiamo duplicati cercando se già presente
            ShoppingList existing = user.getListByName(list.getListName());
            if (existing != null) {
                user.deleteList(existing.getListName());
            }
            list.setTotalPrice(list.getTotalCost());
            list.setTotalItems(list.getTotalItemsCount());

            user.saveShoppingList(list);

            profileDAO.save(user);
        }
    }

    @Override
    public List<ShoppingList> findAllSummariesByOwner(String ownerEmail) {
        Profile profile = profileDAO.findByEmail(ownerEmail);
        if (profile instanceof User) {
            return ((User) profile).getShoppingLists();
        }
        return new ArrayList<>();
    }

    @Override
    public void loadDetails(ShoppingList summaryList) {
        //Nessuna operazione, dal json vengono caricati gli oggetti completi
    }

    @Override
    public void delete(ShoppingList list, String ownerEmail) {
        Profile profile = profileDAO.findByEmail(ownerEmail);

        if (profile instanceof User) {
            User user = (User) profile;
            user.deleteList(list.getListName());
            profileDAO.save(user);
        }
    }
}
