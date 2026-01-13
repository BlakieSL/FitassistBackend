package source.code.integration.test.controller.daily;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import source.code.integration.config.MockAwsS3Config;
import source.code.integration.config.MockAwsSesConfig;
import source.code.integration.config.MockRedisConfig;
import source.code.integration.containers.MySqlContainerInitializer;
import source.code.integration.test.controller.food.FoodSql;
import source.code.integration.utils.TestSetup;
import source.code.integration.utils.Utils;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestSetup
@Import({ MockAwsS3Config.class, MockRedisConfig.class, MockAwsSesConfig.class })
@TestPropertySource(properties = "schema.name=general")
@ContextConfiguration(initializers = { MySqlContainerInitializer.class })
public class ReportsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@FoodSql
	@Test
	@DisplayName("GET - /daily/{date} - Should return daily report with food and activity data")
	void getDailyReportWithData() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/reports/daily/2025-04-01"))
			.andExpectAll(status().isOk(), jsonPath("$.date").value("2025-04-01"),
					jsonPath("$.totalCaloriesConsumed").isNumber(), jsonPath("$.totalCaloriesBurned").isNumber(),
					jsonPath("$.netCalories").isNumber(), jsonPath("$.macros").exists(),
					jsonPath("$.macros.carbohydrates").isNumber(), jsonPath("$.macros.protein").isNumber(),
					jsonPath("$.macros.fat").isNumber());
	}

	@FoodSql
	@Test
	@DisplayName("GET - /daily/{date} - Should return daily report with only food data when no activities")
	void getDailyReportOnlyFood() throws Exception {
		Utils.setUserContext(2);

		mockMvc.perform(get("/api/reports/daily/2025-04-01"))
			.andExpectAll(status().isOk(), jsonPath("$.date").value("2025-04-01"),
					jsonPath("$.totalCaloriesConsumed").isNumber(), jsonPath("$.totalCaloriesBurned").value(0),
					jsonPath("$.netCalories").isNumber());
	}

	@Test
	@DisplayName("GET - /daily/{date} - Should return daily report with zeros when no data exists")
	void getDailyReportNoData() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/reports/daily/2025-12-31"))
			.andExpectAll(status().isOk(), jsonPath("$.date").value("2025-12-31"),
					jsonPath("$.totalCaloriesConsumed").value(0), jsonPath("$.totalCaloriesBurned").value(0),
					jsonPath("$.netCalories").value(0));
	}

	@FoodSql
	@Test
	@DisplayName("GET - /periodic/ - Should return periodic report for date range")
	void getPeriodicReportWithData() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/reports/periodic/").param("fromDate", "2025-04-01").param("toDate", "2025-04-03"))
			.andExpectAll(status().isOk(), jsonPath("$.dailyReports.length()").value(3),
					jsonPath("$.dailyReports[0].date").value("2025-04-01"),
					jsonPath("$.dailyReports[0].totalCaloriesConsumed").isNumber(),
					jsonPath("$.dailyReports[0].totalCaloriesBurned").isNumber(),
					jsonPath("$.dailyReports[0].netCalories").isNumber(),
					jsonPath("$.dailyReports[1].date").value("2025-04-02"),
					jsonPath("$.dailyReports[2].date").value("2025-04-03"),
					jsonPath("$.avgCaloriesConsumed").isNumber(), jsonPath("$.avgCaloriesBurned").isNumber(),
					jsonPath("$.avgNetCalories").isNumber(), jsonPath("$.avgMacros.protein").isNumber(),
					jsonPath("$.avgMacros.fat").isNumber(), jsonPath("$.avgMacros.carbohydrates").isNumber());
	}

	@FoodSql
	@Test
	@DisplayName("GET - /periodic/ - Should return periodic report whne for some days no data exists")
	void getPeriodicReportSomeWithoutData() throws Exception {
		Utils.setUserContext(2);

		mockMvc.perform(get("/api/reports/periodic/").param("fromDate", "2025-03-31").param("toDate", "2025-04-02"))
			.andExpectAll(status().isOk(), jsonPath("$.dailyReports.length()").value(3),
					jsonPath("$.dailyReports[0].date").value("2025-03-31"),
					jsonPath("$.dailyReports[0].totalCaloriesConsumed").value(0),
					jsonPath("$.dailyReports[0].totalCaloriesBurned").value(0),
					jsonPath("$.dailyReports[1].date").value("2025-04-01"),
					jsonPath("$.dailyReports[1].totalCaloriesConsumed").isNumber(),
					jsonPath("$.avgCaloriesConsumed").isNumber());
	}

	@Test
	@DisplayName("GET - /periodic/ - Should return periodic report with all zeros when no data exists")
	void getPeriodicReportNoData() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/reports/periodic/").param("fromDate", "2025-12-01").param("toDate", "2025-12-03"))
			.andExpectAll(status().isOk(), jsonPath("$.dailyReports.length()").value(3),
					jsonPath("$.dailyReports[0].totalCaloriesConsumed").value(0),
					jsonPath("$.dailyReports[0].totalCaloriesBurned").value(0),
					jsonPath("$.dailyReports[0].netCalories").value(0), jsonPath("$.avgCaloriesConsumed").value(0),
					jsonPath("$.avgCaloriesBurned").value(0), jsonPath("$.avgNetCalories").value(0));
	}

	@FoodSql
	@Test
	@DisplayName("GET - /periodic/ - Should return periodic report for single day")
	void getPeriodicReportSingleDay() throws Exception {
		Utils.setUserContext(1);

		mockMvc.perform(get("/api/reports/periodic/").param("fromDate", "2025-04-01").param("toDate", "2025-04-01"))
			.andExpectAll(status().isOk(), jsonPath("$.dailyReports.length()").value(1),
					jsonPath("$.dailyReports[0].date").value("2025-04-01"),
					jsonPath("$.dailyReports[0].totalCaloriesConsumed").isNumber(),
					jsonPath("$.dailyReports[0].totalCaloriesBurned").isNumber(),
					jsonPath("$.avgCaloriesConsumed").isNumber());
	}

}
