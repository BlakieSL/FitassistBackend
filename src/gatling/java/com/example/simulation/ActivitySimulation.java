package com.example.simulation;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

public class ActivitySimulation extends Simulation {

  HttpProtocolBuilder httpProtocol = http
          .baseUrl("http://localhost:8000")
          .acceptHeader("application/json")
          .userAgentHeader("Gatling/PerformanceTest");

  ScenarioBuilder scn = scenario("Get All Activities")
          .exec(
                  http("Get All Activities")
                          .get("/api/activities")
                          .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZXIyQGdtYWlsLmNvbSIsImV4cCI6MTcyODkxMzE3MSwidG9rZW5UeXBlIjoiQUNDRVNTIiwiaWF0IjoxNzI4OTEyMjcxLCJ1c2VySWQiOjI1LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXX0.Whbl1sDb8UkrWcws4-Xf3XC91pVWrzhavUh3DUBNM1c")
                          .check(status().is(200)))
          .pause(1);

  {
    setUp(
            scn.injectOpen(rampUsers(5000).during(10)))
            .protocols(httpProtocol);
  }
}
