package ui;

import model.*;
import service.LibraryService;
import util.ConsoleUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Presentation Layer — Console-based menu UI.
 * Demonstrates: User interaction, exception handling, OOP usage.
 */
public class LibraryUI {

    private final LibraryService service;

    public LibraryUI(LibraryService service) {
        this.service = service;
    }

    // ── Application entry point ───────────────────────────────────────────────
    public void run() {
        showBanner();
        boolean running = true;
        while (running) {
            showMainMenu();
            int choice = ConsoleUtil.promptInt("Choose an option", 0, 6);
            switch (choice) {
                case 1 -> bookMenu();
                case 2 -> memberMenu();
                case 3 -> librarianMenu();
                case 4 -> transactionMenu();
                case 5 -> showDashboard();
                case 6 -> overdueReport();
                case 0 -> running = false;
            }
        }
        ConsoleUtil.info("Thank you for using Library Management System. Goodbye!");
    }

    // ── Menus ─────────────────────────────────────────────────────────────────
    private void showMainMenu() {
        ConsoleUtil.header("Library Management System — Main Menu");
        System.out.println("  1.  Book Management");
        System.out.println("  2.  Member Management");
        System.out.println("  3.  Librarian Management");
        System.out.println("  4.  Transactions (Checkout / Return)");
        System.out.println("  5.  Dashboard Stats");
        System.out.println("  6.  Overdue Report");
        System.out.println("  0.  Exit");
        ConsoleUtil.divider();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BOOK MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    private void bookMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.header("Book Management");
            System.out.println("  1. Add Book       4. Update Book");
            System.out.println("  2. View All       5. Delete Book");
            System.out.println("  3. Search Books   0. Back");
            ConsoleUtil.divider();
            int c = ConsoleUtil.promptInt("Choice", 0, 5);
            switch (c) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> searchBooks();
                case 4 -> updateBook();
                case 5 -> deleteBook();
                case 0 -> back = true;
            }
        }
    }

    private void addBook() {
        ConsoleUtil.header("Add New Book");
        try {
            String title  = ConsoleUtil.prompt("Title");
            String author = ConsoleUtil.prompt("Author");
            String isbn   = ConsoleUtil.prompt("ISBN");
            String genre  = ConsoleUtil.prompt("Genre");
            int    year   = ConsoleUtil.promptInt("Publish Year");
            int    copies = ConsoleUtil.promptInt("Total Copies");

            Book book = new Book(title, author, isbn, genre, year, copies);
            if (service.addBook(book)) {
                ConsoleUtil.success("Book added! ID: " + book.getId());
            } else {
                ConsoleUtil.error("Failed to add book (ISBN may already exist).");
            }
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error("Validation error: " + e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void viewBooks() {
        ConsoleUtil.header("All Books");
        List<Book> books = service.getAllBooks();
        if (books.isEmpty()) {
            ConsoleUtil.warn("No books found.");
        } else {
            books.forEach(b -> System.out.println(b));
            ConsoleUtil.info("Total: " + books.size() + " book(s).");
        }
        ConsoleUtil.pause();
    }

    private void searchBooks() {
        ConsoleUtil.header("Search Books");
        String kw = ConsoleUtil.prompt("Enter keyword (title / author / ISBN / genre)");
        List<Book> results = service.searchBooks(kw);
        if (results.isEmpty()) {
            ConsoleUtil.warn("No books matched \"" + kw + "\".");
        } else {
            results.forEach(System.out::println);
            ConsoleUtil.info("Found: " + results.size() + " result(s).");
        }
        ConsoleUtil.pause();
    }

    private void updateBook() {
        ConsoleUtil.header("Update Book");
        int id = ConsoleUtil.promptInt("Enter Book ID");
        Optional<Book> opt = service.getBookById(id);
        if (opt.isEmpty()) { ConsoleUtil.error("Book ID " + id + " not found."); ConsoleUtil.pause(); return; }

        Book b = opt.get();
        System.out.println("Current: " + b);
        try {
            b.setTitle    (ConsoleUtil.prompt("New Title",   b.getTitle()));
            b.setAuthor   (ConsoleUtil.prompt("New Author",  b.getAuthor()));
            b.setIsbn     (ConsoleUtil.prompt("New ISBN",    b.getIsbn()));
            b.setGenre    (ConsoleUtil.prompt("New Genre",   b.getGenre()));
            b.setPublishYear(ConsoleUtil.promptInt("New Year"));
            b.setTotalCopies(ConsoleUtil.promptInt("Total Copies"));
            b.setAvailableCopies(ConsoleUtil.promptInt("Available Copies"));

            if (service.updateBook(b)) ConsoleUtil.success("Book updated.");
            else                       ConsoleUtil.error("Update failed.");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void deleteBook() {
        ConsoleUtil.header("Delete Book");
        int id = ConsoleUtil.promptInt("Enter Book ID to delete");
        try {
            if (service.deleteBook(id)) ConsoleUtil.success("Book deleted.");
            else                        ConsoleUtil.error("Delete failed.");
        } catch (Exception e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MEMBER MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    private void memberMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.header("Member Management");
            System.out.println("  1. Add Member        4. Update Member");
            System.out.println("  2. View All Members  5. Delete Member");
            System.out.println("  3. Search Members    6. Member Borrow History");
            System.out.println("  0. Back");
            ConsoleUtil.divider();
            int c = ConsoleUtil.promptInt("Choice", 0, 6);
            switch (c) {
                case 1 -> addMember();
                case 2 -> viewMembers();
                case 3 -> searchMembers();
                case 4 -> updateMember();
                case 5 -> deleteMember();
                case 6 -> memberHistory();
                case 0 -> back = true;
            }
        }
    }

    private void addMember() {
        ConsoleUtil.header("Add New Member");
        try {
            String name  = ConsoleUtil.prompt("Full Name");
            String email = ConsoleUtil.prompt("Email");
            String phone = ConsoleUtil.prompt("Phone");
            System.out.println("Membership types: STANDARD | PREMIUM | STUDENT");
            String type  = ConsoleUtil.prompt("Membership Type", "STANDARD").toUpperCase();

            Member m = new Member(name, email, phone, type);
            if (service.addMember(m)) ConsoleUtil.success("Member added! ID: " + m.getId());
            else                      ConsoleUtil.error("Failed (email may already exist).");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void viewMembers() {
        ConsoleUtil.header("All Members");
        List<Member> members = service.getAllMembers();
        if (members.isEmpty()) ConsoleUtil.warn("No members found.");
        else {
            members.forEach(System.out::println);
            ConsoleUtil.info("Total: " + members.size() + " member(s).");
        }
        ConsoleUtil.pause();
    }

    private void searchMembers() {
        ConsoleUtil.header("Search Members");
        String kw = ConsoleUtil.prompt("Keyword (name / email / phone)");
        List<Member> r = service.searchMembers(kw);
        if (r.isEmpty()) ConsoleUtil.warn("No results for \"" + kw + "\".");
        else { r.forEach(System.out::println); ConsoleUtil.info("Found: " + r.size()); }
        ConsoleUtil.pause();
    }

    private void updateMember() {
        ConsoleUtil.header("Update Member");
        int id = ConsoleUtil.promptInt("Enter Member ID");
        Optional<Member> opt = service.getMemberById(id);
        if (opt.isEmpty()) { ConsoleUtil.error("Member not found."); ConsoleUtil.pause(); return; }

        Member m = opt.get();
        System.out.println("Current: " + m);
        try {
            m.setName (ConsoleUtil.prompt("Name",  m.getName()));
            m.setEmail(ConsoleUtil.prompt("Email", m.getEmail()));
            m.setPhone(ConsoleUtil.prompt("Phone", m.getPhone()));
            String type = ConsoleUtil.prompt("Type (STANDARD/PREMIUM/STUDENT)", m.getMembershipType()).toUpperCase();
            m.setMembershipType(type);
            boolean active = ConsoleUtil.promptYesNo("Active");
            m.setActive(active);

            if (service.updateMember(m)) ConsoleUtil.success("Member updated.");
            else                         ConsoleUtil.error("Update failed.");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void deleteMember() {
        ConsoleUtil.header("Delete Member");
        int id = ConsoleUtil.promptInt("Enter Member ID");
        try {
            if (service.deleteMember(id)) ConsoleUtil.success("Member deleted.");
            else                          ConsoleUtil.error("Delete failed.");
        } catch (Exception e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void memberHistory() {
        ConsoleUtil.header("Member Borrow History");
        int id = ConsoleUtil.promptInt("Enter Member ID");
        var history = service.getMemberHistory(id);
        if (history.isEmpty()) ConsoleUtil.warn("No transactions found for this member.");
        else history.forEach(System.out::println);
        ConsoleUtil.pause();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LIBRARIAN MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    private void librarianMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.header("Librarian Management");
            System.out.println("  1. Add Librarian    4. Update Librarian");
            System.out.println("  2. View All         5. Delete Librarian");
            System.out.println("  3. Search           0. Back");
            ConsoleUtil.divider();
            int c = ConsoleUtil.promptInt("Choice", 0, 5);
            switch (c) {
                case 1 -> addLibrarian();
                case 2 -> viewLibrarians();
                case 3 -> searchLibrarians();
                case 4 -> updateLibrarian();
                case 5 -> deleteLibrarian();
                case 0 -> back = true;
            }
        }
    }

    private void addLibrarian() {
        ConsoleUtil.header("Add New Librarian");
        try {
            String name   = ConsoleUtil.prompt("Full Name");
            String email  = ConsoleUtil.prompt("Email");
            String phone  = ConsoleUtil.prompt("Phone");
            String code   = ConsoleUtil.prompt("Employee Code");
            String dept   = ConsoleUtil.prompt("Department");
            double salary = ConsoleUtil.promptDouble("Salary");

            Librarian lib = new Librarian(name, email, phone, code, dept, salary);
            if (service.addLibrarian(lib)) ConsoleUtil.success("Librarian added! ID: " + lib.getId());
            else                           ConsoleUtil.error("Failed to add librarian.");
        } catch (IllegalArgumentException e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void viewLibrarians() {
        ConsoleUtil.header("All Librarians");
        var list = service.getAllLibrarians();
        if (list.isEmpty()) ConsoleUtil.warn("No librarians found.");
        else { list.forEach(System.out::println); ConsoleUtil.info("Total: " + list.size()); }
        ConsoleUtil.pause();
    }

    private void searchLibrarians() {
        String kw = ConsoleUtil.prompt("Keyword");
        service.searchLibrarians(kw).forEach(System.out::println);
        ConsoleUtil.pause();
    }

    private void updateLibrarian() {
        int id = ConsoleUtil.promptInt("Enter Librarian ID");
        Optional<Librarian> opt = service.getLibrarianById(id);
        if (opt.isEmpty()) { ConsoleUtil.error("Not found."); ConsoleUtil.pause(); return; }
        Librarian lib = opt.get();
        lib.setName      (ConsoleUtil.prompt("Name",       lib.getName()));
        lib.setEmail     (ConsoleUtil.prompt("Email",      lib.getEmail()));
        lib.setPhone     (ConsoleUtil.prompt("Phone",      lib.getPhone()));
        lib.setDepartment(ConsoleUtil.prompt("Department", lib.getDepartment()));
        lib.setSalary    (ConsoleUtil.promptDouble("Salary"));
        if (service.updateLibrarian(lib)) ConsoleUtil.success("Updated.");
        else                              ConsoleUtil.error("Update failed.");
        ConsoleUtil.pause();
    }

    private void deleteLibrarian() {
        int id = ConsoleUtil.promptInt("Enter Librarian ID");
        try {
            if (service.deleteLibrarian(id)) ConsoleUtil.success("Deleted.");
            else                             ConsoleUtil.error("Delete failed.");
        } catch (Exception e) { ConsoleUtil.error(e.getMessage()); }
        ConsoleUtil.pause();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  TRANSACTION MANAGEMENT
    // ══════════════════════════════════════════════════════════════════════════

    private void transactionMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.header("Transaction Management");
            System.out.println("  1. Check Out a Book     4. Search Transactions");
            System.out.println("  2. Return a Book        5. View Active Borrows");
            System.out.println("  3. View All Transactions 0. Back");
            ConsoleUtil.divider();
            int c = ConsoleUtil.promptInt("Choice", 0, 5);
            switch (c) {
                case 1 -> checkOut();
                case 2 -> returnBook();
                case 3 -> viewAllTransactions();
                case 4 -> searchTransactions();
                case 5 -> viewActiveTransactions();
                case 0 -> back = true;
            }
        }
    }

    private void checkOut() {
        ConsoleUtil.header("Check Out a Book");
        try {
            int memberId    = ConsoleUtil.promptInt("Member ID");
            int bookId      = ConsoleUtil.promptInt("Book ID");
            int librarianId = ConsoleUtil.promptInt("Librarian ID");

            Transaction txn = service.checkOutBook(memberId, bookId, librarianId);
            ConsoleUtil.success("Checkout successful! Transaction ID: " + txn.getId());
            ConsoleUtil.info("Due date: " + txn.getDueDate());
        } catch (Exception e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void returnBook() {
        ConsoleUtil.header("Return a Book");
        try {
            int txnId = ConsoleUtil.promptInt("Transaction ID");
            Transaction txn = service.returnBook(txnId);
            ConsoleUtil.success("Book returned successfully!");
            if (txn.getFineAmount() > 0) {
                ConsoleUtil.warn(String.format("Overdue fine: $%.2f", txn.getFineAmount()));
            } else {
                ConsoleUtil.info("No fine — returned on time.");
            }
        } catch (Exception e) {
            ConsoleUtil.error(e.getMessage());
        }
        ConsoleUtil.pause();
    }

    private void viewAllTransactions() {
        ConsoleUtil.header("All Transactions");
        var list = service.getAllTransactions();
        if (list.isEmpty()) ConsoleUtil.warn("No transactions found.");
        else { list.forEach(System.out::println); ConsoleUtil.info("Total: " + list.size()); }
        ConsoleUtil.pause();
    }

    private void viewActiveTransactions() {
        ConsoleUtil.header("Active Borrows");
        var list = service.getActiveTransactions();
        if (list.isEmpty()) ConsoleUtil.info("No books currently borrowed.");
        else { list.forEach(System.out::println); ConsoleUtil.info("Active: " + list.size()); }
        ConsoleUtil.pause();
    }

    private void searchTransactions() {
        String kw = ConsoleUtil.prompt("Keyword (member name / book title / status)");
        service.searchTransactions(kw).forEach(System.out::println);
        ConsoleUtil.pause();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DASHBOARD & REPORTS
    // ══════════════════════════════════════════════════════════════════════════

    private void showDashboard() {
        ConsoleUtil.header("Library Dashboard");
        Map<String, Object> stats = service.getDashboardStats();
        System.out.printf("  📚  Total Book Titles  : %s%n",  stats.get("totalBooks"));
        System.out.printf("  📖  Available Copies   : %s%n",  stats.get("availableCopies"));
        System.out.printf("  👥  Registered Members : %s%n",  stats.get("totalMembers"));
        System.out.printf("  📋  Active Borrows     : %s%n",  stats.get("activeTransactions"));
        System.out.printf("  ⚠   Overdue Books      : %s%n",  stats.get("overdueBooks"));
        ConsoleUtil.pause();
    }

    private void overdueReport() {
        ConsoleUtil.header("Overdue Books Report");
        var list = service.getOverdueTransactions();
        if (list.isEmpty()) {
            ConsoleUtil.success("No overdue books! All returns are on time.");
        } else {
            ConsoleUtil.warn("The following books are overdue:");
            list.forEach(t -> {
                System.out.println(t);
                System.out.printf("    Estimated fine: $%.2f%n", t.calculateFine());
            });
            ConsoleUtil.info("Total overdue: " + list.size());
        }
        ConsoleUtil.pause();
    }

    // ── Banner ────────────────────────────────────────────────────────────────
    private void showBanner() {
        System.out.println(ConsoleUtil.CYAN + ConsoleUtil.BOLD);
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║          📚  LIBRARY MANAGEMENT SYSTEM  📚                          ║");
        System.out.println("║              Java OOP + JDBC (SQLite)                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝");
        System.out.println(ConsoleUtil.RESET);
    }
}
