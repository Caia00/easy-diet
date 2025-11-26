package models;

public class NutritionalTarget {

        private appCategory category;

        private double targetKcal;
        private double targetProteins;
        private double targetCarbs;
        private double targetFats;
        private double targetFibers;
        private double targetSugar;

        public NutritionalTarget(appCategory category, double targetKcal, double targetProteins,
                                 double targetCarbs, double targetFats, double targetFibers, double targetSugar) {
            this.category = category;
            this.targetKcal = targetKcal;
            this.targetProteins = targetProteins;
            this.targetCarbs = targetCarbs;
            this.targetFats = targetFats;
            this.targetFibers = targetFibers;
            this.targetSugar = targetSugar;
        }

        public appCategory getCategory() {
            return category;
        }

        public double getTargetKcal() {
            return targetKcal;
        }

        public double getTargetProteins() {
            return targetProteins;
        }

        public double getTargetCarbs() {
            return targetCarbs;
        }

        public double getTargetFats() {
            return targetFats;
        }

        public double getTargetFibers() {
            return targetFibers;
        }

        public double getTargetSugar() {
            return targetSugar;
        }

}
