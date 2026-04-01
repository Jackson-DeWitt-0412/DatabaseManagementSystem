//Name: Jackson DeWitt, Course: Software Development 1 (202620-CEN-3024C-23585), Date: 3/31/2026
//Class Name: Book
//The object the whole project revolves around, detailing all seven (7) values the book(s) is/are expected to hold
//Despite the changes to other parts of the program, this file has remained relatively the same.

public class Book {
    private String title;
    private String author;
    private String type;
    private String genre;
    private int year;
    private int pages;
    private long isbn;   // ISBN MUST BE 13 digits

    public Book() {}

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

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
    public long getIsbn() { return isbn; }
    public void setIsbn(long isbn) { this.isbn = isbn; }

    @Override
    public String toString() {
        return String.format("%-30s %-20s %-15s %-15s %4d %5d %13d",
                title, author, type, genre, year, pages, isbn);
    }
}