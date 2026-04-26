#  Library Management System
 
A **console-based Library Management System** built with **Java** and **SQLite**, developed as a Java OOP assignment.  
The application allows librarians to manage books, members, staff, and borrowing transactions through a simple **menu-driven command-line interface**.
 
---
 
##  Screenshots

### interface
<img width="963" height="449" alt="Screenshot 2026-04-26 210753" src="https://github.com/user-attachments/assets/848b021c-9c48-4973-9442-9fd104d4f220" />

## Demo
### Adding a book
<img width="1242" height="501" alt="Screenshot 2026-04-26 211556" src="https://github.com/user-attachments/assets/b27734ed-1a01-4c6c-aebf-9e01156c517d" />
<img width="1919" height="639" alt="Screenshot 2026-04-26 211608" src="https://github.com/user-attachments/assets/f0700994-2f21-4167-b7e8-c331bd9800a0" />




 
---
 
##  Features
 
- **Dashboard** — Live stats: total books, available copies, members, active borrows, overdue count
- **Book Management** — Add, edit, delete and search books with copy tracking
- **Member Management** — Register members with STANDARD / PREMIUM / STUDENT tiers
- **Librarian Management** — Manage library staff with employee codes and departments
- **Transactions** — Check out and return books with automatic due-date calculation
- **Overdue Report** — View all overdue books with days late and estimated fines ($0.50/day)
- **SQLite Database** — Zero-install, file-based database (`library.db`) created automatically on first run
---
 
##  OOP Concepts Demonstrated
 
| Concept | Where Applied |
|---|---|
| **Classes & Objects** | Book, Member, Librarian, Transaction, LibraryService |
| **Encapsulation** | Private fields + getters/setters in all model classes |
| **Inheritance** | `Member` and `Librarian` both extend abstract `Person` |
| **Polymorphism** | `getRole()` and `toString()` overridden differently per subclass |
| **Abstraction** | Abstract class `Person`, interface `GenericDAO<T, ID>` |
| **Constructor Overloading** | `Book(6 args)`, `Book(8 args)`; `Person` variants |
| **Method Overriding** | `toString()` in every model class |
| **Method Overloading** | `getMaxBooksAllowed()`, `prompt()`, `promptInt()` in ConsoleUtil |
| **Collections** | `ArrayList` in all DAOs; `HashMap` in `getDashboardStats()` |
| **JDBC / CRUD** | BookDAO, MemberDAO, LibrarianDAO, TransactionDAO |
| **Exception Handling** | try/catch in every DAO method and service layer |
| **Layered Architecture** | CLI → Service → DAO → DB |
 
---
 
##  Project Structure
 
```
LibraryManagementSystem/
├── src/
│   ├── Main.java                        # Entry point — launches Swing GUI
│   ├── model/
│   │   ├── Person.java                  # Abstract base class
│   │   ├── Book.java
│   │   ├── Member.java                  # Extends Person
│   │   ├── Librarian.java               # Extends Person
│   │   └── Transaction.java
│   ├── dao/
│   │   ├── GenericDAO.java              # Interface with CRUD contract
│   │   ├── BookDAO.java
│   │   ├── MemberDAO.java
│   │   ├── LibrarianDAO.java
│   │   └── TransactionDAO.java
│   ├── service/
│   │   └── LibraryService.java          # Business logic layer
│   ├── ui/
│   │   └── LibraryUI.java              
│   ├── db/
│   │   ├── DatabaseConnection.java      # Singleton JDBC connection
│   │   └── DatabaseInitializer.java     # Schema creation + seed data
│   └── util/
│       └── ConsoleUtil.java             # Utility helpers
├── sql/
│   └── library_schema.sql               # Full schema
└── report/
    └── LibraryManagementSystem_Report.docx
```
 
---
 
##  How to Run
 
### Step 1 — Compile
 
 
**Windows (Command Prompt):**
```cmd
cd LibraryManagementSystem
mkdir out
javac -cp "src;sqlite-jdbc.jar" -sourcepath src -d out src\Main.java src\db\*.java src\model\*.java src\dao\*.java src\service\*.java src\ui\*.java src\util\*.java
```
 
---
 
### Step 2 — Run
 
**Windows:**
```cmd
java -cp "out;sqlite-jdbc.jar" Main
```

## Console interface
After running the program, you will see a menu like:

===== Library Management System =====
1. Book Management  
2. Member Management  
3. Librarian Management  
4. Transactions (Checkout / Return)  
5. Dashboard Stats  
6. Overdue Report  
0. Exit

====================================

Choose an option:
 
---
 
##  Sample Data 
 
The app auto-seeds the database with:
- **3 Librarians** — Alice Johnson, Bob Martinez, Clara Singh
- **5 Members** — Carol (STANDARD), David (PREMIUM), Emma (STUDENT), Frank, Grace
- **12 Books** — Clean Code, 1984, Atomic Habits, Sapiens, and more
- **4 Sample Transactions** — including one with an overdue fine
---


##  Tech Stack
 
- **Language:** Java 17+
- **Interface:** Console (CLI)
- **Database:** SQLite (via JDBC)
- **Driver:** xerial/sqlite-jdbc
- **Architecture:** 4-layer (CLI → Service → DAO → DB)
---
 
##  Notes
 
- No external frameworks or build tools required — plain `javac` and `java`
- The database file `library.db` is created in the directory where you run the app
- To reset the database, simply delete `library.db` and re-run


