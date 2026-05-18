package hyper.service;

import hyper.exception.ServiceUnavailableException;
import hyper.exception.RoomNotFoundException;
import hyper.model.Room;
import hyper.repository.RoomRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoomService {

    private static final String CIRCUIT_BREAKER_NAME = "roomService";

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrieveAllRoomsFallback")
    public Page<Room> retrieveAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "retrieveRoomFallback")
    public Room retrieveRoom(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("id-" + id));
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteRoomFallback")
    public void deleteRoom(UUID id) {
        roomRepository.deleteById(id);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createRoomFallback")
    public Room createRoom(Room room) {
        room.setId(null);
        return roomRepository.save(room);
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateRoomFallback")
    public boolean updateRoom(UUID id, Room room) {
        if (roomRepository.findById(id).isEmpty()) {
            return false;
        }

        room.setId(id);
        roomRepository.save(room);
        return true;
    }

    private Page<Room> retrieveAllRoomsFallback(Pageable pageable, Throwable throwable) {
        throw serviceUnavailable("Room list is temporarily unavailable", throwable);
    }

    private Room retrieveRoomFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Room retrieval is temporarily unavailable for id " + id, throwable);
    }

    private void deleteRoomFallback(UUID id, Throwable throwable) {
        throw serviceUnavailable("Room deletion is temporarily unavailable for id " + id, throwable);
    }

    private Room createRoomFallback(Room room, Throwable throwable) {
        throw serviceUnavailable("Room creation is temporarily unavailable", throwable);
    }

    private boolean updateRoomFallback(UUID id, Room room, Throwable throwable) {
        throw serviceUnavailable("Room update is temporarily unavailable for id " + id, throwable);
    }

    private ServiceUnavailableException serviceUnavailable(String message, Throwable throwable) {
        return new ServiceUnavailableException(message, throwable);
    }
}

