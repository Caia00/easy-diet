package view;

import controller.DietEditorController;
import models.AppCategory;

import java.util.Scanner;

public class CliDietEditorView implements DietEditorView {
    private DietEditorController controller;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isEditing = true;

    @Override
    public void setController(DietEditorController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        while (isEditing) {
            System.out.println("\n=== EDITOR DIETA: COMANDI ===");
            System.out.println("1. Aggiungi PASTO");
            System.out.println("2. Aggiungi CIBO a un pasto");
            System.out.println("3. Rimuovi PASTO intero");
            System.out.println("4. Rimuovi CIBO specifico");
            System.out.println("5. Rinomina Dieta");
            System.out.println("9. SALVA e ESCI");
            System.out.println("0. Annulla");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": handleAddMeal(); break;
                case "2": handleAddFood(); break;
                case "3": handleRemoveMeal(); break;
                case "4": handleRemoveFood(); break;
                case "5": handleRename(); break;
                case "9": controller.saveAndExit(); break;
                case "0":
                    System.out.println("Modifiche annullate.");
                    close();
                    break;
                default: System.out.println("Comando non valido.");
            }
        }
    }


    private void handleAddMeal() {
        System.out.print("Giorno (es. Lunedì): ");
        String day = scanner.nextLine();
        System.out.print("Nome Pasto (es. Pranzo): ");
        String name = scanner.nextLine();
        System.out.print("Orario (HH:mm, opzionale): ");
        String time = scanner.nextLine();

        controller.addMeal(day, name, time);
    }

    private void handleRemoveMeal() {
        System.out.print("Giorno: ");
        String day = scanner.nextLine();
        System.out.print("Nome Pasto da rimuovere: ");
        String name = scanner.nextLine();
        controller.removeMeal(day, name);
    }

    private void handleAddFood() {
        System.out.println("--- Aggiunta Cibo ---");
        System.out.print("Giorno: "); String day = scanner.nextLine();
        System.out.print("Pasto: "); String meal = scanner.nextLine();

        AppCategory cat = null;
        boolean validCategory = false;

        do {
            printAllCategories();
            System.out.print("Inserisci una Categoria tra quelle sopra: ");
            String catStr = scanner.nextLine().trim().toUpperCase();

            try {
                cat = AppCategory.valueOf(catStr);
                validCategory = true;
            } catch (IllegalArgumentException e) {
                System.out.println("ERRORE: Categoria non valida. Riprova.");
            }
        } while (!validCategory);

        System.out.print("Target Kcal: "); double kcal = readDouble();
        System.out.print("Target Proteine: "); double prot = readDouble();
        System.out.print("Target Carboidrati: "); double carb = readDouble();
        System.out.print("Target Zucchero: "); double sug = readDouble();
        System.out.print("Target Grassi: "); double fats = readDouble();
        System.out.print("Target Fibre: "); double fib = readDouble();

        System.out.print("Prodotto suggerito (Invio per nessuno): ");
        String sugg = scanner.nextLine();

        controller.addFoodItem(day, meal, cat, kcal, prot, carb, sug, fats, fib, sugg);
    }

    private void handleRemoveFood() {
        System.out.println("--- Rimuovi Alimento ---");
        System.out.print("Giorno: ");
        String day = scanner.nextLine();

        System.out.print("Nome pasto: ");
        String mealName = scanner.nextLine();

        System.out.print("Inserisci il numero dell'alimento da eliminare (es. 1, 2...): ");
        try {
            int index = Integer.parseInt(scanner.nextLine());

            controller.removeFoodItem(day, mealName, index - 1);

        } catch (NumberFormatException e) {
            System.out.println("Devi inserire un numero intero!");
        }
    }

    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void handleRename() {
        System.out.println("--- Rinomina Dieta ---");
        System.out.print("Inserisci il nuovo nome: ");
        String newName = scanner.nextLine();

        controller.renameDiet(newName);
    }

    private void printAllCategories() {
        System.out.println("--- Categorie Disponibili ---");
        AppCategory[] categories = AppCategory.values();

        int count = 0;
        for (AppCategory cat : categories) {
            System.out.print(cat.name() + "  ");

            count++;
            if (count % 4 == 0) {
                System.out.println();
            }
        }
        System.out.println("\n-----------------------------");
    }

    @Override
    public void showMessage(String msg) { System.out.println("[OK] " + msg); }
    @Override
    public void showError(String err) { System.err.println("[ERR] " + err); }
    @Override
    public void close() { isEditing = false; }
}
