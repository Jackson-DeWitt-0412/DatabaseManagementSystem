/**
 * Represents a book entity in the Data Management System (DMS).
 * <p>
 * A book contains essential bibliographic information: title, author, type,
 * genre, publication year, page count, and ISBN. The ISBN serves as the
 * unique identifier for each book.
 * </p>
 * <p>
 * This class is used throughout the application as the data carrier between
 * the GUI, controller, and database layers.
 * </p>
 *
 * @author Jackson DeWitt
 * @version 1.0
 */
public class Book {
    private String title;
    private String author;
    private String type;
    private String genre;
    private int year;
    private int pages;
    private long isbn;

    /**
     * Default constructor – creates an empty Book instance.
     * Primarily used for object creation before populating fields.
     */
    public Book() {}

    /**
     * Constructs a new Book with all required attributes.
     *
     * @param title  the title of the book (must not be null or empty)
     * @param author the author(s) of the book (must not be null or empty)
     * @param type   the type of book (e.g., "Novel", "Short Story", "Comic")
     * @param genre  the genre (e.g., "Horror", "Mystery", "Science Fiction")
     * @param year   the publication year (must be between 1021 and 2026)
     * @param pages  the total number of pages (must be positive)
     * @param isbn   the 13-digit ISBN (used as unique primary key)
     */
    public Book(String title, String author, String type, String genre,
                int year, int pages, long isbn) {
        this.title = title;
        this.author = author;
        this.type = type;
        this.genre = genre;
        this.year = year;
        this.pages = pages;
        this.isbn = isbn;
    }

    /**
     * Returns the book title.
     *
     * @return the title
     */
    public String getTitle() { return title; }

    /**
     * Sets the book title.
     *
     * @param title the new title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Returns the author(s).
     *
     * @return the author(s)
     */
    public String getAuthor() { return author; }

    /**
     * Sets the author(s).
     *
     * @param author the new author(s)
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Returns the book type.
     *
     * @return the type
     */
    public String getType() { return type; }

    /**
     * Sets the book type.
     *
     * @param type the new type
     */
    public void setType(String type) { this.type = type; }

    /**
     * Returns the genre.
     *
     * @return the genre
     */
    public String getGenre() { return genre; }

    /**
     * Sets the genre.
     *
     * @param genre the new genre
     */
    public void setGenre(String genre) { this.genre = genre; }

    /**
     * Returns the publication year.
     *
     * @return the year
     */
    public int getYear() { return year; }

    /**
     * Sets the publication year.
     *
     * @param year the new year
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Returns the number of pages.
     *
     * @return the page count
     */
    public int getPages() { return pages; }

    /**
     * Sets the number of pages.
     *
     * @param pages the new page count
     */
    public void setPages(int pages) { this.pages = pages; }

    /**
     * Returns the ISBN.
     *
     * @return the ISBN
     */
    public long getIsbn() { return isbn; }

    /**
     * Sets the ISBN.
     *
     * @param isbn the new ISBN
     */
    public void setIsbn(long isbn) { this.isbn = isbn; }

    /**
     * Returns a formatted string representation of the book,
     * suitable for printing in a table.
     *
     * @return a pipe‑separated and padded string
     */
    @Override
    public String toString() {
        return String.format("| %-30s | %-20s | %-15s | %-15s | %4d | %5d | %13d |",
                title, author, type, genre, year, pages, isbn);
    }

}