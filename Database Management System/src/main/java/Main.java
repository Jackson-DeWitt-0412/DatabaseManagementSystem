//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/31/2026
//Class Name: Main
//Completely reworked from the original, given the new restructuring of the files and code now makes most functions
//happen in other files, making this simply the booting-up part of the program.

public class Main {
    public static void main(String[] args) {
        BookManager bookManager = new BookManager();
        MainController controller = new MainController(bookManager);
        MainView view = new MainView(controller);
        controller.setView(view);
        controller.refreshTable(); // load initial books (sample)
        view.setVisible(true);
    }
}