package view;
import controller.LoginController;
import java.time.LocalDate;
import java.util.Scanner;

public class CliAuthView implements AuthView {
    private LoginController controller;
    private final Scanner scanner;
    private boolean isRunning;

    public CliAuthView() {
        this.scanner = new Scanner(System.in);
        this.isRunning = true;
    }

    @Override
    public void setController(LoginController controller) {
        this.controller = controller;
    }

    @Override
    public void start() {
        System.out.println("=== BENVENUTO IN EASY DIET ===");

        while (isRunning) {
            System.out.println("\nCosa vuoi fare?");
            System.out.println("1. Accedi (Login)");
            System.out.println("2. Registrati come Paziente");
            System.out.println("3. Registrati come Nutrizionista");
            System.out.println("0. Esci");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleLoginInput();
                    break;
                case "2":
                    handlePatientRegisterInput();
                    break;
                case "3":
                    handleNutritionistRegisterInput();
                    break;
                case "0":
                    System.out.println("Arrivederci!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Scelta non valida.");
            }
        }
    }

    //Metodi per leggere e gestire l'input

    private void handleLoginInput() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String pwd = scanner.nextLine();

        controller.login(email, pwd);
    }

    private void handlePatientRegisterInput() {
        System.out.println("--- Registrazione Paziente ---");
        System.out.print("Nome: "); String name = scanner.nextLine();
        System.out.print("Cognome: "); String surname = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Password: "); String pwd = scanner.nextLine();

        System.out.print("Anno nascita (YYYY): "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Mese nascita (1-12): "); int month = Integer.parseInt(scanner.nextLine());
        System.out.print("Giorno nascita (1-31): "); int day = Integer.parseInt(scanner.nextLine());
        LocalDate birth = LocalDate.of(year, month, day);

        System.out.print("Altezza (cm): "); double h = Double.parseDouble(scanner.nextLine());
        System.out.print("Peso (kg): "); double w = Double.parseDouble(scanner.nextLine());
        System.out.print("Sesso (M/F): "); String gender = scanner.nextLine();

        controller.registerPatient(name, surname, email, pwd, birth, h, w, gender);
    }

    private void handleNutritionistRegisterInput() {
        System.out.println("--- Registrazione Nutrizionista ---");
        System.out.print("Nome: "); String name = scanner.nextLine();
        System.out.print("Cognome: "); String surname = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();
        System.out.print("Password: "); String pwd = scanner.nextLine();

        System.out.print("Anno nascita (YYYY): "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Mese nascita (1-12): "); int month = Integer.parseInt(scanner.nextLine());
        System.out.print("Giorno nascita (1-31): "); int day = Integer.parseInt(scanner.nextLine());
        LocalDate birth = LocalDate.of(year, month, day);

        System.out.print("Codice Albo: "); String albo = scanner.nextLine();

        controller.registerNutritionist(name, surname, email, pwd, birth, albo);
    }


    @Override
    public void showErrorMessage(String message) {
        System.err.println("[ERROR] " + message);
    }

    @Override
    public void showSuccessMessage(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    @Override
    public void close() {
        this.isRunning = false;
    }

}
