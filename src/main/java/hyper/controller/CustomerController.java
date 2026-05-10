package hyper.controller;

import hyper.model.Customer;
import hyper.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

	private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

	@GetMapping("/customers")
    @Operation(summary = "Retrieve all customer", description = "Requires a valid bearer token")
	public Page<Customer> retrieveAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
		return customerService.retrieveAllCustomers(PageRequest.of(page, size));
	}

	@GetMapping("/customers/{id}")
	@Operation(summary = "Find customer by id, also returns a link to retrieve all students with rel - all-students",
            description = "Requires a valid bearer token")
	public EntityModel<Customer> retrieveCustomer(@PathVariable UUID id) {
		Customer customer = customerService.retrieveCustomer(id);

		EntityModel<Customer> resource = EntityModel.of(customer);
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllCustomers(0, 10));
		resource.add(linkTo.withRel("all-students"));

		return resource;
	}

	@DeleteMapping("/customers/{id}")
    @Operation(summary = "Delete a customers", description = "Requires a valid bearer token")
	public void deleteCustomer(@PathVariable UUID id) {
		customerService.deleteCustomer(id);
	}

	@PostMapping("/customers")
    @Operation(summary = "Create a customer", description = "Requires a valid bearer token")
	public ResponseEntity<Void> createStudent(@Valid @RequestBody Customer customer) {
		var newCustomer = customerService.createCustomer(customer);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newCustomer.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}
	
	@PutMapping("/customers/{id}")
    @Operation(summary = "Update a customer", description = "Requires a valid bearer token")
	public ResponseEntity<Void> updateCustomer(@RequestBody Customer customer,
                                              @PathVariable UUID id) {
		if (!customerService.updateStudent(id, customer))
            return ResponseEntity.notFound().build();

		return ResponseEntity.noContent().build();
	}
}
