package hyper.controller;

import hyper.model.Room;
import hyper.service.RoomService;
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
public class RoomController {

	private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

	@GetMapping("/rooms")
    @Operation(summary = "Retrieve all rooms")
	public Page<Room> retrieveAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
		return roomService.retrieveAllRooms(PageRequest.of(page, size));
	}

	@GetMapping("/rooms/{id}")
	@Operation(summary = "Find room by id, also returns a link to retrieve all rooms with rel - all-rooms")
	public EntityModel<Room> retrieveRoom(@PathVariable UUID id) {
		Room room = roomService.retrieveRoom(id);

		EntityModel<Room> resource = EntityModel.of(room);
		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllRooms(0, 10));
		resource.add(linkTo.withRel("all-rooms"));

		return resource;
	}

	@DeleteMapping("/rooms/{id}")
    @Operation(summary = "Delete a room")
	public void deleteRoom(@PathVariable UUID id) {
		roomService.deleteRoom(id);
	}

	@PostMapping("/rooms")
    @Operation(summary = "Create a room")
	public ResponseEntity<Void> createRoom(@Valid @RequestBody Room room) {
		var newRoom = roomService.createRoom(room);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(newRoom.getId())
				.toUri();

		return ResponseEntity.created(location).build();

	}

	@PutMapping("/rooms/{id}")
    @Operation(summary = "Update a room")
	public ResponseEntity<Void> updateRoom(@RequestBody Room room,
                                               @PathVariable UUID id) {
		if (!roomService.updateRoom(id, room))
            return ResponseEntity.notFound().build();

		return ResponseEntity.noContent().build();
	}
}

