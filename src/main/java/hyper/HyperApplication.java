package hyper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class HyperApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(HyperApplication.class);

	public static void main(String[] args) {
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
		SpringApplication.run(HyperApplication.class, args);
		LOGGER.info("Application run successfully");
	}

	@Bean
	ApplicationRunner databaseConnectionLogger(DataSource dataSource) {
		return args -> {
			try (Connection connection = dataSource.getConnection()) {
				LOGGER.info("Database connection successful: url={}, user={}",
						connection.getMetaData().getURL(),
						connection.getMetaData().getUserName());
			}
		};
	}
}
