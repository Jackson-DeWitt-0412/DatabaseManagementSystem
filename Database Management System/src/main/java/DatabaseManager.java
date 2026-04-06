//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 4/5/2026
//Class Name: DatabaseManager
//Formerly known as the BookManager, the current code within this file handles all database connectivity and operations
//now that there is a database to store the information from, as well as edit with the GUI now that there is permanence
//to the data outside the currently-running program.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private Connection connection;
    private final String databasePath;

    public DatabaseManager(String dbPath) {
        this.databasePath = dbPath;
    }

    public boolean connect() {
        try {
            // Explicitly load the SQLite JDBC driver class
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

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error disconnecting", e);
        }
    }

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

    public List<Book> getSortedBooks(String field, boolean ascending) {
        String order = ascending ? "ASC" : "DESC";
        String validField = field.toLowerCase();
        if (!validField.matches("year|pages|isbn")) {
            validField = "title";
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