package models.dao;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import models.DietPlan;
import models.Profile;
import models.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemProfileDAO implements ProfileDAO {

    private static final Logger logger = Logger.getLogger(FileSystemProfileDAO.class.getName());

    private final File file;
    private final ObjectMapper mapper;

    public FileSystemProfileDAO(String filePath) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper();

        //Configurazione del mapper
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jdk8.Jdk8Module());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ensureFileExists();
    }

    @Override
    public void save(Profile profile) {
        if (profile == null) return;

        List<Profile> allProfiles = loadAll();

        //Rimuovo vecchio (logica update)
        allProfiles.removeIf(p -> p.getEmail().equalsIgnoreCase(profile.getEmail()));

        //Aggiungo nuovo
        allProfiles.add(profile);
        saveAll(allProfiles);
    }

    @Override
    public Profile findByEmail(String email) {
        if (email == null) return null;

        List<Profile> allProfiles = loadAll();

        return allProfiles.stream()
                .filter(p -> p.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean assignDiet(String patientEmail, DietPlan plan) {
        Profile profile = findByEmail(patientEmail);

        if (profile == null) {
            logger.warning(() -> "Impossibile assegnare dieta: Paziente non trovato " + patientEmail);
            return false;
        }


        if (profile instanceof User patient) {
            patient.assignDiet(plan);
            save(patient);
            return true;
        } else {
            logger.warning("Errore: L'email appartiene a un Nutrizionista, non a un Paziente.");
            return false;
        }
    }

    @Override
    public void delete(String email) {
        if (email == null) return;

        List<Profile> allProfiles = loadAll();

        boolean removed = allProfiles.removeIf(p -> p.getEmail().equalsIgnoreCase(email));

        if (removed) {
            saveAll(allProfiles);
        }
    }


    private List<Profile> loadAll() {
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(file, new TypeReference<List<Profile>>(){});
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore critico durante la lettura del file profili", e);
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Profile> profiles) {
        try {
            mapper.writeValue(file, profiles);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore critico durante il salvataggio su file", e);
        }
    }

    private void ensureFileExists() {
        try {
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if(file.createNewFile()){
                    logger.info("File " + file.getName() + " creato");
                }
                saveAll(new ArrayList<>());
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e, () -> "Impossibile creare il file DB: " + file.getAbsolutePath());
        }
    }

}
