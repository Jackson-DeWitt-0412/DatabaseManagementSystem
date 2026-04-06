//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 4/5/2026
//Class Name: MainController
//This class is essentially the "policing" part of the program that makes sure everything works correctly and
//synchronizes well with the book manager and the UI generation in MainView while also making sure all entries
//into the program fit predesignated requirements.

import javax.swing.*;
import java.util.List;

public class MainController {
    private final DatabaseManager dbManager;
    private MainView view;

    public MainController(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public void setView(MainView view) {
        this.view = view;
    }

    public void refreshTable() {
        List<Book> books = dbManager.getAllBooks();
        view.displayBooks(books);
    }

    public void addBook(Book book) {
        if (!validateBook(book)) return;
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

    public void editBook(Book original, Book updated) {
        if (!validateBook(updated)) return;
        updated.setIsbn(original.getIsbn());
        boolean success = dbManager.updateBook(updated);
        if (success) {
            view.showMessage("Book updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
        } else {
            view.showMessage("Update failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void sortBooks(String field, boolean ascending) {
        List<Book> sorted = dbManager.getSortedBooks(field, ascending);
        view.displayBooks(sorted);
    }

    private boolean validateBook(Book book) {
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            view.showMessage("Title cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            view.showMessage("Author cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (book.getType() == null || book.getType().trim().isEmpty()) {
            view.showMessage("Type cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (book.getGenre() == null || book.getGenre().trim().isEmpty()) {
            view.showMessage("Genre cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (book.getYear() < 1021 || book.getYear() > 2026) {
            view.showMessage("Year must be between 1021 and 2026.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (book.getPages() <= 0) {
            view.showMessage("Pages must be positive.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (String.valueOf(book.getIsbn()).length() != 13) {
            view.showMessage("ISBN must be exactly 13 digits.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}