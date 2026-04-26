package service;

import dao.*;
import model.*;
import model.Transaction.Status;

import java.time.LocalDate;
import java.util.*;

/**
 * Business Logic Layer — orchestrates DAO calls and enforces rules.
 * Demonstrates: Service layer pattern, collections (ArrayList/HashMap).
 */
public class LibraryService {

    private final BookDAO        bookDAO;
    private final MemberDAO      memberDAO;
    private final LibrarianDAO   librarianDAO;
    private final TransactionDAO transactionDAO;

    public LibraryService() {
        this.bookDAO        = new BookDAO();
        this.memberDAO      = new MemberDAO();
        this.librarianDAO   = new LibrarianDAO();
        this.transactionDAO = new TransactionDAO();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BOOK OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean addBook(Book book) {
        validateNotBlank(book.getTitle(),  "Title");
        validateNotBlank(book.getAuthor(), "Author");
        validateNotBlank(book.getIsbn(),   "ISBN");
        if (book.getTotalCopies() < 1) throw new IllegalArgumentException("Copies must be >= 1");
        return bookDAO.create(book);
    }

    public List<Book> getAllBooks()                      { return bookDAO.findAll(); }
    public Optional<Book> getBookById(int id)           { return bookDAO.findById(id); }
    public List<Book> searchBooks(String keyword)       { return bookDAO.search(keyword); }

    public boolean updateBook(Book book) {
        validateNotBlank(book.getTitle(),  "Title");
        validateNotBlank(book.getAuthor(), "Author");
        return bookDAO.update(book);
    }

    public boolean deleteBook(int id) {
        // Cannot delete if copies are currently checked out
        Optional<Book> opt = bookDAO.findById(id);
        if (opt.isEmpty()) throw new NoSuchElementException("Book ID " + id + " not found.");
        Book b = opt.get();
        if (b.getAvailableCopies() < b.getTotalCopies()) {
            throw new IllegalStateException("Cannot delete: some copies are currently borrowed.");
        }
        return bookDAO.delete(id);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MEMBER OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean addMember(Member member) {
        validateNotBlank(member.getName(),  "Name");
        validateNotBlank(member.getEmail(), "Email");
        return memberDAO.create(member);
    }

    public List<Member>   getAllMembers()                    { return memberDAO.findAll(); }
    public Optional<Member> getMemberById(int id)           { return memberDAO.findById(id); }
    public List<Member>   searchMembers(String keyword)     { return memberDAO.search(keyword); }

    public boolean updateMember(Member member) {
        validateNotBlank(member.getName(),  "Name");
        validateNotBlank(member.getEmail(), "Email");
        return memberDAO.update(member);
    }

    public boolean deleteMember(int id) {
        Optional<Member> opt = memberDAO.findById(id);
        if (opt.isEmpty()) throw new NoSuchElementException("Member ID " + id + " not found.");
        if (opt.get().getBooksCheckedOut() > 0) {
            throw new IllegalStateException("Cannot delete: member has books currently checked out.");
        }
        return memberDAO.delete(id);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIBRARIAN OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean addLibrarian(Librarian lib) {
        validateNotBlank(lib.getName(),         "Name");
        validateNotBlank(lib.getEmployeeCode(), "Employee Code");
        return librarianDAO.create(lib);
    }

    public List<Librarian>   getAllLibrarians()                { return librarianDAO.findAll(); }
    public Optional<Librarian> getLibrarianById(int id)       { return librarianDAO.findById(id); }
    public List<Librarian>   searchLibrarians(String keyword) { return librarianDAO.search(keyword); }
    public boolean updateLibrarian(Librarian lib)             { return librarianDAO.update(lib); }
    public boolean deleteLibrarian(int id)                    { return librarianDAO.delete(id); }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANSACTION (CHECKOUT / RETURN) OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Check out a book to a member.
     * Enforces: book availability, member eligibility, copy-count update.
     */
    public Transaction checkOutBook(int memberId, int bookId, int librarianId) {
        Member member = memberDAO.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("Member ID " + memberId + " not found."));
        Book book = bookDAO.findById(bookId)
            .orElseThrow(() -> new NoSuchElementException("Book ID " + bookId + " not found."));

        if (!member.canCheckOutBook())
            throw new IllegalStateException(
                "Member cannot borrow: inactive, overdue membership, or borrow limit reached.");
        if (!book.isAvailable())
            throw new IllegalStateException("No copies of \"" + book.getTitle() + "\" available.");

        Transaction txn = new Transaction(memberId, bookId, librarianId);
        if (!transactionDAO.create(txn))
            throw new RuntimeException("Failed to create transaction.");

        // Decrement available copies
        book.checkOut();
        bookDAO.update(book);

        // Increment member's count
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberDAO.update(member);

        return txn;
    }

    /**
     * Return a previously borrowed book.
     * Calculates fine if overdue.
     */
    public Transaction returnBook(int transactionId) {
        Transaction txn = transactionDAO.findById(transactionId)
            .orElseThrow(() -> new NoSuchElementException("Transaction ID " + transactionId + " not found."));

        if (txn.getStatus() == Status.RETURNED)
            throw new IllegalStateException("This book was already returned.");

        txn.setReturnDate(LocalDate.now());
        double fine = txn.calculateFine();
        txn.setFineAmount(fine);
        txn.setStatus(Status.RETURNED);
        transactionDAO.update(txn);

        // Restore available copy
        Book book = bookDAO.findById(txn.getBookId()).orElseThrow();
        book.returnCopy();
        bookDAO.update(book);

        // Decrement member's borrow count
        Member member = memberDAO.findById(txn.getMemberId()).orElseThrow();
        member.setBooksCheckedOut(Math.max(0, member.getBooksCheckedOut() - 1));
        memberDAO.update(member);

        return txn;
    }

    public List<Transaction> getAllTransactions()              { return transactionDAO.findAll(); }
    public List<Transaction> getActiveTransactions()          { return transactionDAO.findActive(); }
    public List<Transaction> getMemberHistory(int memberId)   { return transactionDAO.findByMember(memberId); }
    public List<Transaction> searchTransactions(String kw)   { return transactionDAO.search(kw); }

    // ══════════════════════════════════════════════════════════════════════════
    //  REPORTS  (uses HashMap / ArrayList — collection usage)
    // ══════════════════════════════════════════════════════════════════════════

    /** Returns a summary map of key library statistics. */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        List<Book>        books   = bookDAO.findAll();
        List<Member>      members = memberDAO.findAll();
        List<Transaction> active  = transactionDAO.findActive();

        stats.put("totalBooks",       books.size());
        stats.put("totalMembers",     members.size());
        stats.put("activeTransactions", active.size());

        long overdueCount = active.stream().filter(Transaction::isOverdue).count();
        stats.put("overdueBooks", overdueCount);

        int totalAvailable = books.stream().mapToInt(Book::getAvailableCopies).sum();
        stats.put("availableCopies", totalAvailable);

        return stats;
    }

    /** Returns all overdue transactions. */
    public List<Transaction> getOverdueTransactions() {
        List<Transaction> overdue = new ArrayList<>();
        for (Transaction t : transactionDAO.findActive()) {
            if (t.isOverdue()) overdue.add(t);
        }
        return overdue;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  VALIDATION HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private void validateNotBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " cannot be empty.");
        }
    }
}
