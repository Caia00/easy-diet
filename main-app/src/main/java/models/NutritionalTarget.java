package models;

public class NutritionalTarget {

        private appCategory category;
        private nutritionalValues nutritionalValues;


        public NutritionalTarget(appCategory category, double targetKcal, double targetProteins,
                                 double targetCarbs, double targetFats, double targetFibers, double targetSugar) {
            this.nutritionalValues = new nutritionalValues(targetKcal, targetCarbs, targetSugar, targetFats, targetFibers, targetProteins);
            this.category = category;
        }

        public appCategory getCategory() {
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

}
