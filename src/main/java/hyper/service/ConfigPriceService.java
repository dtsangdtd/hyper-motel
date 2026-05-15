package hyper.service;

import hyper.exception.ConfigPriceNotFoundException;
import hyper.model.ConfigPrice;
import hyper.model.Customer;
import hyper.repository.ConfigPriceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ConfigPriceService {
    private final ConfigPriceRepository configPriceRepository;

    public ConfigPriceService(ConfigPriceRepository configPriceRepository) {
        this.configPriceRepository = configPriceRepository;
    }

    public Page<ConfigPrice> retrieveAllConfigPrices(Pageable pageable) {
        return configPriceRepository.findAll(pageable);
    }

    public ConfigPrice retrieveConfigPrice(UUID id) {
        return configPriceRepository.findById(id).orElseThrow(() -> new ConfigPriceNotFoundException("id-" + id));
    }

    public void deleteConfigPrice(UUID id) {
        configPriceRepository.deleteById(id);
    }
    public ConfigPrice createConfigPrice(ConfigPrice configPrice) {
        configPrice.setId(null);
        return configPriceRepository.save(configPrice);
    }
    public boolean updateConfigPrice(UUID id, ConfigPrice configPrice) {
        if (configPriceRepository.findById(id).isEmpty()) {
            return false;
        }
        configPrice.setId(id);
        configPriceRepository.save(configPrice);
        return true;

    }
}
