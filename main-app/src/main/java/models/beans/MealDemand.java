package models.beans;

import models.NutritionalTarget;

public class MealDemand {

        private String dayOfWeek;   // es. "Lunedì"
        private String mealName;    // es. "Colazione"

        private NutritionalTarget target;

        public MealDemand(String dayOfWeek, String mealName, NutritionalTarget target) {
            this.dayOfWeek = dayOfWeek;
            this.mealName = mealName;
            this.target = target;
        }

        public String getDayOfWeek() { return dayOfWeek; }
        public String getMealName() { return mealName; }
        public NutritionalTarget getTarget() { return target; }

}
