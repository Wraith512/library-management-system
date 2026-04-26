package model;

import java.time.LocalDate;

/**
 * Represents a library staff member / librarian.
 * Demonstrates: Inheritance (extends Person), Method Overriding.
 */
public class Librarian extends Person {

    private String    employeeCode;
    private String    department;
    private LocalDate hireDate;
    private double    salary;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Librarian() {
        super();
    }

    public Librarian(int id, String name, String email, String phone,
                     String employeeCode, String department,
                     LocalDate hireDate, double salary) {
        super(id, name, email, phone);
        this.employeeCode = employeeCode;
        this.department   = department;
        this.hireDate     = hireDate;
        this.salary       = salary;
    }

    // Constructor overloading - minimal for quick creation
    public Librarian(String name, String email, String phone,
                     String employeeCode, String department, double salary) {
        super(name, email, phone);
        this.employeeCode = employeeCode;
        this.department   = department;
        this.hireDate     = LocalDate.now();
        this.salary       = salary;
    }

    // ── Abstract method implementation ───────────────────────────────────────
    @Override
    public String getRole() {
        return "LIBRARIAN";
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String    getEmployeeCode()                       { return employeeCode; }
    public void      setEmployeeCode(String employeeCode)    { this.employeeCode = employeeCode; }

    public String    getDepartment()                         { return department; }
    public void      setDepartment(String department)        { this.department = department; }

    public LocalDate getHireDate()                           { return hireDate; }
    public void      setHireDate(LocalDate hireDate)         { this.hireDate = hireDate; }

    public double    getSalary()                             { return salary; }
    public void      setSalary(double salary)                { this.salary = salary; }

    // ── toString override (Polymorphism) ─────────────────────────────────────
    @Override
    public String toString() {
        return super.toString()
             + String.format(" | EmpCode: %-8s | Dept: %-15s | Hired: %s | Salary: $%.2f",
               employeeCode, department, hireDate, salary);
    }
}
