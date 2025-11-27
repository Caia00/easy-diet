package models;

import models.CommercialProduct;
import java.util.List;

public interface CatalogLoader {
    List<CommercialProduct> loadCatalog();
}
