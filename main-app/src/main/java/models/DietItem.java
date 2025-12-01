package models;

import java.util.Optional;

public class DietItem {
    private NutritionalTarget target;

    //Suggerimento inserito dal nutrizionista opzionalmente
    private CommercialProduct suggestedProduct;


    public DietItem(NutritionalTarget target, CommercialProduct suggestedProduct) {
        if (target == null) {
            throw new IllegalArgumentException("Il target nutrizionale è obbligatorio per creare un DietItem!");
        }
        this.target = target;
        this.suggestedProduct = suggestedProduct;
    }

    public DietItem(NutritionalTarget target) {
        this(target, null);
    }


    public NutritionalTarget getTarget() {
        return target;
    }

    public Optional<CommercialProduct> getSuggestedProduct() {
        return Optional.ofNullable(suggestedProduct);
    }

    public AppCategory getCategory() {
        return target.getCategory();
    }

    //Setter per aggiornare il prodotto suggerito in un secondo momento
    public void setSuggestedProduct(CommercialProduct suggestedProduct) {
        this.suggestedProduct = suggestedProduct;
    }
}
