package models.beans;

import java.time.LocalDate;

public class ProfileBean {
    private String name;
    private String surname;
    private String email;
    private String password;
    private LocalDate birthdate;

    public ProfileBean(String name, String surname, String email, String password, LocalDate birthdate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public LocalDate getBirthdate() {
        return birthdate;
    }
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

}
