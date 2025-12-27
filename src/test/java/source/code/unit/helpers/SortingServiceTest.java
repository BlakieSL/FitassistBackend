package source.code.unit.helpers;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.implementation.helpers.SortingServiceImpl;

@ExtendWith(MockitoExtension.class)
public class SortingServiceTest {

	@InjectMocks
	private SortingServiceImpl sortingService;

	@Test
	void comparator_ShouldSortPlanDtosInDescOrder() {
		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

		PlanSummaryDto dto2 = new PlanSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(LocalDateTime.of(2024, 1, 3, 10, 0));

		PlanSummaryDto dto3 = new PlanSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

		Comparator<PlanSummaryDto> comparator = sortingService.comparator(PlanSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<PlanSummaryDto> sorted = Stream.of(dto1, dto2, dto3).sorted(comparator).toList();

		assertEquals(2, sorted.get(0).getId());
		assertEquals(3, sorted.get(1).getId());
		assertEquals(1, sorted.get(2).getId());
	}

	@Test
	void comparator_ShouldSortRecipeDtosInAscOrder() {
		RecipeSummaryDto dto1 = new RecipeSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(LocalDateTime.of(2024, 1, 3, 10, 0));

		RecipeSummaryDto dto2 = new RecipeSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

		RecipeSummaryDto dto3 = new RecipeSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

		Comparator<RecipeSummaryDto> comparator = sortingService.comparator(RecipeSummaryDto::getCreatedAt,
			Sort.Direction.ASC);

		List<RecipeSummaryDto> sorted = Stream.of(dto1, dto2, dto3).sorted(comparator).toList();

		assertEquals(2, sorted.get(0).getId());
		assertEquals(3, sorted.get(1).getId());
		assertEquals(1, sorted.get(2).getId());
	}

	@Test
	void comparator_ShouldHandleNullTimestampsDescOrder() {
		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

		PlanSummaryDto dto2 = new PlanSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(null);

		PlanSummaryDto dto3 = new PlanSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

		PlanSummaryDto dto4 = new PlanSummaryDto();
		dto4.setId(4);
		dto4.setCreatedAt(null);

		Comparator<PlanSummaryDto> comparator = sortingService.comparator(PlanSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<PlanSummaryDto> sorted = Stream.of(dto1, dto2, dto3, dto4).sorted(comparator).toList();

		assertEquals(3, sorted.get(0).getId());
		assertEquals(1, sorted.get(1).getId());
		assertNull(sorted.get(2).getCreatedAt());
		assertNull(sorted.get(3).getCreatedAt());
	}

	@Test
	void comparator_ShouldHandleNullTimestampsAscOrder() {
		RecipeSummaryDto dto1 = new RecipeSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

		RecipeSummaryDto dto2 = new RecipeSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(null);

		RecipeSummaryDto dto3 = new RecipeSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

		RecipeSummaryDto dto4 = new RecipeSummaryDto();
		dto4.setId(4);
		dto4.setCreatedAt(null);

		Comparator<RecipeSummaryDto> comparator = sortingService.comparator(RecipeSummaryDto::getCreatedAt,
			Sort.Direction.ASC);

		List<RecipeSummaryDto> sorted = Stream.of(dto1, dto2, dto3, dto4).sorted(comparator).toList();

		assertEquals(3, sorted.get(0).getId());
		assertEquals(1, sorted.get(1).getId());
		assertNull(sorted.get(2).getCreatedAt());
		assertNull(sorted.get(3).getCreatedAt());
	}

	@Test
	void comparator_ShouldHandleEmptyStream() {
		Comparator<PlanSummaryDto> comparator = sortingService.comparator(PlanSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<PlanSummaryDto> sorted = Stream.<PlanSummaryDto>empty().sorted(comparator).toList();

		assertTrue(sorted.isEmpty());
	}

	@Test
	void comparator_ShouldHandleSingleElement() {
		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

		Comparator<PlanSummaryDto> comparator = sortingService.comparator(PlanSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<PlanSummaryDto> sorted = Stream.of(dto1).sorted(comparator).toList();

		assertEquals(1, sorted.size());
		assertEquals(1, sorted.get(0).getId());
	}

	@Test
	void comparator_ShouldHandleAllNullTimestamps() {
		RecipeSummaryDto dto1 = new RecipeSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(null);

		RecipeSummaryDto dto2 = new RecipeSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(null);

		RecipeSummaryDto dto3 = new RecipeSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(null);

		Comparator<RecipeSummaryDto> comparator = sortingService.comparator(RecipeSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<RecipeSummaryDto> sorted = Stream.of(dto1, dto2, dto3).sorted(comparator).toList();

		assertEquals(3, sorted.size());
		sorted.forEach(dto -> assertNull(dto.getCreatedAt()));
	}

	@Test
	void comparator_ShouldHandleIdenticalTimestamps() {
		LocalDateTime sameTime = LocalDateTime.of(2024, 1, 1, 10, 0);

		PlanSummaryDto dto1 = new PlanSummaryDto();
		dto1.setId(1);
		dto1.setCreatedAt(sameTime);

		PlanSummaryDto dto2 = new PlanSummaryDto();
		dto2.setId(2);
		dto2.setCreatedAt(sameTime);

		PlanSummaryDto dto3 = new PlanSummaryDto();
		dto3.setId(3);
		dto3.setCreatedAt(sameTime);

		Comparator<PlanSummaryDto> comparator = sortingService.comparator(PlanSummaryDto::getCreatedAt,
			Sort.Direction.DESC);

		List<PlanSummaryDto> sorted = Stream.of(dto1, dto2, dto3).sorted(comparator).toList();

		assertEquals(3, sorted.size());
		sorted.forEach(dto -> assertEquals(sameTime, dto.getCreatedAt()));
	}

}
