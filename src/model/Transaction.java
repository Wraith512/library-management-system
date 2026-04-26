package model;

import java.time.LocalDate;

/**
 * Represents a book borrowing transaction.
 * Demonstrates: Encapsulation, Constructor overloading.
 */
public class Transaction {

    public enum Status { BORROWED, RETURNED, OVERDUE }

    private int       id;
    private int       memberId;
    private int       bookId;
    private int       librarianId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;   // null if not yet returned
    private Status    status;
    private double    fineAmount;

    // For display purposes (joined data)
    private String memberName;
    private String bookTitle;
    private String librarianName;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Transaction() {}

    public Transaction(int id, int memberId, int bookId, int librarianId,
                       LocalDate borrowDate, LocalDate dueDate,
                       LocalDate returnDate, Status status, double fineAmount) {
        this.id          = id;
        this.memberId    = memberId;
        this.bookId      = bookId;
        this.librarianId = librarianId;
        this.borrowDate  = borrowDate;
        this.dueDate     = dueDate;
        this.returnDate  = returnDate;
        this.status      = status;
        this.fineAmount  = fineAmount;
    }

    // Overloaded - for new checkout
    public Transaction(int memberId, int bookId, int librarianId) {
        this.memberId    = memberId;
        this.bookId      = bookId;
        this.librarianId = librarianId;
        this.borrowDate  = LocalDate.now();
        this.dueDate     = LocalDate.now().plusDays(14);
        this.status      = Status.BORROWED;
        this.fineAmount  = 0.0;
    }

    // ── Business logic ───────────────────────────────────────────────────────
    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }

    /** Calculate fine at $0.50 per overdue day. */
    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        long daysLate = LocalDate.now().toEpochDay() - dueDate.toEpochDay();
        return daysLate * 0.50;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int       getId()                           { return id; }
    public void      setId(int id)                     { this.id = id; }

    public int       getMemberId()                     { return memberId; }
    public void      setMemberId(int memberId)         { this.memberId = memberId; }

    public int       getBookId()                       { return bookId; }
    public void      setBookId(int bookId)             { this.bookId = bookId; }

    public int       getLibrarianId()                  { return librarianId; }
    public void      setLibrarianId(int id)            { this.librarianId = id; }

    public LocalDate getBorrowDate()                   { return borrowDate; }
    public void      setBorrowDate(LocalDate d)        { this.borrowDate = d; }

    public LocalDate getDueDate()                      { return dueDate; }
    public void      setDueDate(LocalDate d)           { this.dueDate = d; }

    public LocalDate getReturnDate()                   { return returnDate; }
    public void      setReturnDate(LocalDate d)        { this.returnDate = d; }

    public Status    getStatus()                       { return status; }
    public void      setStatus(Status status)          { this.status = status; }

    public double    getFineAmount()                   { return fineAmount; }
    public void      setFineAmount(double fineAmount)  { this.fineAmount = fineAmount; }

    public String    getMemberName()                   { return memberName; }
    public void      setMemberName(String n)           { this.memberName = n; }

    public String    getBookTitle()                    { return bookTitle; }
    public void      setBookTitle(String t)            { this.bookTitle = t; }

    public String    getLibrarianName()                { return librarianName; }
    public void      setLibrarianName(String n)        { this.librarianName = n; }

    @Override
    public String toString() {
        return String.format(
            "TxnID: %4d | Member: %-20s | Book: %-35s | Borrowed: %s | Due: %s | Return: %-10s | Status: %-8s | Fine: $%.2f",
            id,
            memberName  != null ? memberName  : "ID:" + memberId,
            bookTitle   != null ? bookTitle   : "ID:" + bookId,
            borrowDate, dueDate,
            returnDate != null ? returnDate.toString() : "N/A",
            status, fineAmount);
    }
}
