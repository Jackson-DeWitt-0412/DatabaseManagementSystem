//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 4/5/2026
//Class Name: Main
//Completely reworked from the original, given the new restructuring of the files and code now makes most functions
//happen in other files. Now, however, one of the most important functions is prompting the user to pick the database
//to connect to (though the interface and policing is still largely handled by the controller and view files,
//respectively).

import javax.swing.*;
import java.io.File;

public class Main {
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
            JOptionPane.showMessageDialog(null, "Failed to connect to database.\nPlease ensure the file is a valid SQLite database.", "Connection Error", JOptionPane.ERROR_MESSAGE);
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