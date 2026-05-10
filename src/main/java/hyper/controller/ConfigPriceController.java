package hyper.controller;

import hyper.model.ConfigPrice;
import hyper.service.ConfigPriceService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class ConfigPriceController {
    private final ConfigPriceService configPriceService;

    public ConfigPriceController(ConfigPriceService configPriceService) {
        this.configPriceService = configPriceService;
    }

    @GetMapping("/config-prices")
    public Page<ConfigPrice> retrieveAllConfigPrices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return configPriceService.retrieveAllConfigPrices(PageRequest.of(page, size));
    }
}
