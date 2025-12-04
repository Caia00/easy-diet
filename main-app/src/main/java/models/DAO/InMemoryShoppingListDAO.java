package models.DAO;

import models.ShoppingList;

import java.util.ArrayList;
import java.util.List;

public class InMemoryShoppingListDAO implements ShoppingListDAO {
    private static List<ShoppingList> db = new ArrayList<>();

    private static int idCounter = 1;

    public InMemoryShoppingListDAO() {}

    @Override
    public void save(ShoppingList list, String ownerEmail) {
        if (list.getListId() == null) {
            list.setListId(idCounter++);
            db.add(list);
            System.out.println("RAM: Salvata lista ID " + list.getListId());
        } else {
            if (!db.contains(list)) {
                db.add(list);
            }
            System.out.println("RAM: Aggiornata lista ID " + list.getListId());
        }
    }

    @Override
    public List<ShoppingList> findAllSummariesByOwner(String ownerEmail) {
        return new ArrayList<>(db);
    }

    @Override
    public void loadDetails(ShoppingList summaryList) {}

    @Override
    public void delete(ShoppingList list) {
        db.removeIf(l -> l.getListId() != null && l.getListId().equals(list.getListId()));
        System.out.println("DEMO DB: Eliminata lista ID " + list.getListId());
    }
}
