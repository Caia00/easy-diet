package models.DAO;

import models.ShoppingList;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

public class InMemoryShoppingListDAO implements ShoppingListDAO {
    private static final Logger logger = Logger.getLogger(InMemoryShoppingListDAO.class.getName());
    private static List<ShoppingList> db = new ArrayList<>();

    private static int idCounter = 1;

    public InMemoryShoppingListDAO() {}

    @Override
    public void save(ShoppingList list, String ownerEmail) {
        if (list.getListId() == null) {
            list.setListId(idCounter++);
            db.add(list);
            list.setTotalItems(list.getTotalItemsCount());
            list.setTotalPrice(list.getTotalCost());
            logger.info("DEMO DB: Salvata lista ID " + list.getListId());
        } else {
            if (!db.contains(list)) {
                db.add(list);
            }
            logger.info("DEMO DB: Aggiornata lista ID " + list.getListId());
        }
    }

    @Override
    public List<ShoppingList> findAllSummariesByOwner(String ownerEmail) {
        return new ArrayList<>(db);
    }

    @Override
    public void loadDetails(ShoppingList summaryList) {
    }

    @Override
    public void delete(ShoppingList list, String email) {
        db.removeIf(l -> l.getListId() != null && l.getListId().equals(list.getListId()));
        logger.info("DEMO DB: Eliminata lista ID " + list.getListId());
    }
}
