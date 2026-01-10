package view.cli;

import controller.*;
import view.PatientHomeView;

import java.util.Scanner;


public class CliPatientHomeView implements PatientHomeView {
    private PatientHomeController controller;
    private final Scanner scanner;
    private boolean isRunning;

    public CliPatientHomeView() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    @Override
    public void setController(PatientHomeController controller) {
        this.controller = controller;
    }

    @Override
    public void showWelcomeMessage(String userName) {
        System.out.println("\n==========================================");
        System.out.println("   BENTORNATO, " + userName.toUpperCase());
        System.out.println("==========================================");
    }

    @Override
    public void start() {
        while (isRunning) {
            System.out.println("\n--- MENU PAZIENTE ---");
            System.out.println("1. Visualizza la mia Dieta");
            System.out.println("2. Lista della Spesa");
            System.out.println("3. Meal Plan");
            System.out.println("4. Profilo");
            System.out.println("0. Esci (Logout)");
            System.out.print("Scegli un'opzione: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    controller.openMyDiet();
                    break;
                case "2":
                    controller.openShoppingList();
                    break;
                case "0":
                    controller.logout();
                    break;
                default:
                    System.out.println("Opzione non valida, riprova.");
            }
        }
    }

    @Override
    public void showErrorMessage(String message) {
        System.out.println("[ERROR] " + message);
    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    @Override
    public void close() {
        this.isRunning = false;
        System.out.println("Sessione chiusa.");
    }
}
