import java.io.*;
import java.util.*;

public class ListManager {

    private static List<String> itemList = new ArrayList<>();
    private static boolean needsToBeSaved = false; // dirty flag
    private static String currentFileName = null;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            showMenu();
            String choice = scanner.nextLine().trim().toUpperCase();

            switch (choice) {
                case "A": addItem(); break;
                case "D": deleteItem(); break;
                case "I": insertItem(); break;
                case "M": moveItem(); break;
                case "C": clearList(); break;
                case "O": openFile(); break;
                case "S": saveFile(); break;
                case "V": viewList(); break;
                case "Q": quitProgram(); return;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n--- List Manager Menu ---");
        System.out.println("A - Add item");
        System.out.println("D - Delete item");
        System.out.println("I - Insert item");
        System.out.println("M - Move item");
        System.out.println("C - Clear list");
        System.out.println("O - Open list from file");
        System.out.println("S - Save list to file");
        System.out.println("V - View list");
        System.out.println("Q - Quit program");
        System.out.print("Select option: ");
    }

    private static void addItem() {
        System.out.print("Enter item to add: ");
        String item = scanner.nextLine();
        itemList.add(item);
        markDirty();
    }

    private static void deleteItem() {
        if (itemList.isEmpty()) {
            System.out.println("List is empty.");
            return;
        }
        viewList();
        System.out.print("Enter index to delete: ");
        int index = getIntInput();
        if (index >= 0 && index < itemList.size()) {
            itemList.remove(index);
            markDirty();
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void insertItem() {
        System.out.print("Enter item to insert: ");
        String item = scanner.nextLine();
        System.out.print("Enter index to insert at: ");
        int index = getIntInput();
        if (index >= 0 && index <= itemList.size()) {
            itemList.add(index, item);
            markDirty();
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void moveItem() {
        if (itemList.size() < 2) {
            System.out.println("Not enough items to move.");
            return;
        }
        viewList();
        System.out.print("Enter index of item to move: ");
        int from = getIntInput();
        System.out.print("Enter new index: ");
        int to = getIntInput();
        if (from >= 0 && from < itemList.size() && to >= 0 && to <= itemList.size()) {
            String item = itemList.remove(from);
            if (from < to) to--; // Adjust if shifting forward
            itemList.add(to, item);
            markDirty();
        } else {
            System.out.println("Invalid index.");
        }
    }

    private static void clearList() {
        itemList.clear();
        markDirty();
        System.out.println("List cleared.");
    }

    private static void openFile() {
        if (needsToBeSaved) {
            promptSave("You have unsaved changes. Save before opening a new file?");
        }

        System.out.print("Enter filename to open (without .txt): ");
        String fileName = scanner.nextLine().trim() + ".txt";
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("File not found.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            itemList.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                itemList.add(line);
            }
            currentFileName = fileName;
            needsToBeSaved = false;
            System.out.println("List loaded from " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading file.");
        }
    }

    private static void saveFile() {
        if (currentFileName == null) {
            System.out.print("Enter filename to save as (without .txt): ");
            currentFileName = scanner.nextLine().trim() + ".txt";
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFileName))) {
            for (String item : itemList) {
                writer.println(item);
            }
            needsToBeSaved = false;
            System.out.println("List saved to " + currentFileName);
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    private static void viewList() {
        if (itemList.isEmpty()) {
            System.out.println("[List is empty]");
        } else {
            System.out.println("\n--- Current List ---");
            for (int i = 0; i < itemList.size(); i++) {
                System.out.println(i + ": " + itemList.get(i));
            }
        }
    }

    private static void quitProgram() {
        if (needsToBeSaved) {
            promptSave("You have unsaved changes. Save before quitting?");
        }
        System.out.println("Exiting program.");
    }

    private static void promptSave(String message) {
        System.out.print(message + " (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        if (choice.equals("Y")) {
            saveFile();
        }
    }

    private static void markDirty() {
        needsToBeSaved = true;
    }

    private static int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
