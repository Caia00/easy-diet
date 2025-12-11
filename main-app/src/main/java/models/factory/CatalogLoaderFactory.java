package models.factory;

import models.CatalogLoader;
import models.SupermarketName;
import models.services.CsvCatalogLoader;

public class CatalogLoaderFactory {
    public static CatalogLoader getLoader(SupermarketName supermarket) {

        ProductFactory productFactory = new ProductFactory();

        switch (supermarket) {
            case CARREFOUR:
                return new CsvCatalogLoader("catalogo_completo_carrefour1.csv", productFactory);

            /* CASI FUTURI
            case CONAD:
                return new ApiCatalogLoader("...");
            case COOP:
                return new CsvCatalogLoader("assets/coop.csv");
            */

            default:
                return null;
        }
    }
}
