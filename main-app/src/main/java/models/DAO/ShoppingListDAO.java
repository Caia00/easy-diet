package models.DAO;

import models.ShoppingList;

import java.util.List;

public interface ShoppingListDAO {
    void save(ShoppingList list, String ownerEmail);

    List<ShoppingList> findAllSummariesByOwner(String ownerEmail);

    void loadDetails(ShoppingList summaryList);

    void delete(ShoppingList list, String ownerEmail);
}
