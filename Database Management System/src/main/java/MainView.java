//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/31/2026
//Class Name: MainController
//This part of the program makes the UI for the program; most of the information involves making sure all the
//information is conveyed in the appropriate fields. The logic is handled by the manager, and the policing of whatever
//the user enters is mostly covered by the controller.

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MainView extends JFrame {
    private final MainController controller;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> sortCombo;
    private JCheckBox ascendingCheckBox;

    public MainView(MainController controller) {
        this.controller = controller;
        setTitle("DMS - Book Database Manager (In-Memory)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Table model (read-only)
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

        // Import button
        JButton importButton = new JButton("Import from File");
        importButton.addActionListener(_ -> importFile());
        toolBar.add(importButton);

        toolBar.addSeparator();

        // Add, Delete, Edit
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

        // Sort
        toolBar.add(new JLabel("Sort by: "));
        sortCombo = new JComboBox<>(new String[]{"Year", "Pages", "ISBN"});
        toolBar.add(sortCombo);
        ascendingCheckBox = new JCheckBox("Ascending", true);
        toolBar.add(ascendingCheckBox);
        JButton sortButton = new JButton("Sort");
        sortButton.addActionListener(_ -> applySort());
        toolBar.add(sortButton);

        toolBar.addSeparator();

        // Refresh
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(_ -> controller.refreshTable());
        toolBar.add(refreshButton);

        add(toolBar, BorderLayout.NORTH);
    }

    // --- File import with JFileChooser ---
    private void importFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Book Data File (pipe-delimited)");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            controller.importBooks(selectedFile.getAbsolutePath());
        }
    }

    // --- Add dialog ---
    private void showAddDialog() {
        Book newBook = showBookDialog(null);
        if (newBook != null) {
            controller.addBook(newBook);
        }
    }

    // --- Edit selected book ---
    private void editSelected() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long isbn = (long) tableModel.getValueAt(row, 6);
        // Retrieve current values from the table
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

    // --- Delete selected book ---
    private void deleteSelected() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            showMessage("Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        long isbn = (long) tableModel.getValueAt(row, 6);
        controller.deleteBook(isbn);
    }

    // --- Apply sort ---
    private void applySort() {
        String field = (String) sortCombo.getSelectedItem();
        boolean ascending = ascendingCheckBox.isSelected();
        controller.sortBooks(field, ascending);
    }

    // --- Generic dialog for adding/editing a book ---
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

    // --- Public methods used by controller ---
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

    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(this, message, "Confirm", JOptionPane.YES_NO_OPTION);
    }
}