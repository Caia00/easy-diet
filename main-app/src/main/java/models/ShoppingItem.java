package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShoppingItem { //Classe usata per modellare gli oggetti all'interno della lista della spesa (più o meno un decorator di CommercialProduct)

    private CommercialProduct product;

    private int quantity;

    private boolean isForDiet;

    public ShoppingItem(){}


    public ShoppingItem(CommercialProduct product, int quantity, boolean isForDiet) {
        this.product = product;
        this.quantity = quantity;
        this.isForDiet = isForDiet;
    }


    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity - amount > 0) {
            this.quantity -= amount;
        }
    }

    public CommercialProduct getProduct() { return product; }
    public void setProduct(CommercialProduct product) { this.product = product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isForDiet() { return isForDiet; }
    public void setForDiet(boolean isForDiet) { this.isForDiet = isForDiet; }

    @Override
    public String toString() {
        return String.format("%dx %s (€%.2f)", quantity, product.getName(), getTotalPrice());
    }

}
