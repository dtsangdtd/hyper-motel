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
	void customersEndpointRequiresOAuth2Token() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/customers"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
	}

	@Test
	void registeredUserCanAccessCustomersEndpoint() throws Exception {
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

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/customers")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.content").isArray());
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
	void authenticatedUserCanCreateCustomer() throws Exception {
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
						org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/customers")
								.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
								.contentType(org.springframework.http.MediaType.APPLICATION_JSON)
								.content("""
										{
										  "roomId": "00000000-0000-0000-0000-000000000001",
										  "identityNumber": "P1234567",
										  "firstName": "David",
										  "lastName": "Miller"
										}
										"""))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.header().string("Location", org.hamcrest.Matchers.matchesPattern(".*/customers/[0-9a-fA-F\\-]{36}$")));
	}
}
