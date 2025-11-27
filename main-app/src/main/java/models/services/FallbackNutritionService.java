package models.services;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.NutritionalValues;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FallbackNutritionService {
    private static FallbackNutritionService instance; //Istanza statica privata per pattern singleton

    private List<StandardFood> database;

    private FallbackNutritionService() { //Costruttore privato per pattern singleton
        loadDatabase();
    }

    public static FallbackNutritionService getInstance() { //Metodo per ottenere istanza
        if (instance == null) {
            instance = new FallbackNutritionService();
        }
        return instance;
    }

    private void loadDatabase() { //Metodo per caricamento prodotti fallback da file .json sfruttando jackson
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = getClass().getResourceAsStream("/fallbackData.json")) {
            if (inputStream == null) {
                throw new RuntimeException("ERRORE: Impossibile trovare il file fallbackData.json!");
            }

            this.database = mapper.readValue(inputStream, new TypeReference<List<StandardFood>>(){});

            System.out.println("Fallback Database caricato con successo: " + database.size() + " alimenti.");

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Errore nel caricamento del database di fallback");
        }
    }

    public NutritionalValues findByProductName(String commercialProductName) { //Ricerca prodotto reale in fallback database via keywords
        if (commercialProductName == null || database == null) return null;

        String cleanedName = normalizeName(commercialProductName);

        System.out.println("DEBUG: Cerco fallback per: '" + cleanedName + "'");

        for (StandardFood standardFood : database) {
            if (standardFood.getKeywords() != null) {
                for (String keyword : standardFood.getKeywords()) {

                    String lowerKeyword = keyword.toLowerCase();

                    if (cleanedName.contains(lowerKeyword)) {
                        return standardFood.getValues();
                    }
                }
            }
        }
        return null;
    }

    private String normalizeName(String input) { //Metodo per normalizzare il nome reale del prodotto in modo che la ricerca sia più efficiente
        if (input == null) return "";

        String spacedName = input.replaceAll("([a-z])([A-Z])", "$1 $2");

        spacedName = spacedName.replace("_", " ").replace("-", " ");

        return spacedName.trim().toLowerCase();
    }



    public static class StandardFood { //Classe interna utilizzata per la gestione dei prodotti trovati nel fallback database
        private String nome;
        private NutritionalValues values;
        private List<String> keywords;

        public StandardFood() {}

        public String getName() { return nome; }
        public void setName(String name) { this.nome = name; }

        public NutritionalValues getValues() { return values; }
        public void setValues(NutritionalValues values) { this.values = values; }

        public List<String> getKeywords() { return keywords; }
        public void setKeywords(List<String> keywords) { this.keywords = keywords; }
    }



}
