package hyper.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "\"configPrice\"")
@Schema(description = "Details about config price table")
public class ConfigPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    public ConfigPrice(){}
    public ConfigPrice(UUID id, String description, String code, BigDecimal price){
        this.id = id;
        this.code = code;
        this.description= description;
        this.price = price;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
