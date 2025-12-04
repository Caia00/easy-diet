package models;

public class NutritionalTarget {

        private AppCategory category;
        private NutritionalValues nutritionalValues;


        public NutritionalTarget(AppCategory category, double targetKcal, double targetProteins,
                                 double targetCarbs, double targetFats, double targetFibers, double targetSugar) {
            this.nutritionalValues = new NutritionalValues(targetKcal, targetCarbs, targetSugar, targetFats, targetFibers, targetProteins);
            this.category = category;
        }

        public AppCategory getCategory() {
            return category;
        }

        public double getTargetKcal() {
            return nutritionalValues.getKcal();
        }

        public double getTargetProteins() {
            return nutritionalValues.getProteins();
        }

        public double getTargetCarbs() {
            return nutritionalValues.getCarbs();
        }

        public double getTargetFats() {
            return nutritionalValues.getFats();
        }

        public double getTargetFibers() {
            return nutritionalValues.getFibers();
        }

        public double getTargetSugar() {
            return nutritionalValues.getSugar();
        }

    @Override
    public String toString() {
        return String.format("Target %s: %.0f kcal (P:%.1f C:%.1f S:%.1f F:%.1f FIB:%.1f) )",
                category, getTargetKcal(), getTargetProteins(), getTargetCarbs(), getTargetSugar(), getTargetFats(), getTargetFibers());
    }

}
