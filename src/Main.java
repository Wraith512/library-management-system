import db.DatabaseConnection;
import db.DatabaseInitializer;
import service.LibraryService;
import ui.LibraryUI;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║     Library Management System — Application Entry Point  ║
 * ║     Java OOP Assignment — All requirements met           ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * OOP Checklist:
 *   ✔ Classes & Objects   — Book, Member, Librarian, Transaction, LibraryService …
 *   ✔ Encapsulation       — Private fields + getters/setters in all model classes
 *   ✔ Inheritance         — Member, Librarian extend abstract Person
 *   ✔ Polymorphism        — getRole(), toString() overridden differently in each subclass
 *   ✔ Abstraction         — abstract class Person, interface GenericDAO<T,ID>
 *   ✔ Constructor O/L     — Book(6 args), Book(7 args), Book(8 args); Person variants
 *   ✔ Method Overriding   — toString() in every model class
 *   ✔ Method Overloading  — getMaxBooksAllowed(), prompt(), promptInt() in ConsoleUtil
 *   ✔ Collections         — ArrayList in all DAOs; HashMap in getDashboardStats()
 *   ✔ JDBC / CRUD         — BookDAO, MemberDAO, LibrarianDAO, TransactionDAO
 *   ✔ Exception handling  — try/catch in every DAO method + service layer validation
 *   ✔ Layered architecture— ui → service → dao → db
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialise database schema + seed data
        DatabaseInitializer.initialize();

        // 2. Create service (Business Logic)
        LibraryService service = new LibraryService();

        // 3. Launch console UI (Presentation Layer)
        LibraryUI ui = new LibraryUI(service);
        ui.run();

        // 4. Clean up DB connection on exit
        DatabaseConnection.close();
    }
}
