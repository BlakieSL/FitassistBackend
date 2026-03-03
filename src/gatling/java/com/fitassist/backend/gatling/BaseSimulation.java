package com.fitassist.backend.gatling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.util.Map;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public abstract class BaseSimulation extends Simulation {

	protected static final ObjectMapper MAPPER = new ObjectMapper();

	protected String baseUrl = System.getProperty("GATLING_BASE_URL", "http://localhost:8000");

	protected String username = System.getProperty("GATLING_USERNAME", "performance_test@example.com");

	protected String password = System.getProperty("GATLING_PASSWORD", "Secured!123");

	protected HttpProtocolBuilder httpProtocol = http.baseUrl(baseUrl)
		.acceptHeader("application/json")
		.contentTypeHeader("application/json");

	protected ChainBuilder refreshXsrf = exec(getCookieValue(CookieKey("XSRF-TOKEN").saveAs("xsrfToken")));

	protected ChainBuilder authenticate = exec(http("Login").post("/api/users/login")
		.body(StringBody(toJson(Map.of("username", username, "password", password))))
		.check(status().is(200))).exec(getCookieValue(CookieKey("accessToken").saveAs("accessToken")))
		.exec(refreshXsrf);

	protected static String toJson(Object obj) {
		try {
			return MAPPER.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
