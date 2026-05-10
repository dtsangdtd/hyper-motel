package hyper.auth;

import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID id
) {
}