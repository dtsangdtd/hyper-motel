package hyper.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.oauth2")
public record OAuth2ClientProperties(
        String clientId,
        String clientSecret,
        long tokenValiditySeconds
) {
}
