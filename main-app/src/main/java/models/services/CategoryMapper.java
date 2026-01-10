package models.services;

import models.AppCategory;

public class CategoryMapper {
        private CategoryMapper() {
            //Costruttore privato usato solo per nascondere quello pubblico implicito
        }

    public static AppCategory map(String rawName, String rawCategory) {
        String name = (rawName != null) ? rawName.toLowerCase() : "";
        String cat = (rawCategory != null) ? rawCategory.toLowerCase() : "";

        AppCategory result;

        //Interroghiamo i vari metodi in ordine
        if ((result = checkCarbs(name)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkBreakfastAndSweets(name)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkDairyAndEggs(name, cat)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkProteins(name)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkProduce(name)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkCondiments(name)) != AppCategory.SCONOSCIUTO) return result;
        if ((result = checkBeverages(name)) != AppCategory.SCONOSCIUTO) return result;

        //Se il mapping del nome fallisce, proviamo con la categoria dello scraping
        return mapByScrapedCategory(cat);
    }

    private static AppCategory checkCarbs(String name) {
        if (containsAny(name, "pasta", "spaghetti", "penne", "fusilli", "rigatoni", "maccheroni", "farfalle", "linguine", "orecchiette", "mezze maniche"))
            return AppCategory.PASTA;
        if (containsAny(name, "riso", "basmati", "carnaroli", "arborio", "farro", "orzo", "couscous", "quinoa", "grano", "cereali mix"))
            return AppCategory.RISO_E_CEREALI;
        if (containsAny(name, "patate", "patata")) {
            if (name.contains("fritte") || name.contains("chips") || name.contains("patatine")) return AppCategory.DOLCI_E_SNACK;
            return AppCategory.PATATE_E_TUBERI;
        }
        if (containsAny(name, "pane", "pagnotta", "ciabatta", "bauletto", "cracker", "grissini", "taralli", "gallette", "piadina", "focaccia"))
            return AppCategory.PANE_E_SOSTITUTI;
        if (containsAny(name, "farina", "semola", "amido"))
            return AppCategory.FARINE;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkBreakfastAndSweets(String name) {
        if (containsAny(name, "biscotti", "frollini", "gocciole", "pan di stelle", "croissant", "brioche", "merendina", "plumcake", "fette biscottate"))
            return AppCategory.BISCOTTI_E_DOLCI_COLAZIONE;
        if (containsAny(name, "corn flakes", "muesli", "fiocchi", "avena", "cereali croccanti", "kellogg"))
            return AppCategory.CEREALI_COLAZIONE;
        if (containsAny(name, "cioccolato", "barretta", "snack", "pop corn", "patatine", "wafer", "caramelle"))
            return AppCategory.DOLCI_E_SNACK;
        if (containsAny(name, "marmellata", "confettura", "miele", "crema nocciole", "nutella", "burro d'arachidi"))
            return AppCategory.CREME_SPALMABILI;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkDairyAndEggs(String name, String cat) {
        if (containsAny(name, "uova", "albume", "tuorlo")) return AppCategory.UOVA;

        // Bevande vegetali (precedenza su latte generico)
        if (containsAny(name, "bevanda", "soia", "avena", "mandorla", "riso") && name.contains("latte"))
            return AppCategory.LATTE_E_BEVANDE_VEG;

        if (containsAny(name, "latte")) {
            if (cat.contains("igiene") || cat.contains("corpo")) return AppCategory.SCONOSCIUTO;
            return AppCategory.LATTE_E_BEVANDE_VEG;
        }

        if (containsAny(name, "yogurt", "kefir", "yoghurt")) return AppCategory.YOGURT_E_FERMENTATI;
        if (containsAny(name, "formaggio", "mozzarella", "grana", "parmigiano", "pecorino", "stracchino", "ricotta", "mascarpone", "provola", "scamorza", "gorgonzola"))
            return AppCategory.FORMAGGI;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkProteins(String name) {
        if (containsAny(name, "pollo", "tacchino", "coniglio", "faraona", "aia")) return AppCategory.CARNE_BIANCA;
        if (containsAny(name, "manzo", "bovino", "vitello", "scottona", "hamburger", "suino", "salsiccia", "macinato", "bistecca", "agnello"))
            return AppCategory.CARNE_ROSSA;
        if (containsAny(name, "prosciutto", "salame", "mortadella", "bresaola", "fesa", "speck", "pancetta", "wurstel"))
            return AppCategory.AFFETTATI_E_SALUMI;
        if (containsAny(name, "tonno", "salmone", "merluzzo", "nasello", "gamberi", "pesce", "orata", "branzino", "sogliola", "platessa", "calamari", "vongole", "cozze"))
            return AppCategory.PESCE;
        if (containsAny(name, "ceci", "fagioli", "lenticchie", "piselli", "soia", "edamame")) return AppCategory.LEGUMI;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkProduce(String name) {
        if (containsAny(name, "noci", "mandorle", "nocciole", "anacardi", "pistacchi", "arachidi")) return AppCategory.FRUTTA_SECCA;
        if (containsAny(name, "mela", "mele", "banana", "banane", "arancia", "pera", "pere", "kiwi", "uva", "fragole", "pesca", "albicocca", "ananas", "frutta"))
            return AppCategory.FRUTTA_FRESCA;
        if (containsAny(name, "insalata", "pomodoro", "zucchine", "melanzane", "spinaci", "verdura", "carote", "cipolla", "peperoni", "broccoletti", "minestrone", "bietole"))
            return AppCategory.VERDURA;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkCondiments(String name) {
        if (containsAny(name, "olio", "burro", "margarina", "strutto")) return AppCategory.OLIO_E_GRASSI;
        if (containsAny(name, "maionese", "ketchup", "sugo", "pesto", "passata", "polpa")) return AppCategory.SALSE;

        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory checkBeverages(String name) {
        if (containsAny(name, "acqua", "succo", "tè", "coca", "pepsi", "fanta", "vino", "birra")) return AppCategory.BEVANDE;
        return AppCategory.SCONOSCIUTO;
    }

    private static AppCategory mapByScrapedCategory(String cat) {
        if (containsAny(cat, "macelleria", "carne")) return AppCategory.CARNE_ROSSA;
        if (containsAny(cat, "pescheria", "ittico")) return AppCategory.PESCE;
        if (containsAny(cat, "ortofrutta", "frutta e verdura")) return AppCategory.VERDURA;
        if (containsAny(cat, "panetteria", "pane")) return AppCategory.PANE_E_SOSTITUTI;
        if (containsAny(cat, "latticini", "formaggi")) return AppCategory.FORMAGGI;

        return AppCategory.SCONOSCIUTO;
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
