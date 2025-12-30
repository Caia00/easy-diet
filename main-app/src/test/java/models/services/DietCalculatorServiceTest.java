package models.services;

import models.AppCategory;
import models.CommercialProduct;
import models.NutritionalTarget;
import models.NutritionalValues;
import models.beans.MealDemand;
import models.beans.ShoppingCalculationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DietCalculatorServiceTest {

    private CommercialProduct createProduct(String name, AppCategory cat, double weightPack,
                                            double kcal, double prot, double carbs, double fat, double fiber) {

        NutritionalValues nv = new NutritionalValues(kcal, carbs, 0.0, fat, fiber, prot);
        return new CommercialProduct(name, 0.0, weightPack, cat, nv, false);
    }


    private NutritionalTarget createTarget(AppCategory cat, double kcal, double prot, double carbs, double fat, double fiber) {

        return new NutritionalTarget(cat, kcal, prot, carbs, fat, fiber, 0.0);
    }

    @Test
    @DisplayName("Calcolo Proteine: Deve calcolare i grammi corretti basandosi sulle proteine")
    void testCalculateSinglePortionShouldCalculateBasedOnProteins() {
        //ARRANGE
        //Target: Carne Rossa, richiede 20g di proteine
        NutritionalTarget target = createTarget(AppCategory.CARNE_ROSSA, 0, 20, 0, 0, 0);

        //Prodotto: Bistecca, pacco da 500g, 150kcal, 20g proteine, 0 carbs, 5 grassi, 0 fibre
        CommercialProduct steak = createProduct("Bistecca", AppCategory.CARNE_ROSSA, 500, 150, 20, 0, 5, 0);

        List<MealDemand> demands = new ArrayList<>();
        demands.add(new MealDemand("Lunedì", "Pranzo", target));

        //ACT
        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(demands, steak);

        //ASSERT
        //Matematica: (20g richiesti / 20g nel prodotto) * 100 = 100g
        assertEquals(100.0, result.getTotalGramsRequired(), 0.1, "Dovrebbe servire 100g di bistecca");
        assertEquals(1, result.getPacksToBuy(), "Dovrebbe servire 1 confezione");
    }

    @Test
    @DisplayName("Calcolo Carboidrati: Deve calcolare i grammi corretti per la pasta")
    void testCalculateSinglePortionShouldCalculateBasedOnCarbs() {
        //ARRANGE
        //Target: Pasta, richiede 60g carboidrati
        NutritionalTarget target = createTarget(AppCategory.PASTA, 0, 0, 60, 0, 0);

        //Prodotto: Fusilli, 500g, 350kcal, 12 pro, 75 carbo, 1 fat, 3 fiber
        CommercialProduct pasta = createProduct("Fusilli", AppCategory.PASTA, 500, 350, 12, 75, 1, 3);

        List<MealDemand> demands = new ArrayList<>();
        demands.add(new MealDemand("Martedì", "Cena", target));

        //ACT
        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(demands, pasta);

        //ASSERT
        //Matematica: (60 richiesti / 75 presenti) * 100 = 80g
        assertEquals(80.0, result.getTotalGramsRequired(), 0.1, "Dovrebbero servire 80g di pasta");
    }

    @Test
    @DisplayName("Calcolo Pacchetti: Deve arrotondare per eccesso le confezioni")
    void testCalculateShoppingNeedsShouldCalculatePacksCorrectly() {
        //ARRANGE
        //Target: Carne bianca, richiede 20g pro
        NutritionalTarget target = createTarget(AppCategory.CARNE_BIANCA, 0, 20, 0, 0, 0);

        //Prodotto: Pollo, confezione da 200g
        CommercialProduct chicken = createProduct("Pollo", AppCategory.CARNE_BIANCA, 200, 100, 20, 0, 0, 0);

        //3 meal demand
        List<MealDemand> demands = new ArrayList<>();
        demands.add(new MealDemand("Lunedì", "Pranzo", target));
        demands.add(new MealDemand("Martedì", "Pranzo", target));
        demands.add(new MealDemand("Mercoledì", "Pranzo", target));

        //ACT
        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(demands, chicken);

        //ASSERT
        //Matematica: ((20 richiesti / 20 presenti) * 100) * n_meal_demand = 300g
        assertEquals(300.0, result.getTotalGramsRequired(), 0.1);

        //Pacco da 200g, servono 300g, 300/200 = 1.5 -> Arrotondato a 2 pacchi.
        assertEquals(2, result.getPacksToBuy(), "Con 300g necessari e pacchi da 200g, servono 2 pacchi");

        //Extra sul calcolo avanzi
        assertEquals(100.0, result.calculateLeftovers(200), 0.1, "Dovrebbero avanzare 100g (400 comprati - 300 usati)");
    }

    @Test
    @DisplayName("Logica Verdure: Priorità Fibre > Carbo > Kcal")
    void testCalculateSinglePortionShouldPrioritizeFibersForVegetables() {
        //ARRANGE
        //Target: Verdura, vuole 5g di fibre. Metto kcal a 100 per verificare che non usi le kcal ma le fibre
        NutritionalTarget target = createTarget(AppCategory.VERDURA, 100, 0, 10, 0, 5);

        //Prodotto: Spinaci, 1000g, 25kcal, 3 pro, 3 carb, 0 fat, 2.5 FIBRE
        CommercialProduct spinaci = createProduct("Spinaci", AppCategory.VERDURA, 1000, 25, 3, 3, 0, 2.5);

        List<MealDemand> demands = new ArrayList<>();
        demands.add(new MealDemand("Lunedì", "Cena", target));

        //ACT
        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(demands, spinaci);

        //ASSERT
        //Matematica: (5g richiesti / 2.5g presenti) * 100 = 200g prodotto
        assertEquals(200.0, result.getTotalGramsRequired(), 0.1, "Dovrebbe calcolare basandosi sulle FIBRE");
    }

    @Test
    @DisplayName("Fallback Kcal: Se il nutriente manca, usa le calorie")
    void testCalculateSinglePortionShouldFallbackToKcalIfNutrientMissing() {
        //ARRANGE
        //Target: Pesce, vorrei 20g proteine. Ma imposto anche 200kcal.
        NutritionalTarget target = createTarget(AppCategory.PESCE, 200, 20, 0, 0, 0);

        //Prodotto: Un pesce "immaginario" con 0 proteine, ma 100kcal.
        CommercialProduct weirdFish = createProduct("Pesce Finto", AppCategory.PESCE, 100, 100, 0, 0, 0, 0);

        List<MealDemand> demands = new ArrayList<>();
        demands.add(new MealDemand("Giovedì", "Pranzo", target));

        //ACT
        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(demands, weirdFish);

        //ASSERT
        //Proteine prod = 0 -> Fallback Kcal.
        //Matematica: (200kcal richieste / 100kcal presenti) * 100 = 200g
        assertEquals(200.0, result.getTotalGramsRequired(), 0.1, "Dovrebbe usare le Kcal se le proteine sono 0");
    }

    @Test
    @DisplayName("Caso Limite: Lista vuota")
    void testCalculateShoppingNeedsShouldReturnZeroForEmptyList() {
        List<MealDemand> emptyList = new ArrayList<>();
        CommercialProduct apple = createProduct("Mela", AppCategory.FRUTTA_FRESCA, 100, 50, 0, 10, 0, 2);

        ShoppingCalculationResult result = DietCalculatorService.calculateShoppingNeeds(emptyList, apple);

        assertEquals(0.0, result.getTotalGramsRequired());
        assertEquals(0, result.getPacksToBuy());
    }

}
