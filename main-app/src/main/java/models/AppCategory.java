package models;

public enum AppCategory {
    // --- FONTI DI CARBOIDRATI ---
    PASTA,              // Pasta di semola, integrale, all'uovo
    RISO_E_CEREALI,     // Riso, Farro, Orzo, Couscous, Quinoa (chicchi)
    PANE_E_SOSTITUTI,   // Pane, Pan bauletto, Cracker, Grissini, Gallette
    PATATE_E_TUBERI,    // Patate, Patate dolci
    FARINE,

    // --- COLAZIONE E DOLCI ---
    BISCOTTI_E_DOLCI_COLAZIONE, // Biscotti, Fette biscottate, Merendine, Croissant
    CEREALI_COLAZIONE,          // Corn flakes, Muesli, Avena (fiocchi)
    DOLCI_E_SNACK,              // Cioccolato, Barrette, Patatine (cose "extra")
    CREME_SPALMABILI,           // Marmellata, Crema nocciole, Miele

    // --- PROTEINE ---
    CARNE_BIANCA,
    CARNE_ROSSA,
    PESCE,
    AFFETTATI_E_SALUMI,
    UOVA,
    LEGUMI,             // In scatola o secchi

    // --- LATTICINI ---
    LATTE_E_BEVANDE_VEG, // Latte vaccino, Latte di soia/avena
    YOGURT_E_FERMENTATI, // Yogurt greco, Kefir
    FORMAGGI,            // Mozzarella, Grana, ecc.

    // --- ORTOFRUTTA ---
    FRUTTA_FRESCA,
    FRUTTA_SECCA,       // Noci, Mandorle (sono grassi, non frutta fresca!)
    VERDURA,

    // --- CONDIMENTI ---
    OLIO_E_GRASSI,      // Olio EVO, Burro
    SALSE,              // Maionese, Ketchup, Sugo pronto

    BEVANDE,
    SCONOSCIUTO
}
