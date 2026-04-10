import javax.swing.*;
import java.util.List;

/**
 * The controller layer of the DMS application.
 * <p>
 * This class handles all user actions triggered by the GUI, validates input,
 * and orchestrates operations with the {@link DatabaseManager}. It acts as the
 * intermediary between the presentation layer (MainView) and the data layer.
 * </p>
 * <p>
 * The controller is responsible for:
 * <ul>
 *   <li>Validating book data (year range, ISBN length, non‑empty fields)</li>
 *   <li>Performing CRUD operations via the database manager</li>
 *   <li>Refreshing the view with updated data</li>
 *   <li>Showing user messages and confirmations</li>
 * </ul>
 * </p>
 *
 * @author Jackson DeWitt
 * @version 1.0
 */
public class MainController {
    private final DatabaseManager dbManager;
    private MainView view;

    /**
     * Constructs a MainController with the given DatabaseManager.
     *
     * @param dbManager the database manager used for all persistence operations
     */
    public MainController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Injects the view reference so that the controller can update the UI.
     *
     * @param view the MainView instance to be controlled
     */
    public void setView(MainView view) {
        this.view = view;
    }

    /**
     * Fetches all books from the database and displays them in the view.
     */
    public void refreshTable() {
        List<Book> books = dbManager.getAllBooks();
        view.displayBooks(books);
    }

    /**
     * Adds a new book after validating its data and checking for duplicate ISBN.
     *
     * @param book the book to add
     */
    public void addBook(Book book) {
        if (validateBook(book)) return;
        if (dbManager.bookExists(book.getIsbn())) {
            view.showMessage("A book with this ISBN already exists.", "Duplicate ISBN", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean success = dbManager.addBook(book);
        if (success) {
            view.showMessage("Book added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            view.showMessage("Failed to add book.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes a book after user confirmation.
     *
     * @param isbn the ISBN of the book to delete
     */
    public void deleteBook(long isbn) {
        int confirm = view.showConfirmDialog("Delete book with ISBN " + isbn + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = dbManager.deleteBook(isbn);
            if (success) {
                view.showMessage("Book deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshTable();
            } else {
                view.showMessage("Delete failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates an existing book with new data. The ISBN cannot be changed.
     *
     * @param original the original book (used to keep the ISBN)
     * @param updated  the book containing updated fields (its ISBN is ignored)
     */
    public void editBook(Book original, Book updated) {
        if (validateBook(updated)) return;
        updated.setIsbn(original.getIsbn()); // ISBN is immutable
        boolean success = dbManager.updateBook(updated);
        if (success) {
            view.showMessage("Book updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            view.showMessage("Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sorts the book list by the specified field and displays the sorted result.
     *
     * @param field     the column to sort by (Year, Pages, or ISBN)
     * @param ascending {@code true} for ascending, {@code false} for descending
     */
    public void sortBooks(String field, boolean ascending) {
        List<Book> sorted = dbManager.getSortedBooks(field, ascending);
        view.displayBooks(sorted);
    }

    /**
     * Validates all fields of a book according to business rules.
     *
     * @param book the book to validate
     * @return {@code true} if all fields are valid, {@code false} otherwise
     */
    private boolean validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            view.showMessage("Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            view.showMessage("Author cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (book.getType() == null || book.getType().trim().isEmpty()) {
            view.showMessage("Type cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            view.showMessage("Genre cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (book.getYear() < 1021 || book.getYear() > 2026) {
            view.showMessage("Year must be between 1021 and 2026.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (book.getPages() <= 0) {
            view.showMessage("Pages must be positive.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        if (String.valueOf(book.getIsbn()).length() != 13) {
            view.showMessage("ISBN must be exactly 13 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return true;
        }
        return false;
    }
}