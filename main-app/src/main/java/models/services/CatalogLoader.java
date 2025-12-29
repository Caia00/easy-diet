package models.services;

import models.CommercialProduct;
import java.util.List;

public interface CatalogLoader {
    List<CommercialProduct> loadCatalog();
}
