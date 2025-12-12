package view.cli;

import controller.NutritionistHomeController;
import view.NutritionistHomeView;

import java.util.Scanner;

public class CliNutritionistHomeView implements NutritionistHomeView {
    private NutritionistHomeController controller;
    private Scanner scanner;
    private boolean isRunning;


    public CliNutritionistHomeView() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    @Override
    public void setController(NutritionistHomeController controller) {
        this.controller = controller;
    }

    @Override
    public void showWelcome(String doctorName) {
        System.out.println("\n==========================================");
        System.out.println("   DASHBOARD NUTRIZIONISTA: Dr. " + doctorName);
        System.out.println("==========================================");
    }


    @Override
    public void start() {
        while (isRunning) {
            System.out.println("\n=== DASHBOARD PRINCIPALE ===");
            System.out.println("1. Gestione Diete (Crea/Modifica/Elimina/Assegna)");
            System.out.println("2. Il mio Profilo");
            System.out.println("0. Logout");
            System.out.print("Scegli: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1": controller.goToDietManager(); break;
                case "2": controller.goToProfile(); break;
                case "0": controller.logout(); break;
                default: System.out.println("Scelta non valida.");
            }
        }
    }

    @Override
    public void close() { isRunning = false; }
}
