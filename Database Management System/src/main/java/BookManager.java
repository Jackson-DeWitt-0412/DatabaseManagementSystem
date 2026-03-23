//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/21/2026
//Class Name: BookManager
//Eventually to be replaced with the DatabaseManager, the current code within the BookManager is largely (probably)
//going to be the same: it handles all the books entered in and, as detailed within the project planning thus far,
//will be the main communicator between the database, the main program, and the books. For now though, it just handles
//books.

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BookManager {
    private final List<Book> books;

    // Validation constants
    private static final int MIN_YEAR = 1021;
    private static final int MAX_YEAR = 2026;
    private static final Pattern TEXT_PATTERN = Pattern.compile("^[\\p{L}\\s\\-']+$");

    public BookManager() {
        books = new ArrayList<>();
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

    public Book getBook(long isbn) {
        return books.stream().filter(b -> b.getIsbn() == isbn).findFirst().orElse(null);
    }

    public boolean bookExists(long isbn) {
        return books.stream().anyMatch(b -> b.getIsbn() == isbn);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    // ----- Sorting -----
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

    // ----- Import from file with validation -----
    public int importFromFile(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (!file.exists() || !file.canRead()) {
            return -1;
        }

        int count = 0;
        int lineNumber = 0;
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
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
                    System.out.println("Warning: Line " + lineNumber + " has incorrect number of fields. Skipped.");
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

                    // Validate fields
                    if (isValidText(title) || isValidText(author) || isValidText(type) || isValidText(genre)) {
                        System.out.println("Warning: Line " + lineNumber + " contains invalid text (digits/symbols). Skipped.");
                        continue;
                    }
                    if (year < MIN_YEAR || year > MAX_YEAR) {
                        System.out.println("Warning: Line " + lineNumber + " has year outside allowed range. Skipped.");
                        continue;
                    }
                    if (pages <= 0) {
                        System.out.println("Warning: Line " + lineNumber + " has invalid page count. Skipped.");
                        continue;
                    }
                    if (String.valueOf(isbn).length() != 13 || isbn <= 0) {
                        System.out.println("Warning: Line " + lineNumber + " has invalid ISBN. Skipped.");
                        continue;
                    }

                    Book book = new Book(title, author, type, genre, year, pages, isbn);
                    if (!bookExists(book.getIsbn())) {
                        books.add(book);
                        count++;
                    } else {
                        System.out.println("Warning: Line " + lineNumber + " duplicates existing ISBN. Skipped.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Line " + lineNumber + " contains invalid number format. Skipped.");
                }
            }
        } catch (java.io.IOException e) {
            return -2;
        }
        return count;
    }

    // ----- Validation helpers -----
    private boolean isValidText(String text) {
        return text == null || !TEXT_PATTERN.matcher(text).matches();
    }

    // ----- Utility -----
    public int size() {
        return books.size();
    }

}