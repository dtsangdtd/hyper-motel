package hyper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class HyperApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void contextLoads() {
	}

	@Test
	void studentsEndpointRequiresOAuth2Token() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/students"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	void registeredUserCanAccessStudentsEndpoint() throws Exception {
		String tokenResponse = mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/register")
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "username": "alice",
										  "password": "password123"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
				.andReturn()
				.getResponse()
				.getContentAsString();

		String accessToken = tokenResponse.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/students")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].name").value("Ranga"));
	}

	@Test
	void loginReturnsTokenForExistingUser() throws Exception {
		mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/register")
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "username": "bob",
										  "password": "password123"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated());

		mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "username": "bob",
										  "password": "password123"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.username").value("bob"));
	}

	@Test
	void authenticatedUserCanCreateStudent() throws Exception {
		String tokenResponse = mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/register")
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "username": "charlie",
										  "password": "password123"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		String accessToken = tokenResponse.replaceAll(".*\"accessToken\":\"([^\"]+)\".*", "$1");

		mockMvc.perform(
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/students")
								.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "id": "99999999-9999-9999-9999-999999999999",
										  "name": "David",
										  "passportNumber": "P1234567"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/students/[0-9a-fA-F\\-]{36}$")));
	}
}
