import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * The graphical user interface (presentation layer) for the DMS application.
 * <p>
 * This class creates a Swing‑based window with a toolbar and a table that
 * displays book information. It provides buttons for adding, editing, deleting,
 * sorting, and refreshing books. All user interactions are forwarded to the
 * {@link MainController}.
 * </p>
 * <p>
 * The UI is designed to be intuitive: the table shows all books, the toolbar
 * groups actions, and dialogs collect user input with built‑in validation
 * (handled by the controller).
 * </p>
 *
 * @author Jackson DeWitt
 * @version 1.0
 */
public class MainView extends JFrame {
    private final MainController controller;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> sortCombo;
    private JCheckBox ascendingCheckBox;

    /**
     * Constructs the main application window and initializes the UI components.
     *
     * @param controller the controller that will handle user actions
     */
    public MainView(MainController controller) {
        this.controller = controller;
        setTitle("DMS - Book Database Manager (SQLite)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initUI();
    }

    /**
     * Builds the GUI components: table, toolbar, and layout.
     */
    private void initUI() {
        // Table model (read‑only)
        tableModel = new DefaultTableModel(
                new String[]{"Title", "Author", "Type", "Genre", "Year", "Pages", "ISBN"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        add(scrollPane, BorderLayout.CENTER);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton addButton = new JButton("Add");
        addButton.addActionListener(_ -> showAddDialog());
        toolBar.add(addButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(_ -> deleteSelected());
        toolBar.add(deleteButton);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(_ -> editSelected());
        toolBar.add(editButton);

        toolBar.addSeparator();

        toolBar.add(new JLabel("Sort by: "));
        sortCombo = new JComboBox<>(new String[]{"Year", "Pages", "ISBN"});
        toolBar.add(sortCombo);
        ascendingCheckBox = new JCheckBox("Ascending", true);
        toolBar.add(ascendingCheckBox);
        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(_ -> applySort());
        toolBar.add(sortButton);

        toolBar.addSeparator();

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(_ -> controller.refreshTable());
        toolBar.add(refreshButton);

        add(toolBar, BorderLayout.NORTH);
    }

    /**
     * Opens a dialog to add a new book.
     */
    private void showAddDialog() {
        Book newBook = showBookDialog(null);
        if (newBook != null) {
            controller.addBook(newBook);
        }
    }

    /**
     * Opens a dialog to edit the currently selected book.
     */
    private void editSelected() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long isbn = (long) tableModel.getValueAt(row, 6);
        Book original = new Book();
        original.setIsbn(isbn);
        original.setTitle((String) tableModel.getValueAt(row, 0));
        original.setAuthor((String) tableModel.getValueAt(row, 1));
        original.setType((String) tableModel.getValueAt(row, 2));
        original.setGenre((String) tableModel.getValueAt(row, 3));
        original.setYear((int) tableModel.getValueAt(row, 4));
        original.setPages((int) tableModel.getValueAt(row, 5));

        Book updated = showBookDialog(original);
        if (updated != null) {
            controller.editBook(original, updated);
        }
    }

    /**
     * Deletes the currently selected book after user confirmation.
     */
    private void deleteSelected() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long isbn = (long) tableModel.getValueAt(row, 6);
        controller.deleteBook(isbn);
    }

    /**
     * Triggers sorting of the displayed books based on the selected column and order.
     */
    private void applySort() {
        String field = (String) sortCombo.getSelectedItem();
        boolean ascending = ascendingCheckBox.isSelected();
        controller.sortBooks(field, ascending);
    }

    /**
     * Shows a modal dialog for entering or editing book details.
     *
     * @param existing an existing Book (for edit) or {@code null} for add
     * @return a new Book with the entered data, or {@code null} if canceled
     */
    private Book showBookDialog(Book existing) {
        JTextField titleField = new JTextField(existing != null ? existing.getTitle() : "", 20);
        JTextField authorField = new JTextField(existing != null ? existing.getAuthor() : "", 20);
        JTextField typeField = new JTextField(existing != null ? existing.getType() : "", 20);
        JTextField genreField = new JTextField(existing != null ? existing.getGenre() : "", 20);
        JTextField yearField = new JTextField(existing != null ? String.valueOf(existing.getYear()) : "", 10);
        JTextField pagesField = new JTextField(existing != null ? String.valueOf(existing.getPages()) : "", 10);
        JTextField isbnField = new JTextField(existing != null ? String.valueOf(existing.getIsbn()) : "", 13);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Pages:"));
        panel.add(pagesField);
        panel.add(new JLabel("ISBN:"));
        panel.add(isbnField);

        if (existing != null) {
            isbnField.setEditable(false); // ISBN cannot be changed on edit
        }

        int result = JOptionPane.showConfirmDialog(this, panel,
                existing == null ? "Add Book" : "Edit Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String type = typeField.getText().trim();
            String genre = genreField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            int pages = Integer.parseInt(pagesField.getText().trim());
            long isbn = Long.parseLong(isbnField.getText().trim());
            return new Book(title, author, type, genre, year, pages, isbn);
        } catch (NumberFormatException e) {
            showMessage("Invalid numeric input.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Populates the table with a list of books.
     *
     * @param books the list of books to display
     */
    public void displayBooks(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                    b.getTitle(),
                    b.getAuthor(),
                    b.getType(),
                    b.getGenre(),
                    b.getYear(),
                    b.getPages(),
                    b.getIsbn()
            });
        }
    }

    /**
     * Displays a simple message dialog.
     *
     * @param message     the text to show
     * @param title       the dialog title
     * @param messageType one of {@link JOptionPane} message types
     */
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * Shows a confirmation dialog (Yes/No).
     *
     * @param message the message to display
     * @return the user's choice: {@link JOptionPane#YES_OPTION} or {@link JOptionPane#NO_OPTION}
     */
    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
    }
}