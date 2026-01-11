package models.services;

import models.AppCategory;
import models.CommercialProduct;
import models.factory.ProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestCsvCatalogLoader {
    private List<CommercialProduct> loadedProducts;

    //Metodo setup per i test, caricamento catalogo e salvataggio lista in attributo di classe
    @BeforeEach
    void setUp() {
        //Input
        ProductFactory realFactory = new ProductFactory();
        CsvCatalogLoader loader = new CsvCatalogLoader("test_catalog.csv", realFactory);
        this.loadedProducts = loader.loadCatalog();
    }


    @Test
    void testLoadCatalogAndParseLineShouldLoadCorrectNumberOfProducts() {
        //Output
        assertNotNull(loadedProducts, "La lista non deve essere null");

        //Il file test_catalog.csv ha 4 righe di dati, di cui una non valida
        assertEquals(3, loadedProducts.size(), "Dovrebbe aver caricato esattamente 3 prodotti validi");
    }


    @Test
    void testParseDoubleSafeAndParseKcalShouldParseProductDetailsCorrectly() {
        //Output
        //Verifica rigatoni, parsing standard
        CommercialProduct p1 = loadedProducts.get(0);
        assertEquals("Rigatoni Barilla", p1.getName());
        assertEquals(AppCategory.PASTA, p1.getCategory());
        assertEquals(350.0, p1.getKcalPer100g(), 0.1);

        //Verifica mela, presenza virgola e scritta kcal
        CommercialProduct p2 = loadedProducts.get(1);
        assertEquals("Mela Fuji", p2.getName());
        assertEquals(0.80, p2.getPrice(), 0.01, "Prezzo con virgola errato");
        assertEquals(52.0, p2.getKcalPer100g(), 0.1, "Parsing '52 kcal' errato");

        //Verifica pollo, presenza struttura complessa Kj / kcal
        CommercialProduct p3 = loadedProducts.get(2);
        assertEquals("Pollo Arrosto", p3.getName());
        assertEquals(100.0, p3.getKcalPer100g(), 0.1, "Parsing regex '150 kj / 100 kcal' errato");
    }


    @Test
    void shouldReturnEmptyListIfFileMissing() {
        //Qui non si usa il setup in quanto vogliamo un file sbagliato apposta
        //Input
        ProductFactory factory = new ProductFactory();
        CsvCatalogLoader badLoader = new CsvCatalogLoader("file_inesistente.csv", factory);

        List<CommercialProduct> emptyList = badLoader.loadCatalog();

        //Output
        assertTrue(emptyList.isEmpty(), "Deve restituire lista vuota se file manca");
    }
}
