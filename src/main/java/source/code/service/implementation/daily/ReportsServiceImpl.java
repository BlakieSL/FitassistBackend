package source.code.service.implementation.daily;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import source.code.dto.pojo.DateFoodMacros;
import source.code.dto.pojo.FoodMacros;
import source.code.dto.response.activity.ActivityCalculatedResponseDto;
import source.code.dto.response.reports.DailyReportResponseDto;
import source.code.dto.response.reports.PeriodicReportResponseDto;
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

		return createPeriodicReport(dailyReports);
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

	private PeriodicReportResponseDto createPeriodicReport(List<DailyReportResponseDto> dailyReports) {
		int totalDays = dailyReports.size();

		BigDecimal avgCaloriesConsumed = dailyReports.stream()
			.map(DailyReportResponseDto::getTotalCaloriesConsumed)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		BigDecimal avgCaloriesBurned = dailyReports.stream()
			.map(DailyReportResponseDto::getTotalCaloriesBurned)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		BigDecimal avgNetCalories = dailyReports.stream()
			.map(DailyReportResponseDto::getNetCalories)
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		FoodMacros avgMacros = calculateAverageMacros(dailyReports, totalDays);

		return new PeriodicReportResponseDto(dailyReports, avgCaloriesConsumed, avgCaloriesBurned, avgNetCalories,
				avgMacros);
	}

	private FoodMacros calculateAverageMacros(List<DailyReportResponseDto> dailyReports, int totalDays) {
		BigDecimal avgProtein = dailyReports.stream()
			.map(report -> report.getMacros().getProtein())
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		BigDecimal avgFat = dailyReports.stream()
			.map(report -> report.getMacros().getFat())
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		BigDecimal avgCarbs = dailyReports.stream()
			.map(report -> report.getMacros().getCarbohydrates())
			.reduce(BigDecimal.ZERO, BigDecimal::add)
			.divide(BigDecimal.valueOf(totalDays), 2, RoundingMode.HALF_UP);

		return FoodMacros.of(null, avgProtein, avgFat, avgCarbs);
	}

}
