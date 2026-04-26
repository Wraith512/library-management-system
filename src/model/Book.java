package model;

/**
 * Represents a book in the library catalog.
 * Demonstrates: Encapsulation, Constructor overloading.
 */
public class Book {

    private int    id;
    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int    publishYear;
    private int    totalCopies;
    private int    availableCopies;

    // ── Constructors (Constructor overloading) ────────────────────────────────
    public Book() {}

    public Book(int id, String title, String author, String isbn,
                String genre, int publishYear, int totalCopies, int availableCopies) {
        this.id              = id;
        this.title           = title;
        this.author          = author;
        this.isbn            = isbn;
        this.genre           = genre;
        this.publishYear     = publishYear;
        this.totalCopies     = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Overloaded - for new book entry (id assigned by DB)
    public Book(String title, String author, String isbn,
                String genre, int publishYear, int totalCopies) {
        this.title           = title;
        this.author          = author;
        this.isbn            = isbn;
        this.genre           = genre;
        this.publishYear     = publishYear;
        this.totalCopies     = totalCopies;
        this.availableCopies = totalCopies;
    }

    // ── Business logic ───────────────────────────────────────────────────────
    public boolean isAvailable()       { return availableCopies > 0; }
    public void    checkOut()          { if (isAvailable()) availableCopies--; }
    public void    returnCopy()        { if (availableCopies < totalCopies) availableCopies++; }

    // ── Getters & Setters (Encapsulation) ────────────────────────────────────
    public int    getId()                              { return id; }
    public void   setId(int id)                        { this.id = id; }

    public String getTitle()                           { return title; }
    public void   setTitle(String title)               { this.title = title; }

    public String getAuthor()                          { return author; }
    public void   setAuthor(String author)             { this.author = author; }

    public String getIsbn()                            { return isbn; }
    public void   setIsbn(String isbn)                 { this.isbn = isbn; }

    public String getGenre()                           { return genre; }
    public void   setGenre(String genre)               { this.genre = genre; }

    public int    getPublishYear()                     { return publishYear; }
    public void   setPublishYear(int publishYear)      { this.publishYear = publishYear; }

    public int    getTotalCopies()                     { return totalCopies; }
    public void   setTotalCopies(int totalCopies)      { this.totalCopies = totalCopies; }

    public int    getAvailableCopies()                        { return availableCopies; }
    public void   setAvailableCopies(int availableCopies)     { this.availableCopies = availableCopies; }

    @Override
    public String toString() {
        return String.format("ID: %4d | %-40s | %-25s | ISBN: %-15s | Genre: %-12s | Year: %d | Avail: %d/%d",
                id, title, author, isbn, genre, publishYear, availableCopies, totalCopies);
    }
}
