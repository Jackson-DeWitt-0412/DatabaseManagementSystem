//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/11/2026
//Class Name: Main
//The main class that currently prompts the user to do all operations that will eventually be communicated through
//the database by local memory, adding, deleting, and otherwise interacting with books as needed.

import java.util.List;
import java.util.Scanner;

public class Main {
    private final BookManager bookManager;
    private final Scanner scanner;

    public Main() {
        bookManager = new BookManager();
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Main app = new Main();
        int exitCode = app.run();
        System.exit(exitCode);
    }

    public int run() {
        System.out.println("=== DMS: Book Database Manager (In-Memory) ===\n");
        boolean exit = false;
        while (!exit) {
            String menu = getMenuText();
            System.out.print(menu);
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    boolean added = handleAdd();
                    if (added) System.out.println("Book added successfully.");
                    else System.out.println("Failed to add book (duplicate ISBN or invalid input).");
                }
                case "2" -> {
                    boolean deleted = handleDelete();
                    if (deleted) System.out.println("Book deleted successfully.");
                    else System.out.println("Deletion failed or cancelled.");
                }
                case "3" -> {
                    boolean edited = handleEdit();
                    if (edited) System.out.println("Book updated successfully.");
                    else System.out.println("Update failed or cancelled.");
                }
                case "4" -> handleView();
                case "5" -> handleSort();
                case "6" -> {
                    int imported = handleImport();
                    if (imported >= 0) {
                        System.out.println("Imported " + imported + " book(s).");
                    } else {
                        System.out.println("Import failed (file not found, unreadable, or IO error).");
                    }
                }
                case "7" -> {
                    exit = true;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid option. Please try again.\n");
            }
        }
        scanner.close();
        return 0;
    }

    private String getMenuText() {
        return """
               \s
                --- Menu ---
                1. Add a book
                2. Delete a book
                3. Edit a book
                4. View all books
                5. Sort books (custom action)
                6. Import books from file
                7. Exit
                Choose an option:\s""";
    }

    // ----- Add -----
    private boolean handleAdd() {
        System.out.println("\n--- Add a New Book ---");
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new IllegalArgumentException("Title cannot be empty.");

            System.out.print("Author(s): ");
            String author = scanner.nextLine().trim();
            if (author.isEmpty()) throw new IllegalArgumentException("Author cannot be empty.");

            System.out.print("Type (e.g., Novel, Short Story): ");
            String type = scanner.nextLine().trim();
            if (type.isEmpty()) throw new IllegalArgumentException("Type cannot be empty.");

            System.out.print("Genre (e.g., Horror, Mystery): ");
            String genre = scanner.nextLine().trim();
            if (genre.isEmpty()) throw new IllegalArgumentException("Genre cannot be empty.");

            System.out.print("Year: ");
            int year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Pages: ");
            int pages = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("ISBN (13 digits): ");
            long isbn = Long.parseLong(scanner.nextLine().trim());

            Book book = new Book(title, author, type, genre, year, pages, isbn);
            return bookManager.addBook(book);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter integers for year, pages, and ISBN.");
            return false;
        } catch (IllegalArgumentException e) {
            System.out.println("Input error: " + e.getMessage());
            return false;
        }
    }

    // ----- Delete -----
    private boolean handleDelete() {
        System.out.println("\n--- Delete a Book ---");
        System.out.print("Enter ISBN of the book to delete: ");
        try {
            long isbn = Long.parseLong(scanner.nextLine());
            if (!bookManager.bookExists(isbn)) {
                System.out.println("No book found with that ISBN.");
                return false;
            }
            System.out.print("Are you sure? (y/n): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("y")) {
                return bookManager.deleteBook(isbn);
            } else {
                System.out.println("Deletion cancelled.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ISBN.");
            return false;
        }
    }

    // ----- Edit -----
    private boolean handleEdit() {
        System.out.println("\n--- Edit a Book ---");
        System.out.print("Enter ISBN of the book to edit: ");
        try {
            long isbn = Long.parseLong(scanner.nextLine());
            Book existing = bookManager.getBook(isbn);
            if (existing == null) {
                System.out.println("No book found with that ISBN.");
                return false;
            }

            System.out.println("Leave field blank to keep current value.");
            System.out.print("Title [" + existing.getTitle() + "]: ");
            String title = scanner.nextLine();
            if (title.trim().isEmpty()) title = existing.getTitle();

            System.out.print("Author(s) [" + existing.getAuthor() + "]: ");
            String author = scanner.nextLine();
            if (author.trim().isEmpty()) author = existing.getAuthor();

            System.out.print("Type [" + existing.getType() + "]: ");
            String type = scanner.nextLine();
            if (type.trim().isEmpty()) type = existing.getType();

            System.out.print("Genre [" + existing.getGenre() + "]: ");
            String genre = scanner.nextLine();
            if (genre.trim().isEmpty()) genre = existing.getGenre();

            System.out.print("Year [" + existing.getYear() + "]: ");
            String yearStr = scanner.nextLine();
            int year = yearStr.trim().isEmpty() ? existing.getYear() : Integer.parseInt(yearStr);

            System.out.print("Pages [" + existing.getPages() + "]: ");
            String pagesStr = scanner.nextLine();
            int pages = pagesStr.trim().isEmpty() ? existing.getPages() : Integer.parseInt(pagesStr);

            Book updated = new Book(title, author, type, genre, year, pages, isbn);
            return bookManager.updateBook(updated);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return false;
        }
    }

    // ----- View -----
    private void handleView() {
        System.out.println("\n--- All Books ---");
        List<Book> books = bookManager.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in the collection.");
        } else {
            printBookTable(books);
        }
    }

    // ----- Sort -----
    private void handleSort() {
        System.out.println("\n--- Sort Books ---");
        int bookCount = bookManager.size();
        if (bookCount == 0) {
            System.out.println("There are no books to sort.");
            return;
        }
        if (bookCount == 1) {
            System.out.println("Only one book in the collection. Sorting will just display that book.");
        }

        String field = null;
        while (field == null) {
            System.out.println("Sort by: 1. Year  2. Pages  3. ISBN");
            System.out.print("Choose column (1-3): ");
            String colChoice = scanner.nextLine().trim();
            field = switch (colChoice) {
                case "1" -> "year";
                case "2" -> "pages";
                case "3" -> "isbn";
                default -> {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    yield null;
                }
            };
        }

        Boolean ascending = null;
        while (ascending == null) {
            System.out.print("Order (A for Ascending, D for Descending): ");
            String orderChoice = scanner.nextLine().trim().toUpperCase();
            if (orderChoice.equals("A")) {
                ascending = true;
            } else if (orderChoice.equals("D")) {
                ascending = false;
            } else {
                System.out.println("Invalid choice. Please enter A or D.");
            }
        }

        try {
            List<Book> sorted = bookManager.getSortedBooks(field, ascending);
            System.out.println("\n--- Sorted Books ---");
            printBookTable(sorted);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ----- Import -----
    private int handleImport() {
        System.out.println("\n--- Import Books from File ---");
        System.out.println("Please enter the full path to the text file (e.g., C:\\Users\\name\\books.txt or /home/user/books.txt)");
        System.out.print("File path: ");
        String filePath = scanner.nextLine().trim();

        if (filePath.isEmpty()) {
            System.out.println("No file path entered.");
            return -1;
        }

        int result = bookManager.importFromFile(filePath);
        if (result == -1) {
            System.out.println("File does not exist or cannot be read.");
        } else if (result == -2) {
            System.out.println("IO error while reading file.");
        }
        return result;
    }

    // ----- Helper -----
    private void printBookTable(List<Book> books) {
        System.out.println("+--------------------------------+----------------------+-----------------+-----------------+------+-------+---------------+");
        System.out.println("| Title                          | Author               | Type            | Genre           | Year | Pages | ISBN          |");
        System.out.println("+--------------------------------+----------------------+-----------------+-----------------+------+-------+---------------+");
        for (Book b : books) {
            System.out.println(b);
        }
        System.out.println("+--------------------------------+----------------------+-----------------+-----------------+------+-------+---------------+");
    }
}