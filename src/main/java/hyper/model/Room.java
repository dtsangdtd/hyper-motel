package hyper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room")
@Schema(description = "Details about the room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "\"name\"", nullable = false, length = 100)
    private String name;

    @Column(name = "\"electricNumber\"", nullable = true, precision = 10, scale = 2)
    private BigDecimal electricNumber;

    @Column(name = "\"waterNumber\"", nullable = true, precision = 10, scale = 2)
    private BigDecimal waterNumber;

    @Column(name = "\"price\"", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @UpdateTimestamp
    @Column(name = "\"updateAt\"", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updateAt;

    @CreationTimestamp
    @Column(name = "\"createAt\"", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private LocalDateTime createAt;

    public Room() {
    }

    public Room(UUID id, String name, BigDecimal electricNumber, BigDecimal waterNumber, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.electricNumber = electricNumber;
        this.waterNumber = waterNumber;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getElectricNumber() {
        return electricNumber;
    }

    public void setElectricNumber(BigDecimal electricNumber) {
        this.electricNumber = electricNumber;
    }

    public BigDecimal getWaterNumber() {
        return waterNumber;
    }

    public void setWaterNumber(BigDecimal waterNumber) {
        this.waterNumber = waterNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

