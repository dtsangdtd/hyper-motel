package hyper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "customer")
@Schema(description = "All details about the customer.")
public class Customer {

    @Id
    // FIX 1: Change GenerationType to UUID (for modern Hibernate 6.x / Spring Boot 3.x)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // FIX 2: Since Room ID is a UUID in the updated SQL schema, this must also be a UUID
    @Column(nullable = false)
    private UUID roomId;

    @Column(nullable = false, unique = true, length = 50)
    private String identityNumber;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 20)
    private String phoneNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;

    // FIX 3: If your DB column is strictly named "updateAt" (case-sensitive),
    // you must quote the name in the @Column annotation to prevent Hibernate
    // from converting it to "update_at".
    @UpdateTimestamp
    @Column(name = "\"updateAt\"", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updateAt;

    @CreationTimestamp
    @Column(name = "\"createAt\"", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createAt;

    public Customer() {
    }

    public Customer(UUID id, UUID roomId, String identityNumber, String firstName, String lastName, String phoneNumber, String address, Boolean isActive) {
        this.id = id;
        this.roomId = roomId;
        this.identityNumber = identityNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}