package models;

public class ShoppingItem { //Classe usata per modellare gli oggetti all'interno della lista della spesa (più o meno un decorator di CommercialProduct)

    private CommercialProduct product;

    private int quantity;

    private boolean isForDiet;

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
        }else{
            System.out.println("Impossibile diminuire di quantità richiesta...");
        }
    }

    public CommercialProduct getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isForDiet() { return isForDiet; }

    @Override
    public String toString() {
        return String.format("%dx %s (€%.2f)", quantity, product.getName(), getTotalPrice());
    }

}
