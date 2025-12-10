package view;

import controller.ShoppingListEditorController;
import models.AppCategory;
import models.CommercialProduct;
import models.ShoppingItem;
import models.ShoppingList;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CliShoppingListEditorView implements ShoppingListEditorView{
    private ShoppingListEditorController controller;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isEditing = true;

    @Override
    public void setController(ShoppingListEditorController controller) {
        this.controller = controller;
    }

    @Override
    public void showCurrentList(ShoppingList list) {
        System.out.println("\n=== LISTA: " + list.getListName() + " (" + list.getSupermarket() + ") ===");
        if (list.getItems().isEmpty()) {
            System.out.println(" (Carrello vuoto)");
        } else {
            for (ShoppingItem item : list.getItems()) {
                System.out.printf(" - %dx %s (€%.2f)\n", item.getQuantity(), item.getProduct().getName(), item.getTotalPrice());
            }
        }
        System.out.printf(" TOTALE STIMATO: €%.2f\n", list.getTotalCost());
        System.out.println("============================================");
    }

    @Override
    public void showDietStatus(Map<AppCategory, String> demandsStatus) {
        System.out.println("STATO COPERTURA DIETA:");
        if (demandsStatus.isEmpty()) {
            System.out.println(" (Nessuna richiesta specifica o dieta non presente)");
        } else {
            int count = 0;
            for (var entry : demandsStatus.entrySet()) {
                System.out.printf(" %-18s [%s]   ", entry.getKey(), entry.getValue());
                count++;
                if (count % 2 == 0) System.out.println();
            }
            if (count % 2 != 0) System.out.println();
        }
        System.out.println("--------------------------------------------");
    }

    @Override
    public void start() {
        while (isEditing) {
            System.out.println("\nAZIONI:");
            System.out.println("1. Aggiungi Prodotto (Scegli Categoria)");
            System.out.println("2. Rimuovi Prodotto dalla Lista");
            System.out.println("9. SALVA E ESCI");
            System.out.print("> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    handleCategorySelection();
                    break;
                case "2":
                    handleRemoveItem();
                    break;
                case "9":
                    controller.saveAndExit();
            }
        }
    }

    private void handleCategorySelection() {
        System.out.println("\n--- SCEGLI CATEGORIA ---");
        AppCategory[] cats = AppCategory.values();
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("%d. %s\n", (i+1), cats[i]);
        }
        System.out.print("Numero Categoria: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx >= 0 && idx < cats.length) {
                controller.selectCategory(cats[idx]);
            } else {
                System.out.println("Numero non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Inserisci un numero.");
        }
    }

    private void handleRemoveItem() {
        System.out.println("\n--- RIMUOVI/RIDUCI PRODOTTO ---");

        List<ShoppingItem> items = controller.getCurrentList().getItems();

        for (int i = 0; i < items.size(); i++) {
            ShoppingItem item = items.get(i);
            System.out.printf("%d. %s (Qta: %d)\n", (i+1), item.getProduct().getName(), item.getQuantity());
        }

        System.out.print("Numero prodotto da modificare (0 annulla): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;

            if (idx == -1) return;

            if (idx >= 0 && idx < items.size()) {
                ShoppingItem selected = items.get(idx);

                System.out.printf("Ne hai %d nel carrello. Quanti ne vuoi togliere? (Invio = Tutti): ", selected.getQuantity());
                String qtyStr = scanner.nextLine();

                int qtyToRemove;
                if (qtyStr.trim().isEmpty()) {
                    qtyToRemove = selected.getQuantity();
                } else {
                    qtyToRemove = Integer.parseInt(qtyStr);
                }

                if (qtyToRemove > 0) {
                    controller.removeProduct(idx, qtyToRemove);
                } else {
                    System.out.println("Quantità non valida.");
                }

            } else {
                System.out.println("Indice non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
        }
    }

    @Override
    public void showCatalogProducts(AppCategory category, List<CommercialProduct> products) {
        System.out.println("\n--- PRODOTTI DISPONIBILI: " + category + " ---");
        for (int i = 0; i < products.size(); i++) {
            CommercialProduct p = products.get(i);
            System.out.printf("%d. %s (%.0fg) - €%.2f\n", (i+1), p.getName(), p.getWeightInGrams(), p.getPrice());
        }
        System.out.println("0. Annulla (Torna indietro)");
        System.out.print("Scegli prodotto: ");

        try {
            int idx = Integer.parseInt(scanner.nextLine()) - 1;
            if (idx == -1) return;

            if (idx >= 0 && idx < products.size()) {
                controller.selectProduct(products.get(idx));
            } else {
                System.out.println("Prodotto non valido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input non valido.");
        }
    }

    @Override
    public int askQuantityWithAdvice(CommercialProduct product, String suggestionMsg, int suggestedQty) {
        System.out.println("\n>>> SELEZIONATO: " + product.getName());
        System.out.println(suggestionMsg);
        System.out.print("Quante confezioni vuoi acquistare? [" + suggestedQty + ", INVIO per accettare]: ");

        String input = scanner.nextLine();
        if (input.trim().isEmpty()) {
            return suggestedQty;
        }

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido, annullato.");
            return 0;
        }
    }

    @Override
    public boolean showUnmetDemandsWarning(List<String> missingCategories) {
        System.out.println("\nATTENZIONE: Hai ancora richieste dietetiche non soddisfatte per:");
        System.out.println(missingCategories);
        System.out.print("Vuoi salvare comunque? (s/n): ");
        return scanner.nextLine().equalsIgnoreCase("s");
    }

    @Override
    public void showMessage(String msg) { System.out.println("[OK] " + msg); }
    @Override
    public void showError(String err) { System.err.println("[ERR] " + err); }
    @Override
    public void close() { isEditing = false; }
}
