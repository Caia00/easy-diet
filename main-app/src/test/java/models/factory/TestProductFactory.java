package models.factory;

import models.AppCategory;
import models.CommercialProduct;
import models.NutritionalValues;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestProductFactory {
    private final ProductFactory factory = new ProductFactory();

    @Test
    void testCreateProductShouldCreateProductWithFullData() {
        //Input
        NutritionalValues validValues = new NutritionalValues(350, 72, 2, 1, 3, 12);

        CommercialProduct product = factory.createProduct(
                "Rigatoni Barilla",
                "Pasta",
                1.50,
                "500g",
                validValues
        );

        //Output
        assertNotNull(product, "Il prodotto deve essere creato");
        assertEquals("Rigatoni Barilla", product.getName());
        assertEquals(AppCategory.PASTA, product.getCategory());
        assertEquals(500.0, product.getWeightInGrams(), 0.1);
        assertEquals(350.0, product.getKcalPer100g(), 0.1);
        assertFalse(product.isDataEstimated(), "Con dati completi, il flag estimated deve essere false");
    }

    @Test
    void testNormalizeNameAndParseWeightShouldCleanNameAndParseWeightFromTitle() {
        //Input
        //Nome "sporco" e peso mancante nel parametro dedicato
        String rawName = "CarrefourPollo 400g";
        NutritionalValues validValues = new NutritionalValues(100, 0, 0, 0, 0, 20);

        CommercialProduct product = factory.createProduct(
                rawName,
                "Carne",
                5.00,
                "",
                validValues
        );

        //Output
        assertNotNull(product);
        //Ci aspettiamo "Carrefour Pollo 400g" invece di "CarrefourPollo 400g"
        assertEquals("Carrefour Pollo 400g", product.getName(), "Il nome dovrebbe essere stato normalizzato (CamelCase separato)");
        //Verifica che il peso sia stato estratto dal nome
        assertEquals(400.0, product.getWeightInGrams(), 0.1, "Il peso doveva essere estratto dalla stringa del nome");
    }

    @Test
    void testCreateProductShouldRetrieveFallbackDataFromJsonIfScrapedIs0() {
        //Input
        String productName = "Petto di Pollo Amadori";
        NutritionalValues emptyValues = new NutritionalValues(0, 0, 0, 0, 0, 0);

        CommercialProduct product = factory.createProduct(
                productName,
                "Carne",
                0.0,
                "100g",
                emptyValues
        );

        //Output
        assertNotNull(product, "Il prodotto dovrebbe essere creato anche se mancano i valori (grazie al fallback)");
        //Deve essere marcato come stimato
        assertTrue(product.isDataEstimated(), "Il flag isDataEstimated deve essere true quando usa il JSON");
        //Controllo che i valori non siano zero.
        assertTrue(product.getProteinsPer100g() > 0, "Dovrebbe aver caricato le proteine dal JSON reale.");
    }

    @Test
    void testCreateProductShouldReturnNullForUnknownCategory() {
        //Input
        NutritionalValues v = new NutritionalValues(100,0,0,0,0,0);

        CommercialProduct product = factory.createProduct(
                "Sasso di fiume",
                "Minerale",
                0.0,
                "1kg",
                v
        );

        //Output
        assertNull(product, "La factory deve restituire null se la categoria non viene riconosciuta");
    }

    @Test
    void testCreateProductShouldHandleNullInputsGracefully() {
        //Input
        CommercialProduct product = factory.createProduct(
                null, // Nome null
                "Carne",
                0.0,
                null,
                null
        );

        //Output
        assertNull(product, "Con nome null dovrebbe restituire null o gestire l'errore senza eccezioni non controllate");
    }
}
