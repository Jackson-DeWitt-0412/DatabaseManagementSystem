//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/31/2026
//Class Name: BookManager
//Soon to be replaced with the DatabaseManager, the current code within the BookManager is largely (probably)
//going to be the same: it handles all the books entered in and, as detailed within the project planning thus far,
//will be the main communicator between the database, the main program, and the books. For now though, it just handles
//books.

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class BookManager {
    private final List<Book> books;

    public BookManager() {
        books = new ArrayList<>();
        // Optionally add some sample books for demonstration
        addSampleBook();
    }

    private void addSampleBook() {
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "Novel", "Literary Fiction",
                1925, 180, 9780743273565L));
        books.add(new Book("1984", "George Orwell", "Novel", "Dystopian",
                1949, 328, 9780451524935L));
    }

    // ----- CRUD -----
    public boolean addBook(Book book) {
        if (bookExists(book.getIsbn())) {
            return false;
        }
        books.add(book);
        return true;
    }

    public boolean deleteBook(long isbn) {
        return books.removeIf(b -> b.getIsbn() == isbn);
    }

    public boolean updateBook(Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn() == updatedBook.getIsbn()) {
                books.set(i, updatedBook);
                return true;
            }
        }
        return false;
    }

    public boolean bookExists(long isbn) {
        return books.stream().anyMatch(b -> b.getIsbn() == isbn);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    // ----- Sorting (custom action) -----
    public List<Book> getSortedBooks(String field, boolean ascending) {
        Comparator<Book> comparator = switch (field.toLowerCase()) {
            case "year" -> Comparator.comparingInt(Book::getYear);
            case "pages" -> Comparator.comparingInt(Book::getPages);
            case "isbn" -> Comparator.comparingLong(Book::getIsbn);
            default -> throw new IllegalArgumentException("Invalid sort field: " + field);
        };
        if (!ascending) {
            comparator = comparator.reversed();
        }
        return books.stream().sorted(comparator).collect(Collectors.toList());
    }

    // ----- Import from file (pipe‑delimited) -----
    public int importFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists() || !file.canRead()) {
            return -1; // file not found/unreadable
        }

        int count = 0;
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (firstLine) {
                    firstLine = false; // skip header
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length != 7) {
                    System.err.println("Warning: Line " + lineNumber + " has incorrect number of fields. Skipped.");
                    continue;
                }
                try {
                    String title = parts[0].trim();
                    String author = parts[1].trim();
                    String type = parts[2].trim();
                    String genre = parts[3].trim();
                    int year = Integer.parseInt(parts[4].trim());
                    int pages = Integer.parseInt(parts[5].trim());
                    long isbn = Long.parseLong(parts[6].trim());

                    // Basic validation (can be enhanced)
                    if (title.isEmpty() || author.isEmpty() || type.isEmpty() || genre.isEmpty() ||
                            year < 1021 || year > 2026 || pages <= 0 || String.valueOf(isbn).length() != 13) {
                        System.err.println("Warning: Line " + lineNumber + " contains invalid data. Skipped.");
                        continue;
                    }

                    Book book = new Book(title, author, type, genre, year, pages, isbn);
                    if (!bookExists(book.getIsbn())) {
                        books.add(book);
                        count++;
                    } else {
                        System.err.println("Warning: Duplicate ISBN at line " + lineNumber + ". Skipped.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Line " + lineNumber + " has invalid number format. Skipped.");
                }
            }
        } catch (IOException e) {
            return -2; // IO error
        }
        return count;
    }

    // ----- Utility -----
    public int size() {
        return books.size();
    }

}