package model;

/**
 * Abstract base class representing a Person.
 * Demonstrates: Abstraction, Encapsulation, Inheritance root.
 */
public abstract class Person {

    // Encapsulated fields with private access
    private int id;
    private String name;
    private String email;
    private String phone;

    // Default constructor
    public Person() {}

    // Parameterized constructor (Constructor overloading)
    public Person(int id, String name, String email, String phone) {
        this.id    = id;
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    // Constructor overloading - without id (for new records before DB insert)
    public Person(String name, String email, String phone) {
        this.name  = name;
        this.email = email;
        this.phone = phone;
    }

    // ── Getters & Setters (Encapsulation) ────────────────────────────────────
    public int    getId()               { return id; }
    public void   setId(int id)         { this.id = id; }

    public String getName()             { return name; }
    public void   setName(String name)  { this.name = name; }

    public String getEmail()              { return email; }
    public void   setEmail(String email)  { this.email = email; }

    public String getPhone()              { return phone; }
    public void   setPhone(String phone)  { this.phone = phone; }

    // ── Abstract method (Abstraction) ────────────────────────────────────────
    /** Every Person subclass must provide its own display representation. */
    public abstract String getRole();

    // ── Polymorphic toString (Method overriding) ──────────────────────────────
    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Name: %-25s | Email: %-30s | Phone: %s",
                getRole(), id, name, email, phone);
    }
}
