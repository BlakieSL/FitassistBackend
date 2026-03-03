package com.fitassist.backend.gatling.daily;

import com.fitassist.backend.gatling.BaseSimulation;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.LocalDate;
import java.util.Map;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DailyFoodSimulation extends BaseSimulation {

	private ChainBuilder assignDate = exec(
			session -> session.set("userDate", LocalDate.of(2025, 1, 1).plusDays(session.userId() % 365).toString()));

	private ChainBuilder addFood = exec(http("Add Food").post("/api/cart/add/1")
		.header("X-XSRF-TOKEN", "#{xsrfToken}")
		.body(StringBody(session -> toJson(Map.of("quantity", 150.0, "date", session.getString("userDate")))))
		.check(status().is(201))).exec(refreshXsrf);

	private ChainBuilder getCart = exec(http("Get Cart").get(session -> "/api/cart/" + session.getString("userDate"))
		.check(status().is(200))
		.check(jsonPath("$.foods[-1].dailyItemId").saveAs("dailyCartFoodId"))).exec(refreshXsrf);

	private ChainBuilder patchFood = exec(http("Patch Food").patch("/api/cart/update/#{dailyCartFoodId}")
		.header("X-XSRF-TOKEN", "#{xsrfToken}")
		.body(StringBody(session -> toJson(Map.of("quantity", 200.0, "date", session.getString("userDate")))))
		.check(status().is(204))).exec(refreshXsrf);

	private ChainBuilder deleteFood = exec(http("Delete Food").delete("/api/cart/remove/#{dailyCartFoodId}")
		.header("X-XSRF-TOKEN", "#{xsrfToken}")
		.check(status().is(204))).exec(refreshXsrf);

	private ScenarioBuilder smoke = scenario("Daily Food Smoke").exec(assignDate, authenticate, addFood, getCart);

	private ScenarioBuilder load = scenario("Daily Food Load").exec(assignDate, authenticate, addFood, getCart,
			patchFood, deleteFood);

	{
		setUp(smoke.injectOpen(atOnceUsers(1)),
				load.injectOpen(rampUsers(50).during(60), constantUsersPerSec(10).during(120)))
			.protocols(httpProtocol)
			.assertions(global().responseTime().percentile(95).lt(500),
					global().successfulRequests().percent().gt(99.0));
	}

}
