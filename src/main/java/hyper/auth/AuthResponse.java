package hyper.auth;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        String phoneNumber
) {
}