package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDate;

//Jackson distinguerà le classi tramite il campo role (existing property, grazie a getRole())
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "role",
        visible = true,
        defaultImpl = User.class
)
@JsonSubTypes({
        //Il nome deve corrispondere a quello che restituisce getRole()
        @JsonSubTypes.Type(value = User.class, name = "PATIENT"),
        @JsonSubTypes.Type(value = Nutritionist.class, name = "NUTRITIONIST")
})

@JsonIgnoreProperties(ignoreUnknown = true)

public abstract class Profile {
    protected String name;
    protected String surname;
    protected String email;
    protected String password;
    protected LocalDate birthDate;

    public Profile() {}

    public Profile(String name, String surname, String email, String password, LocalDate birthDate) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public abstract String getRole();
}
