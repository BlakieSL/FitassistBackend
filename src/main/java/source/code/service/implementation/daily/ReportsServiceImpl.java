package source.code.service.implementation.daily;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import source.code.dto.pojo.DateFoodMacros;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.pojo.ReportStats;
import source.code.dto.response.reports.DailyReportResponseDto;
import source.code.dto.response.reports.PeriodicReportResponseDto;
import source.code.dto.response.reports.UserActionCountsDto;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.FoodMapper;
import source.code.mapper.daily.DailyActivityMapper;
import source.code.model.daily.DailyCart;
import source.code.repository.DailyCartRepository;
import source.code.service.declaration.daily.ReportsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportsServiceImpl implements ReportsService {

	private final DailyCartRepository dailyCartRepository;

	private final DailyActivityMapper dailyActivityMapper;

	private final FoodMapper foodMapper;

	public ReportsServiceImpl(DailyCartRepository dailyCartRepository, DailyActivityMapper dailyActivityMapper,
			FoodMapper foodMapper) {
		this.dailyCartRepository = dailyCartRepository;
		this.dailyActivityMapper = dailyActivityMapper;
		this.foodMapper = foodMapper;
	}

	@Override
	public DailyReportResponseDto getDailyReport(LocalDate date) {
		int userId = AuthorizationUtil.getUserId();

		FoodMacros foodMacros = dailyCartRepository.findAggregatedFoodMacrosByUserIdAndDate(userId, date)
			.orElse(FoodMacros.zero());

		BigDecimal caloriesBurned = dailyCartRepository.findByUserIdAndDateWithActivityAssociations(userId, date)
			.map(cart -> cart.getDailyCartActivities()
				.stream()
				.map(dailyActivityMapper::toActivityCalculatedResponseDto)
				.map(ActivityCalculatedResponseDto::getCaloriesBurned)
				.reduce(BigDecimal.ZERO, BigDecimal::add))
			.orElse(BigDecimal.ZERO);

		BigDecimal netCalories = foodMacros.getCalories().subtract(caloriesBurned);

		return new DailyReportResponseDto(date, foodMacros.getCalories(), caloriesBurned, netCalories, foodMacros);
	}

	@Override
	public PeriodicReportResponseDto getPeriodicReport(LocalDate startDate, LocalDate endDate)
			throws BadRequestException {
		if (startDate.isAfter(endDate)) {
			throw new BadRequestException("Start date must be before or equal to end date");
		}

		int userId = AuthorizationUtil.getUserId();

		Map<LocalDate, DateFoodMacros> foodMacrosByDate = dailyCartRepository
			.findAggregatedFoodMacrosByUserIdAndDateRange(userId, startDate, endDate)
			.stream()
			.collect(Collectors.toMap(DateFoodMacros::getDate, macros -> macros));

		Map<LocalDate, BigDecimal> caloriesBurnedByDate = dailyCartRepository
			.findByUserIdAndDateRangeWithActivityAssociations(userId, startDate, endDate)
			.stream()
			.collect(Collectors.toMap(DailyCart::getDate,
					cart -> cart.getDailyCartActivities()
						.stream()
						.map(dailyActivityMapper::toActivityCalculatedResponseDto)
						.map(ActivityCalculatedResponseDto::getCaloriesBurned)
						.reduce(BigDecimal.ZERO, BigDecimal::add)));

		List<DailyReportResponseDto> dailyReports = startDate.datesUntil(endDate.plusDays(1))
			.map(date -> createDailyReport(date, foodMacrosByDate.get(date), caloriesBurnedByDate.get(date)))
			.collect(Collectors.toList());

		return calculatePeriodicReport(dailyReports);
	}

	private DailyReportResponseDto createDailyReport(LocalDate date, DateFoodMacros dateFoodMacros,
			BigDecimal caloriesBurned) {
		FoodMacros foodMacros = Optional.ofNullable(dateFoodMacros)
			.map(foodMapper::toFoodMacros)
			.orElse(FoodMacros.zero());
		BigDecimal calories = Optional.ofNullable(caloriesBurned).orElse(BigDecimal.ZERO);
		BigDecimal netCalories = foodMacros.getCalories().subtract(calories);

		return new DailyReportResponseDto(date, foodMacros.getCalories(), calories, netCalories, foodMacros);
	}

	private PeriodicReportResponseDto calculatePeriodicReport(List<DailyReportResponseDto> dailyReports) {
		int totalDays = dailyReports.size();

		DailyReportResponseDto first = dailyReports.getFirst();

		BigDecimal sumConsumed = first.getTotalCaloriesConsumed();
		BigDecimal maxConsumed = first.getTotalCaloriesConsumed();
		BigDecimal minConsumed = first.getTotalCaloriesConsumed();

		BigDecimal sumBurned = first.getTotalCaloriesBurned();
		BigDecimal maxBurned = first.getTotalCaloriesBurned();
		BigDecimal minBurned = first.getTotalCaloriesBurned();

		BigDecimal sumNet = first.getNetCalories();
		BigDecimal maxNet = first.getNetCalories();
		BigDecimal minNet = first.getNetCalories();

		BigDecimal sumProtein = first.getMacros().getProtein();
		BigDecimal sumFat = first.getMacros().getFat();
		BigDecimal sumCarbs = first.getMacros().getCarbohydrates();

		for (int i = 1; i < totalDays; i++) {
			DailyReportResponseDto report = dailyReports.get(i);
			BigDecimal consumed = report.getTotalCaloriesConsumed();
			BigDecimal burned = report.getTotalCaloriesBurned();
			BigDecimal net = report.getNetCalories();
			BigDecimal protein = report.getMacros().getProtein();
			BigDecimal fat = report.getMacros().getFat();
			BigDecimal carbs = report.getMacros().getCarbohydrates();

			sumConsumed = sumConsumed.add(consumed);
			maxConsumed = maxConsumed.max(consumed);
			minConsumed = minConsumed.min(consumed);

			sumBurned = sumBurned.add(burned);
			maxBurned = maxBurned.max(burned);
			minBurned = minBurned.min(burned);

			sumNet = sumNet.add(net);
			maxNet = maxNet.max(net);
			minNet = minNet.min(net);

			sumProtein = sumProtein.add(protein);
			sumFat = sumFat.add(fat);
			sumCarbs = sumCarbs.add(carbs);
		}

		BigDecimal totalDaysDivisor = BigDecimal.valueOf(totalDays);

		BigDecimal avgConsumed = sumConsumed.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);
		BigDecimal avgBurned = sumBurned.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);
		BigDecimal avgNet = sumNet.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);

		BigDecimal avgProtein = sumProtein.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);
		BigDecimal avgFat = sumFat.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);
		BigDecimal avgCarbs = sumCarbs.divide(totalDaysDivisor, 2, RoundingMode.HALF_UP);

		ReportStats stats = new ReportStats(avgConsumed, maxConsumed, minConsumed, avgBurned, maxBurned, minBurned,
				avgNet, maxNet, minNet);
		FoodMacros avgMacros = new FoodMacros(null, avgProtein, avgFat, avgCarbs);

		return new PeriodicReportResponseDto(dailyReports, avgMacros, stats);
	}

	@Override
	public List<UserActionCountsDto> getHeatmap() {
		int userId = AuthorizationUtil.getUserId();
		LocalDate endDate = LocalDate.now();
		LocalDate startDate = endDate.minusYears(1);

		return dailyCartRepository.findActionCountsByUserIdAndDateRange(userId, startDate, endDate)
			.stream()
			.map(ac -> new UserActionCountsDto(ac.getDate(), ac.getFoodLogsCount() + ac.getActivityLogsCount(),
					ac.getFoodLogsCount(), ac.getActivityLogsCount()))
			.collect(Collectors.toList());
	}

}
