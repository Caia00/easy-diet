package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends Profile{
    private String professionalCode;
    private List<User> managedPatients;
    private List<DietPlan> dietTemplates;

    public Nutritionist(String name, String surname, String email, String password, LocalDate birthDate,
                        String professionalCode) {
        super(name, surname, email, password, birthDate);
        this.professionalCode = professionalCode;
        this.managedPatients = new ArrayList<>();
        this.dietTemplates = new ArrayList<>();
    }


    public void addPatient(User patient) {
        if (!managedPatients.contains(patient)) {
            managedPatients.add(patient);
        }
    }

    public List<User> getManagedPatients() {
        return managedPatients;
    }

    public DietPlan createDietTemplate(String dietName) {
        // Controllo se esiste già per evitare duplicati
        if (getDietTemplateByName(dietName) != null) {
            throw new IllegalArgumentException("Esiste già una dieta con questo nome!");
        }

        DietPlan newDiet = new DietPlan(dietName);
        this.dietTemplates.add(newDiet);
        return newDiet; // La ritorno così il controller può aprirla subito per l'editing
    }


    public DietPlan getDietTemplateByName(String dietName) {
        for (DietPlan diet : dietTemplates) {
            if (diet.getDietName().equalsIgnoreCase(dietName)) {
                return diet;
            }
        }
        return null;
    }

    public void deleteDietTemplate(String dietName) {
        dietTemplates.removeIf(d -> d.getDietName().equalsIgnoreCase(dietName));
    }

    public List<DietPlan> getAllDietTemplates() {
        return dietTemplates;
    }

    public void assignDietToPatient(User patient, String dietName) {

        if (!managedPatients.contains(patient)) {
            throw new IllegalArgumentException("Paziente non gestito da questo nutrizionista.");
        }

        DietPlan template = getDietTemplateByName(dietName);
        if (template == null) {
            throw new IllegalArgumentException("Dieta non trovata nell'archivio.");
        }

        patient.assignDiet(template);

        System.out.println("Dieta '" + dietName + "' assegnata a " + patient.getSurname());
    }

    @Override
    public String getRole() {
        return "NUTRITIONIST";
    }
}
