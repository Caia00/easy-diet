package view;

import controller.ShoppingListManagerController;
import models.ShoppingList;
import models.SupermarketName;

import java.util.List;
import java.util.Scanner;

public class CliShoppingListManagerView implements ShoppingListManagerView{
    private ShoppingListManagerController controller;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isRunning = true;
    private List<ShoppingList> currentHistory;

    @Override
    public void setController(ShoppingListManagerController controller) {
        this.controller = controller;
    }

    @Override
    public void showShoppingHistory(List<ShoppingList> history) {
        this.currentHistory = history;
        System.out.println("\n=== LE TUE LISTE DELLA SPESA ===");
        if (history.isEmpty()) {
            System.out.println(" (Nessuna lista salvata)");
        } else {
            for (int i = 0; i < history.size(); i++) {
                ShoppingList l = history.get(i);
                System.out.printf("[%d] %s (%s) - Tot: €%.2f\n",
                        (i+1), l.getListName(), l.getSupermarket(), l.getTotalCost());
            }
        }
        System.out.println("--------------------------------");
    }

    @Override
    public void showListDetails(ShoppingList list) {
        System.out.println("\n============================================");
        System.out.printf(" DETTAGLIO LISTA: %s \n", list.getListName().toUpperCase());
        System.out.printf(" Supermercato: %s  -  Data: %s\n", list.getSupermarket(), list.getCreationDate());
        System.out.println("============================================");

        if (list.getItems().isEmpty()) {
            System.out.println(" (Lista vuota)");
        } else {
            System.out.printf(" %-4s | %-25s | %-10s | %s\n", "Q.tà", "Prodotto", "Prezzo", "Tipo");
            System.out.println("----------------------------------------------------------");

            for (var item : list.getItems()) {
                String type = item.isForDiet() ? "(DIETA)" : "(EXTRA)";
                System.out.printf(" %-4dx | %-25s | €%-9.2f | %s\n",
                        item.getQuantity(),
                        truncate(item.getProduct().getName(), 25),
                        item.getTotalPrice(),
                        type);
            }
        }

        System.out.println("----------------------------------------------------------");
        System.out.printf(" TOTALE COMPLESSIVO: €%.2f\n", list.getTotalCost());
        System.out.println("============================================");

        System.out.println("\nPremi INVIO per tornare all'elenco...");
        scanner.nextLine();
    }

    private String truncate(String str, int width) {
        if (str.length() > width) {
            return str.substring(0, width - 3) + "...";
        }
        return str;
    }

    @Override
    public void start() {
        while (isRunning) {
            System.out.println("\nAZIONI:");
            System.out.println(" [C] Crea Nuova Lista");
            System.out.println(" [N] Seleziona Lista (es. '1') per Aprire/Eliminare");
            System.out.println(" [0] Indietro (Dashboard)");
            System.out.print("> ");

            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                controller.back();
            } else if (input.equals("C")) {
                handleCreate();
            } else {
                try {
                    int index = Integer.parseInt(input) - 1;
                    if (currentHistory != null && index >= 0 && index < currentHistory.size()) {
                        handleSelectedList(currentHistory.get(index));
                    } else {
                        System.out.println("Numero lista non valido.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Comando non valido.");
                }
            }
        }
    }

    private void handleCreate() {
        System.out.println("\n--- NUOVA LISTA ---");
        System.out.print("Nome lista (es. Spesa Lunedì): ");
        String name = scanner.nextLine();

        System.out.println("Scegli Supermercato:");
        SupermarketName[] markets = SupermarketName.values();
        for (int i = 0; i < markets.length; i++) {
            System.out.println((i+1) + ". " + markets[i]);
        }
        System.out.print("Numero: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < markets.length) {
                controller.createNewList(name, markets[idx]);
            } else {
                System.out.println("Supermercato non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero valido.");
        }
    }

    private void handleSelectedList(ShoppingList list) {
        System.out.println("\nSelezionato: " + list.getListName());
        System.out.println("1. Apri / Modifica");
        System.out.println("2. Elimina");
        System.out.println("0. Annulla");
        System.out.print("> ");

        String sub = scanner.nextLine();
        switch (sub) {
            case "1": controller.openList(list); break;
            case "2":
                System.out.print("Sicuro di voler eliminare? (s/n): ");
                if(scanner.nextLine().equalsIgnoreCase("s")) controller.deleteList(list);
                break;
            case "0": break;
        }
    }

    @Override
    public void showMessage(String msg) { System.out.println("[OK] " + msg); }
    @Override
    public void showError(String err) { System.err.println("[ERR] " + err); }
    @Override
    public void close() { isRunning = false; }
}
