import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all database operations for the DMS project using SQLite.
 * <p>
 * This class manages the connection to an SQLite database and provides
 * CRUD (Create, Read, Update, Delete) methods for the {@code books} table.
 * It also supports sorting and existence checks.
 * </p>
 * <p>
 * The database table is created automatically if it does not exist.
 * All SQL exceptions are logged using {@code java.util.logging}.
 * </p>
 *
 * @author Jackson DeWitt
 * @version 1.0
 */
public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private Connection connection;
    private final String databasePath;

    /**
     * Constructs a DatabaseManager with the given SQLite database file path.
     *
     * @param dbPath the absolute or relative path to the SQLite database file
     */
    public DatabaseManager(String dbPath) {
        this.databasePath = dbPath;
    }

    /**
     * Establishes a connection to the SQLite database and creates the
     * {@code books} table if it does not already exist.
     *
     * @return {@code true} if the connection succeeded, {@code false} otherwise
     */
    public boolean connect() {
        try {
            // Explicitly load the SQLite JDBC driver (required for fat JARs)
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + databasePath;
            connection = DriverManager.getConnection(url);
            createTableIfNotExists();
            return true;
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQLite JDBC driver not found in classpath", e);
            return false;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database", e);
            return false;
        }
    }

    /**
     * Closes the current database connection if it is open.
     */
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error disconnecting", e);
        }
    }

    /**
     * Creates the {@code books} table with the required schema if it does not exist.
     * <p>
     * Table schema: isbn (PRIMARY KEY), title, author, type, genre, year, pages.
     * </p>
     */
    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS books (" +
                "isbn INTEGER PRIMARY KEY, " +
                "title TEXT NOT NULL, " +
                "author TEXT NOT NULL, " +
                "type TEXT, " +
                "genre TEXT, " +
                "year INTEGER, " +
                "pages INTEGER)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to create table", e);
        }
    }

    /**
     * Checks whether a book with the given ISBN exists in the database.
     *
     * @param isbn the ISBN to look for
     * @return {@code true} if the book exists, {@code false} otherwise
     */
    public boolean bookExists(long isbn) {
        String sql = "SELECT 1 FROM books WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking ISBN existence", e);
            return false;
        }
    }

    /**
     * Inserts a new book into the database.
     *
     * @param book the book to add
     * @return {@code true} if insertion succeeded, {@code false} otherwise
     */
    public boolean addBook(Book book) {
        String sql = "INSERT INTO books(isbn, title, author, type, genre, year, pages) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getType());
            pstmt.setString(5, book.getGenre());
            pstmt.setInt(6, book.getYear());
            pstmt.setInt(7, book.getPages());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding book", e);
            return false;
        }
    }

    /**
     * Deletes a book from the database by its ISBN.
     *
     * @param isbn the ISBN of the book to delete
     * @return {@code true} if deletion succeeded, {@code false} otherwise
     */
    public boolean deleteBook(long isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, isbn);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting book", e);
            return false;
        }
    }

    /**
     * Updates an existing book's information (except ISBN, which is immutable).
     *
     * @param book the book containing updated data (ISBN must match an existing row)
     * @return {@code true} if update succeeded, {@code false} otherwise
     */
    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, type=?, genre=?, year=?, pages=? WHERE isbn=?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getType());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getYear());
            pstmt.setInt(6, book.getPages());
            pstmt.setLong(7, book.getIsbn());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating book", e);
            return false;
        }
    }

    /**
     * Retrieves all books from the database, ordered alphabetically by title.
     *
     * @return a list of all Book objects (may be empty)
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY title";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getLong("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setType(rs.getString("type"));
                b.setGenre(rs.getString("genre"));
                b.setYear(rs.getInt("year"));
                b.setPages(rs.getInt("pages"));
                books.add(b);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all books", e);
        }
        return books;
    }

    /**
     * Retrieves all books sorted by the specified field in ascending or descending order.
     *
     * @param field     the column to sort by: "year", "pages", or "isbn"
     * @param ascending {@code true} for ascending order, {@code false} for descending
     * @return a sorted list of Book objects (may be empty)
     * @throws IllegalArgumentException if the field is not one of the allowed values
     */
    public List<Book> getSortedBooks(String field, boolean ascending) {
        String order = ascending ? "ASC" : "DESC";
        String validField = field.toLowerCase();
        if (!validField.matches("year|pages|isbn")) {
            throw new IllegalArgumentException("Invalid sort field: " + field);
        }
        String sql = "SELECT * FROM books ORDER BY " + validField + " " + order;
        List<Book> books = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getLong("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setType(rs.getString("type"));
                b.setGenre(rs.getString("genre"));
                b.setYear(rs.getInt("year"));
                b.setPages(rs.getInt("pages"));
                books.add(b);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving sorted books", e);
        }
        return books;
    }
}