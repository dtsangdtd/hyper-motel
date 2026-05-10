package hyper.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "\"user\"") // "user" is a reserved word in Postgres, so it must be escaped
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "\"phoneNumber\"", nullable = false, unique = true, length = 20)
    private String phoneNumber;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(name = "\"firstName\"", length = 100)
    private String firstName;

    @Column(name = "\"lastName\"", length = 100)
    private String lastName;

    @Column(name = "\"isActive\"", nullable = false)
    private Boolean isActive = true;


    protected User() {
    }


    // Add Getter and Setter for roles

    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public User(String phoneNumber, String password, String firstName, String lastName) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "\"userRole\"",
            joinColumns = @JoinColumn(name = "\"userId\""),      // Explicitly quote userId
            inverseJoinColumns = @JoinColumn(name = "\"roleId\"") // Explicitly quote roleId
    )
    private Set<Role> roles = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
