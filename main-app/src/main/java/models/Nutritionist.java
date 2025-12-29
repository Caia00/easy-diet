package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Nutritionist extends Profile{
    private String professionalCode;
    private List<DietPlan> dietTemplates;

    public Nutritionist() {
        super();
    }

    public Nutritionist(String name, String surname, String email, String password, LocalDate birthDate,
                        String professionalCode) {
        super(name, surname, email, password, birthDate);
        this.professionalCode = professionalCode;
        this.dietTemplates = new ArrayList<>();
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


    public String getProfessionalCode() {
        return professionalCode;
    }
    public void setProfessionalCode(String professionalCode) {
        this.professionalCode = professionalCode;
    }

    public List<DietPlan> getDietTemplates(){
        return dietTemplates;
    }
    public void setDietTemplates(List<DietPlan> dietTemplates) {
        this.dietTemplates = dietTemplates;
    }

    @Override
    public String getRole() {
        return "NUTRITIONIST";
    }

    @Override
    public String toString() {
        return String.format("Dr. %s %s (%s) - Albo: %s",
                name, surname, email, professionalCode);
    }
}
