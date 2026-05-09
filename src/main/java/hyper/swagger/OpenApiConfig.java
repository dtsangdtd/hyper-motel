package hyper.swagger;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Reference Links:
 * <ul>
 *   <li><a href="https://springdoc.org/faq.html#how-can-i-configure-swagger-ui">
 *       Customize Swagger UI</a></li>
 *   <li><a href="https://stackoverflow.com/questions/59291371/migrating-from-springfox-swagger-2-to-springdoc-open-api">
 *       Migrating from Springfox Swagger 2 to Springdoc OpenAPI (Stack Overflow)</a></li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI awesomeAPI() {
        return new OpenAPI()
                .info(new Info().title("Awesome API Title")
                        .description("Awesome API Description with JWT register/login example")
                        .version("1.0")
                        .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0")))
                .components(new Components().addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Register or login via /auth, then paste the JWT here")))
                .externalDocs(new ExternalDocumentation()
                        .description("Ranga Karanam, in28minutes@gmail.com")
                        .url("http://www.in28minutes.com"));
    }

}
