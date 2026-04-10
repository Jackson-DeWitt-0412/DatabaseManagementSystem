import javax.swing.*;
import java.io.File;

/**
 * The main entry point for the DMS application (Phase 4 – Database Integration).
 * <p>
 * This class prompts the user to select an SQLite database file, establishes
 * a connection, and launches the graphical user interface. The program exits
 * if the user cancels the file selection or if the database connection fails.
 * </p>
 * <p>
 * Usage: Run the compiled JAR or class; a file chooser will appear. Choose a
 * valid SQLite database file (e.g., {@code books.db}) that contains a {@code books}
 * table with the required schema. The GUI then allows full CRUD operations and
 * sorting on the book collection.
 * </p>
 *
 * @author Jackson DeWitt
 * @version 1.0
 */
public class Main {

    /**
     * The main method – starts the DMS application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Ask user for SQLite database file
        String dbPath = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select SQLite Database File (.db)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("SQLite Database", "db"));
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            dbPath = selectedFile.getAbsolutePath();
        } else {
            System.err.println("No database selected. Exiting.");
            System.exit(0);
        }

        // Connect to the chosen database
        DatabaseManager dbManager = new DatabaseManager(dbPath);
        if (!dbManager.connect()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to database.\nPlease ensure the file is a valid SQLite database.",
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        MainController controller = new MainController(dbManager);
        MainView view = new MainView(controller);
        controller.setView(view);
        controller.refreshTable();
        view.setVisible(true);

        // Ensure database disconnects when the application closes
        Runtime.getRuntime().addShutdownHook(new Thread(dbManager::disconnect));
    }
}