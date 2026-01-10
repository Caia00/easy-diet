package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ShoppingList {
    private static final Logger logger = Logger.getLogger(ShoppingList.class.getName());
    private Integer listId;
    private String listName;
    private int totalItems = 0;
    private Double totalPrice = 0.0;
    private LocalDate creationDate;

    private SupermarketName supermarket;

    private List<ShoppingItem> items = new ArrayList<>();

    public ShoppingList() {}

    public ShoppingList(String listName, SupermarketName supermarket) {
        this.listId = null;
        this.listName = listName;
        this.supermarket = supermarket;
        this.creationDate = LocalDate.now();
        this.items = new ArrayList<>();
    }

    public ShoppingList(Integer id, String listName, SupermarketName supermarket, LocalDate date, int totalItems, Double totalPrice) {
        this.listId = id;
        this.listName = listName;
        this.supermarket = supermarket;
        this.creationDate = date;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
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

    public void decreaseQuantity(ShoppingItem item, int amountToRemove) {
        if(items.contains(item)) {
            item.decreaseQuantity(amountToRemove);
        }else{
            logger.info("Item non trovato nella lista");
        }
    }

    public void removeProductCompletely(ShoppingItem item) {
        if (items.contains(item)) {
            items.remove(item);
        }else{
            logger.info("Item non trovato nella lista");
        }
    }

    @JsonIgnore
    public double getTotalCost() {
        return items.stream().mapToDouble(ShoppingItem::getTotalPrice).sum();
    }

    @JsonIgnore
    public int getTotalItemsCount() {
        return items.stream().mapToInt(ShoppingItem::getQuantity).sum();
    }

    public String getListName() { return listName; }
    public void setListName(String listName) { this.listName = listName; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
    public SupermarketName getSupermarket() { return supermarket; }
    public void setSupermarket(SupermarketName supermarket) { this.supermarket = supermarket; }
    public List<ShoppingItem> getItems() { return items; }
    public void setItems(List<ShoppingItem> items) { this.items = items; }
    public Integer getListId() { return listId; }
    public void setListId(Integer id) { this.listId = id; }
    public int getTotalItems() { return totalItems; }
    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double price) { this.totalPrice = price; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }

    @Override
    public String toString() {
        return String.format("Lista: '%s' [%s] - Data: %s - Totale: €%.2f (%d prodotti)",
                listName, supermarket, creationDate, getTotalCost(), getTotalItemsCount());
    }

}
