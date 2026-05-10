package hyper.repository;

import hyper.model.ConfigPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConfigPriceRepository extends JpaRepository<ConfigPrice, UUID> {
}
