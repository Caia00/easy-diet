package view.cli;

import controller.DietManagerController;
import models.DietPlan;
import view.DietManagerView;

import java.util.List;
import java.util.Scanner;

public class CliDietManagerView implements DietManagerView {

    private DietManagerController controller;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isRunning = true;

    private List<DietPlan> currentList;

    @Override
    public void setController(DietManagerController controller) {
        this.controller = controller;
    }

    @Override
    public void showDietList(List<DietPlan> summaries) {
        this.currentList = summaries;
        System.out.println("\n--- GESTIONE DIETE: ELENCO ---");
        if (summaries.isEmpty()) {
            System.out.println(" (Nessuna dieta presente)");
        } else {
            for (int i = 0; i < summaries.size(); i++) {
                System.out.printf("[%d] %s (ID: %d)\n", (i+1), summaries.get(i).getDietName(), summaries.get(i).getDietId());
            }
        }
        System.out.println("------------------------------");
    }

    @Override
    public void start(){
        while (isRunning) {
            System.out.println("\nAZIONI DISPONIBILI:");
            System.out.println(" [C] Crea Nuova Dieta");
            System.out.println(" [N] Seleziona Dieta per Modifica/Assegnazione (es. scrivi '1')");
            System.out.println(" [0] Indietro (Torna alla Dashboard)");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                controller.back();
            }
            else if (input.equals("C")) {
                System.out.print("Nome nuova dieta: ");
                controller.createDiet(scanner.nextLine());
            }
            else {
                //Gestione selezione numerica
                try {
                    int index = Integer.parseInt(input) - 1;
                    if (currentList != null && index >= 0 && index < currentList.size()) {
                        handleSelectedDiet(currentList.get(index));
                    } else {
                        System.out.println("Indice non valido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Comando non valido.");
                }
            }
        }
    }

    private void handleSelectedDiet(DietPlan selected) {
        System.out.println("\nHai selezionato: " + selected.getDietName());
        System.out.println("1. Modifica Contenuto");
        System.out.println("2. Elimina");
        System.out.println("3. Assegna a Paziente");
        System.out.println("0. Annulla selezione");
        System.out.print("Cosa vuoi fare? ");

        String subChoice = scanner.nextLine();
        switch (subChoice) {
            case "1": controller.editDiet(selected); break;
            case "2":
                System.out.print("Sicuro? (s/n): ");
                if(scanner.nextLine().equalsIgnoreCase("s")) controller.deleteDiet(selected);
                break;
            case "3":
                System.out.print("Email del paziente: ");
                controller.assignDiet(selected, scanner.nextLine());
                break;
            case "0": break;
            default: System.out.println("Azione non valida.");
        }
    }

    @Override
    public void showMessage(String message) {
        System.out.println("[OK] " + message);
    }

    @Override
    public void showError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public void close(){
        isRunning = false;
    }


}
