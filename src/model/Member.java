package model;

import java.time.LocalDate;

/**
 * Represents a library member.
 * Demonstrates: Inheritance (extends Person), Encapsulation, Method Overriding.
 */
public class Member extends Person {

    private String membershipType;   // "STANDARD" | "PREMIUM" | "STUDENT"
    private LocalDate joinDate;
    private LocalDate expiryDate;
    private int       booksCheckedOut;
    private boolean   active;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Member() {
        super();
    }

    public Member(int id, String name, String email, String phone,
                  String membershipType, LocalDate joinDate,
                  LocalDate expiryDate, int booksCheckedOut, boolean active) {
        super(id, name, email, phone);
        this.membershipType   = membershipType;
        this.joinDate         = joinDate;
        this.expiryDate       = expiryDate;
        this.booksCheckedOut  = booksCheckedOut;
        this.active           = active;
    }

    // Constructor overloading - for creating new members
    public Member(String name, String email, String phone, String membershipType) {
        super(name, email, phone);
        this.membershipType  = membershipType;
        this.joinDate        = LocalDate.now();
        this.expiryDate      = LocalDate.now().plusYears(1);
        this.booksCheckedOut = 0;
        this.active          = true;
    }

    // ── Abstract method implementation ───────────────────────────────────────
    @Override
    public String getRole() {
        return "MEMBER";
    }

    // ── Business logic ───────────────────────────────────────────────────────
    /** Maximum books allowed depends on membership type (Method overloading example). */
    public int getMaxBooksAllowed() {
        switch (membershipType.toUpperCase()) {
            case "PREMIUM":  return 10;
            case "STUDENT":  return 5;
            default:         return 3;   // STANDARD
        }
    }

    /** Overloaded version - allows custom cap per policy */
    public int getMaxBooksAllowed(int customCap) {
        return customCap;
    }

    public boolean canCheckOutBook() {
        return active && booksCheckedOut < getMaxBooksAllowed()
                && expiryDate.isAfter(LocalDate.now());
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String    getMembershipType()                         { return membershipType; }
    public void      setMembershipType(String membershipType)    { this.membershipType = membershipType; }

    public LocalDate getJoinDate()                               { return joinDate; }
    public void      setJoinDate(LocalDate joinDate)             { this.joinDate = joinDate; }

    public LocalDate getExpiryDate()                             { return expiryDate; }
    public void      setExpiryDate(LocalDate expiryDate)         { this.expiryDate = expiryDate; }

    public int       getBooksCheckedOut()                        { return booksCheckedOut; }
    public void      setBooksCheckedOut(int booksCheckedOut)     { this.booksCheckedOut = booksCheckedOut; }

    public boolean   isActive()                                  { return active; }
    public void      setActive(boolean active)                   { this.active = active; }

    // ── toString override (Polymorphism) ─────────────────────────────────────
    @Override
    public String toString() {
        return super.toString()
             + String.format(" | Type: %-8s | Books Out: %d/%d | Expires: %s | Status: %s",
               membershipType, booksCheckedOut, getMaxBooksAllowed(),
               expiryDate, active ? "ACTIVE" : "INACTIVE");
    }
}
