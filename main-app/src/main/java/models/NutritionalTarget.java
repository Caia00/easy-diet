package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NutritionalTarget {

        private AppCategory category;
        private NutritionalValues nutritionalValues = new NutritionalValues();

        public NutritionalTarget(){}

        public NutritionalTarget(AppCategory category, double targetKcal, double targetProteins,
                                 double targetCarbs, double targetFats, double targetFibers, double targetSugar) {
            this.nutritionalValues = new NutritionalValues(targetKcal, targetCarbs, targetSugar, targetFats, targetFibers, targetProteins);
            this.category = category;
        }

        public AppCategory getCategory() {
            return category;
        }
        public void setCategory(AppCategory category) {
            this.category = category;
        }

        public double getTargetKcal() {
            return nutritionalValues.getKcal();
        }
        public void setTargetKcal(double targetKcal) {
            nutritionalValues.setKcal(targetKcal);
        }

        public double getTargetProteins() {
            return nutritionalValues.getProteins();
        }
        public void setTargetProteins(double targetProteins) {
            nutritionalValues.setProteins(targetProteins);
        }

        public double getTargetCarbs() {
            return nutritionalValues.getCarbs();
        }
        public void setTargetCarbs(double targetCarbs) {
            nutritionalValues.setCarbs(targetCarbs);
        }

        public double getTargetFats() {
            return nutritionalValues.getFats();
        }
        public void setTargetFats(double targetFats) {
            nutritionalValues.setFats(targetFats);
        }

        public double getTargetFibers() {
            return nutritionalValues.getFibers();
        }
        public void setTargetFibers(double targetFibers) {
            nutritionalValues.setFibers(targetFibers);
        }

        public double getTargetSugar() {
            return nutritionalValues.getSugar();
        }
        public void setTargetSugar(double targetSugar) {
            nutritionalValues.setSugar(targetSugar);
        }

    @Override
    public String toString() {
        return String.format("Target %s: %.0f kcal (P:%.1f C:%.1f S:%.1f F:%.1f FIB:%.1f) )",
                category, getTargetKcal(), getTargetProteins(), getTargetCarbs(), getTargetSugar(), getTargetFats(), getTargetFibers());
    }

}
