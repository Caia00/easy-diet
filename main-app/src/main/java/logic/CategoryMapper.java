package logic;

import models.appCategory;

public class CategoryMapper {

        public static appCategory map(String rawName, String rawCategory) {

            String name = (rawName != null) ? rawName.toLowerCase() : "";
            String cat = (rawCategory != null) ? rawCategory.toLowerCase() : "";

            // Analisi nome prodotto da catalogo, per ricercare la corretta categoria.
            //L'ordine dei controlli è importante essendo strutturato per evitare errori.

            // --- CARBOIDRATI ---
            if (containsAny(name, "pasta", "spaghetti", "penne", "fusilli", "rigatoni", "maccheroni", "farfalle", "linguine", "orecchiette", "mezze maniche")) {
                return appCategory.PASTA;
            }
            if (containsAny(name, "riso", "basmati", "carnaroli", "arborio", "farro", "orzo", "couscous", "quinoa", "grano", "cereali mix")) {
                return appCategory.RISO_E_CEREALI;
            }
            if (containsAny(name, "patate", "patata")) {
                // Nota: Le patatine in busta (chips) spesso hanno "patatine" nel nome, potresti volerle distinguere
                if (name.contains("fritte") || name.contains("chips") || name.contains("patatine")) return appCategory.DOLCI_E_SNACK;
                return appCategory.PATATE_E_TUBERI;
            }
            if (containsAny(name, "pane", "pagnotta", "ciabatta", "bauletto", "cracker", "grissini", "taralli", "gallette", "piadina", "focaccia")) {
                return appCategory.PANE_E_SOSTITUTI;
            }
            if (containsAny(name, "farina", "semola", "amido")) {
                return appCategory.FARINE;
            }

            // --- COLAZIONE E DOLCI ---
            if (containsAny(name, "biscotti", "frollini", "gocciole", "pan di stelle", "croissant", "brioche", "merendina", "plumcake", "fette biscottate")) {
                return appCategory.BISCOTTI_E_DOLCI_COLAZIONE;
            }
            if (containsAny(name, "corn flakes", "muesli", "fiocchi", "avena", "cereali croccanti", "kellogg")) {
                return appCategory.CEREALI_COLAZIONE;
            }
            if (containsAny(name, "cioccolato", "barretta", "snack", "pop corn", "patatine", "wafer", "caramelle")) {
                return appCategory.DOLCI_E_SNACK;
            }
            if (containsAny(name, "marmellata", "confettura", "miele", "crema nocciole", "nutella", "burro d'arachidi")) {
                return appCategory.CREME_SPALMABILI;
            }

            // --- LATTICINI E UOVA ---
            if (containsAny(name, "uova", "albume", "tuorlo")) {
                return appCategory.UOVA;
            }
            // Specifico per bevande vegetali PRIMA di "latte" generico
            if (containsAny(name, "bevanda", "soia", "avena", "mandorla", "riso") && name.contains("latte")) {
                return appCategory.LATTE_E_BEVANDE_VEG;
            }
            if (containsAny(name, "latte")) {
                // Escludiamo il latte detergente o per il corpo (caso limite scraping errato)
                if (cat.contains("igiene") || cat.contains("corpo")) return appCategory.SCONOSCIUTO;
                return appCategory.LATTE_E_BEVANDE_VEG;
            }
            if (containsAny(name, "yogurt", "kefir", "yoghurt")) {
                return appCategory.YOGURT_E_FERMENTATI;
            }
            if (containsAny(name, "formaggio", "mozzarella", "grana", "parmigiano", "pecorino", "stracchino", "ricotta", "mascarpone", "provola", "scamorza", "gorgonzola")) {
                return appCategory.FORMAGGI;
            }

            // --- PROTEINE (Carne e Pesce) ---
            if (containsAny(name, "pollo", "tacchino", "coniglio", "faraona", "aia")) {
                // 'Aia' è una marca famosa di pollo, utile metterla
                return appCategory.CARNE_BIANCA;
            }
            if (containsAny(name, "manzo", "bovino", "vitello", "scottona", "hamburger", "suino", "salsiccia", "macinato", "bistecca", "agnello")) {
                return appCategory.CARNE_ROSSA;
            }
            if (containsAny(name, "prosciutto", "salame", "mortadella", "bresaola", "fesa", "speck", "pancetta", "wurstel")) {
                return appCategory.AFFETTATI_E_SALUMI;
            }
            if (containsAny(name, "tonno", "salmone", "merluzzo", "nasello", "gamberi", "pesce", "orata", "branzino", "sogliola", "platessa", "calamari", "vongole", "cozze")) {
                return appCategory.PESCE;
            }
            if (containsAny(name, "ceci", "fagioli", "lenticchie", "piselli", "soia", "edamame")) {
                return appCategory.LEGUMI;
            }

            // --- ORTOFRUTTA ---
            if (containsAny(name, "noci", "mandorle", "nocciole", "anacardi", "pistacchi", "arachidi")) {
                return appCategory.FRUTTA_SECCA;
            }
            if (containsAny(name, "mela", "mele", "banana", "banane", "arancia", "pera", "pere", "kiwi", "uva", "fragole", "pesca", "albicocca", "ananas", "frutta")) {
                return appCategory.FRUTTA_FRESCA;
            }
            if (containsAny(name, "insalata", "pomodoro", "zucchine", "melanzane", "spinaci", "verdura", "carote", "cipolla", "peperoni", "broccoletti", "minestrone", "bietole")) {
                return appCategory.VERDURA;
            }

            // --- CONDIMENTI ---
            if (containsAny(name, "olio", "burro", "margarina", "strutto")) {
                return appCategory.OLIO_E_GRASSI;
            }
            if (containsAny(name, "maionese", "ketchup", "sugo", "pesto", "passata", "polpa")) {
                return appCategory.SALSE; // O CONDIMENTI se non hai SALSE
            }

            // --- BEVANDE ---
            if (containsAny(name, "acqua", "succo", "tè", "coca", "pepsi", "fanta", "vino", "birra")) {
                return appCategory.BEVANDE;
            }

            // Analisi categoria ottenuta dallo scraping, se l'analisi del nome non porta a nessun risultato

            if (containsAny(cat, "macelleria", "carne")) return appCategory.CARNE_ROSSA; // Default sicuro
            if (containsAny(cat, "pescheria", "ittico")) return appCategory.PESCE;
            if (containsAny(cat, "ortofrutta", "frutta e verdura")) {
                return appCategory.VERDURA;
            }
            if (containsAny(cat, "panetteria", "pane")) return appCategory.PANE_E_SOSTITUTI;
            if (containsAny(cat, "latticini", "formaggi")) return appCategory.FORMAGGI;

            return appCategory.SCONOSCIUTO;
        }

        private static boolean containsAny(String text, String... keywords) {
            for (String keyword : keywords) {
                if (text.contains(keyword)) {
                    return true;
                }
            }
            return false;
        }
}
