package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShoppingList {
    private Integer listId;
    private String listName;
    private LocalDate creationDate;

    private SupermarketName supermarket;

    private List<ShoppingItem> items;

    public ShoppingList(String listName, SupermarketName supermarket) {
        this.listId = null;
        this.listName = listName;
        this.supermarket = supermarket;
        this.creationDate = LocalDate.now();
        this.items = new ArrayList<>();
    }

    public ShoppingList(Integer id, String listName, SupermarketName supermarket, LocalDate date) {
        this.listId = id;
        this.listName = listName;
        this.supermarket = supermarket;
        this.creationDate = date;
        this.items = new ArrayList<>();
    }

    //Metodi per gestione della lista

    public void addItem(CommercialProduct product, int quantity, boolean isForDiet) {

        for (ShoppingItem item : items) {
            if (item.getProduct().getName().equals(product.getName())) {
                item.increaseQuantity(quantity);
                return;
            }
        }

        items.add(new ShoppingItem(product, quantity, isForDiet));
    }

    public void decreaseQuantity(CommercialProduct product, int amountToRemove) {
        //Utilizzo un iterator per rimuovere l'oggetto dalla lista senza errori
        Iterator<ShoppingItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            ShoppingItem item = iterator.next();

            if (item.getProduct().getName().equals(product.getName())) {

                int currentQty = item.getQuantity();
                int newQty = currentQty - amountToRemove;

                if (newQty > 0) {
                    item.setQuantity(newQty);
                } else {
                    iterator.remove();
                }
                return;
            }
        }
    }

    public void removeProductCompletely(CommercialProduct product) {
        items.removeIf(item -> item.getProduct().getName().equals(product.getName()));
    }

    public double getTotalCost() {
        return items.stream().mapToDouble(ShoppingItem::getTotalPrice).sum();
    }

    public int getTotalItemsCount() {
        return items.stream().mapToInt(ShoppingItem::getQuantity).sum();
    }

    public String getListName() { return listName; }
    public LocalDate getCreationDate() { return creationDate; }
    public SupermarketName getSupermarket() { return supermarket; }
    public List<ShoppingItem> getItems() { return items; }
    public Integer getListId() { return listId; }
    public void setListId(Integer id) { this.listId = id; }

    @Override
    public String toString() {
        return String.format("Lista: '%s' [%s] - Data: %s - Totale: €%.2f (%d prodotti)",
                listName, supermarket, creationDate, getTotalCost(), getTotalItemsCount());
    }

}
