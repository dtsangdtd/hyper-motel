package hyper.auth;

import hyper.exception.ServiceUnavailableException;
import hyper.security.OAuth2ClientProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private static final String CIRCUIT_BREAKER_NAME = "authService";

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final OAuth2ClientProperties clientProperties;

    public AuthService(AppUserRepository appUserRepository,
                       PasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       OAuth2ClientProperties clientProperties) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.clientProperties = clientProperties;
    }

    @Transactional
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "registerFallback")
    public AuthResponse register(AuthRequest request) {
        if (appUserRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new AuthException("Phone number already exists");
        }

        AppUser user = new AppUser(request.phoneNumber(), passwordEncoder.encode(request.password()));
        appUserRepository.save(user);
        return issueToken(user.getPhoneNumber());
    }

    @Transactional(readOnly = true)
    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "loginFallback")
    public AuthResponse login(AuthRequest request) {
        AppUser user = appUserRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new AuthException("Invalid phone number or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid phone number or password");
        }

        return issueToken(user.getPhoneNumber());
    }

    private AuthResponse issueToken(String phoneNumber) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(clientProperties.tokenValiditySeconds());
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer("self")
                        .subject(phoneNumber)
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .build()
        )).getTokenValue();

        return new AuthResponse(token, "Bearer", clientProperties.tokenValiditySeconds(), phoneNumber);
    }

    private AuthResponse registerFallback(AuthRequest request, Throwable throwable) {
        throw new ServiceUnavailableException("Registration is temporarily unavailable", throwable);
    }

    private AuthResponse loginFallback(AuthRequest request, Throwable throwable) {
        throw new ServiceUnavailableException("Login is temporarily unavailable", throwable);
    }
}