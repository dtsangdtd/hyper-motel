package hyper.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableConfigurationProperties(OAuth2ClientProperties.class)
class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/oauth/token", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/customers/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/customers/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/customers/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/customers/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/rooms/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/rooms/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/rooms/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/rooms/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/payments/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/payments/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.security.jwt.secret}") String secret) {
        return NimbusJwtDecoder.withSecretKey(secretKey(secret)).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${app.security.jwt.secret}") String secret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(secret)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private SecretKey secretKey(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
