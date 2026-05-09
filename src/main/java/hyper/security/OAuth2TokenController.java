package hyper.security;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Hidden
@RestController
public class OAuth2TokenController {

    private final OAuth2ClientProperties clientProperties;
    private final JwtEncoder jwtEncoder;

    public OAuth2TokenController(OAuth2ClientProperties clientProperties, JwtEncoder jwtEncoder) {
        this.clientProperties = clientProperties;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> token(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam("grant_type") String grantType,
            HttpServletRequest request) {
        validateClient(authorization);

        if (!"client_credentials".equals(grantType)) {
            throw new OAuth2Exception("unsupported_grant_type", "Only client_credentials is supported");
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(clientProperties.tokenValiditySeconds());
        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer(request.getRequestURL().toString())
                        .subject(clientProperties.clientId())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .build()
        )).getTokenValue();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("access_token", accessToken);
        response.put("token_type", "Bearer");
        response.put("expires_in", clientProperties.tokenValiditySeconds());
        return response;
    }

    private void validateClient(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Basic ")) {
            throw new OAuth2Exception("invalid_client", "Missing Basic client credentials");
        }

        String credentials = new String(Base64.getDecoder()
                .decode(authorization.substring("Basic ".length())), StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);
        if (values.length != 2
                || !clientProperties.clientId().equals(values[0])
                || !clientProperties.clientSecret().equals(values[1])) {
            throw new OAuth2Exception("invalid_client", "Client authentication failed");
        }
    }

    @ExceptionHandler(OAuth2Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleOauthError(RuntimeException exception) {
        OAuth2Exception oauth2Exception = (OAuth2Exception) exception;
        return Map.of(
                "error", oauth2Exception.error(),
                "error_description", oauth2Exception.getMessage()
        );
    }

    private static final class OAuth2Exception extends RuntimeException {
        private final String error;

        private OAuth2Exception(String error, String message) {
            super(message);
            this.error = error;
        }

        private String error() {
            return error;
        }
    }
}
