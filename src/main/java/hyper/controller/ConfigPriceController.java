package hyper.controller;

import hyper.model.ConfigPrice;
import hyper.model.Customer;
import hyper.service.ConfigPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class ConfigPriceController {
    private final ConfigPriceService configPriceService;

    public ConfigPriceController(ConfigPriceService configPriceService) {
        this.configPriceService = configPriceService;
    }

    @GetMapping("/config-prices")
    @Operation(summary = "Retrieve all config prices", description = "Requires a valid bearer token")
    public Page<ConfigPrice> retrieveAllConfigPrices(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return configPriceService.retrieveAllConfigPrices(PageRequest.of(page, size));
    }

    @GetMapping("/config-prices/{id}")
    @Operation(summary = "Find config price by id", description = "Requires a valid bearer token")
    public EntityModel<ConfigPrice> retrieveConfigPrice(@PathVariable UUID id) {
        ConfigPrice configPrice = configPriceService.retrieveConfigPrice(id);
        EntityModel<ConfigPrice> resource = EntityModel.of(configPrice);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllConfigPrices(0, 10));
        resource.add(linkTo.withRel("all-configPrice"));

        return resource;
    }

    @GetMapping("/config-prices/{id}")
    @Operation(summary = "Delete config price", description = "Requires a valid bearer token")
    public void deleteConfigPrice(@PathVariable UUID id) {
        configPriceService.deleteConfigPrice(id);
    }

    @PostMapping("/config-prices")
    @Operation(summary = "Create config price", description = "Requires a valid bearer token")
    public ResponseEntity<Void> createConfigPrice(@Valid @RequestBody ConfigPrice configPrice) {
        var newConfigPrice = configPriceService.createConfigPrice(configPrice);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newConfigPrice.getId()).toUri();

        return ResponseEntity.created(location).build();
    }
    @PutMapping("/config-prices/{id}")
    @Operation(summary = "Update a config prices", description = "Requires a valid bearer token")
    public ResponseEntity<Void> updateConfigPrice(@RequestBody ConfigPrice configPrice,
                                               @PathVariable UUID id) {
        if (!configPriceService.updateConfigPrice(id, configPrice))
            return ResponseEntity.notFound().build();

        return ResponseEntity.noContent().build();
    }
}
