package com.fitassist.backend.gatling.daily;

import com.fitassist.backend.gatling.BaseSimulation;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import java.time.LocalDate;
import java.util.Map;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DailyActivitySimulation extends BaseSimulation {

	private ChainBuilder assignDate = exec(
			session -> session.set("userDate", LocalDate.of(2025, 1, 1).plusDays(session.userId() % 365).toString()));

	private ChainBuilder addActivity = exec(http("Add Activity").post("/api/daily-activities/add/25")
		.header("X-XSRF-TOKEN", "#{xsrfToken}")
		.body(StringBody(session -> toJson(Map.of("time", 30, "weight", 70.5, "date", session.getString("userDate")))))
		.check(status().is(201))).exec(refreshXsrf);

	private ChainBuilder getActivities = exec(
			http("Get Activities").get(session -> "/api/daily-activities/" + session.getString("userDate"))
				.check(status().is(200))
				.check(jsonPath("$.activities[-1].dailyItemId").saveAs("dailyActivityItemId")))
		.exec(refreshXsrf);

	private ChainBuilder patchActivity = exec(http("Patch Activity")
		.patch("/api/daily-activities/modify-activity/#{dailyActivityItemId}")
		.header("X-XSRF-TOKEN", "#{xsrfToken}")
		.body(StringBody(session -> toJson(Map.of("time", 45, "weight", 75.0, "date", session.getString("userDate")))))
		.check(status().is(204))).exec(refreshXsrf);

	private ChainBuilder deleteActivity = exec(
			http("Delete Activity").delete("/api/daily-activities/remove/#{dailyActivityItemId}")
				.header("X-XSRF-TOKEN", "#{xsrfToken}")
				.check(status().is(204)))
		.exec(refreshXsrf);

	private ScenarioBuilder smoke = scenario("Daily Activity Smoke").exec(assignDate, authenticate, addActivity,
			getActivities);

	private ScenarioBuilder load = scenario("Daily Activity Load").exec(assignDate, authenticate, addActivity,
			getActivities, patchActivity, deleteActivity);

	{
		setUp(smoke.injectOpen(atOnceUsers(1)),
				load.injectOpen(rampUsers(50).during(60), constantUsersPerSec(10).during(120)))
			.protocols(httpProtocol)
			.assertions(global().responseTime().percentile(95).lt(500),
					global().successfulRequests().percent().gt(99.0));
	}

}
