package models;

import java.time.LocalDate;

public abstract class Profile {
    protected String name;
    protected String surname;
    protected String email;
    protected String password;
    protected LocalDate birthDate;

    public Profile(String name, String surname, String email, String password, LocalDate birthDate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public LocalDate getBirthDate() { return birthDate; }

    public abstract String getRole();
}
