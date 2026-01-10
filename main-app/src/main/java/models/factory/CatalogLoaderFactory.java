package models.factory;

import models.services.CatalogLoader;
import models.SupermarketName;
import models.services.CsvCatalogLoader;

public class CatalogLoaderFactory {
    private CatalogLoaderFactory(){
        //Costruttore vuoto perché non utilizzato
    }
    public static CatalogLoader getLoader(SupermarketName supermarket) {

        ProductFactory productFactory = new ProductFactory();

        //Quando si potranno supportare altri supermercati basterà modificare questo if con uno switch su supermarket
        if (supermarket == SupermarketName.CARREFOUR) {
            return new CsvCatalogLoader("catalogo_completo_carrefour1.csv", productFactory);
        } else {
            return null;
        }
    }
}
