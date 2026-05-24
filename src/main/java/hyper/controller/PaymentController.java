package hyper.controller;

import hyper.model.Payment;
import hyper.service.PaymentService;
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
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

	private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

	@GetMapping("/payments")
    @Operation(summary = "Retrieve all payments")
	public Page<Payment> retrieveAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
		return paymentService.retrieveAllPayments(PageRequest.of(page, size));
	}

	@GetMapping("/payments/{id}")
	@Operation(summary = "Find payment by id, also returns a link to retrieve all payments with rel - all-payments")
	public EntityModel<Payment> retrievePayment(@PathVariable UUID id) {
		Payment payment = paymentService.retrievePayment(id);

		EntityModel<Payment> resource = EntityModel.of(payment);
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllPayments(0, 10));
		resource.add(linkTo.withRel("all-payments"));

		return resource;
	}

	@DeleteMapping("/payments/{id}")
    @Operation(summary = "Delete a payment")
	public void deletePayment(@PathVariable UUID id) {
		paymentService.deletePayment(id);
	}

	@PostMapping("/payments")
    @Operation(summary = "Create a payment")
	public ResponseEntity<Void> createPayment(@Valid @RequestBody Payment payment) {
		var newPayment = paymentService.createPayment(payment);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newPayment.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}

	@PutMapping("/payments/{id}")
    @Operation(summary = "Update a payment")
	public ResponseEntity<Void> updatePayment(@RequestBody Payment payment,
                                                @PathVariable UUID id) {
		if (!paymentService.updatePayment(id, payment))
            return ResponseEntity.notFound().build();

		return ResponseEntity.noContent().build();
	}
}

