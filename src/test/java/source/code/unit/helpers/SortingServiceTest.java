package source.code.unit.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import source.code.dto.response.plan.PlanSummaryDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.service.implementation.helpers.SortingServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class SortingServiceTest {

    private SortingServiceImpl sortingService;

    @BeforeEach
    void setUp() {
        sortingService = new SortingServiceImpl();
    }

    @Test
    @DisplayName("sortByTimestamp - Should sort PlanSummaryDto list in DESC order")
    void sortByTimestamp_ShouldSortPlanDtosInDescOrder() {
        List<PlanSummaryDto> dtos = new ArrayList<>();

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        dto1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setId(2);
        dto2.setCreatedAt(LocalDateTime.of(2024, 1, 3, 10, 0));

        PlanSummaryDto dto3 = new PlanSummaryDto();
        dto3.setId(3);
        dto3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);

        sortingService.sortByTimestamp(dtos, PlanSummaryDto::getCreatedAt, Sort.Direction.DESC);

        assertEquals(2, dtos.get(0).getId());
        assertEquals(3, dtos.get(1).getId());
        assertEquals(1, dtos.get(2).getId());
    }

    @Test
    @DisplayName("sortByTimestamp - Should sort RecipeSummaryDto list in ASC order")
    void sortByTimestamp_ShouldSortRecipeDtosInAscOrder() {
        List<RecipeSummaryDto> dtos = new ArrayList<>();

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setCreatedAt(LocalDateTime.of(2024, 1, 3, 10, 0));

        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);
        dto2.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        RecipeSummaryDto dto3 = new RecipeSummaryDto();
        dto3.setId(3);
        dto3.setCreatedAt(LocalDateTime.of(2024, 1, 2, 10, 0));

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);

        sortingService.sortByTimestamp(dtos, RecipeSummaryDto::getCreatedAt, Sort.Direction.ASC);

        assertEquals(2, dtos.get(0).getId());
        assertEquals(3, dtos.get(1).getId());
        assertEquals(1, dtos.get(2).getId());
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle null timestamps with DESC order")
    void sortByTimestamp_ShouldHandleNullTimestampsDescOrder() {
        List<PlanSummaryDto> dtos = new ArrayList<>();

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

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);
        dtos.add(dto4);

        sortingService.sortByTimestamp(dtos, PlanSummaryDto::getCreatedAt, Sort.Direction.DESC);

        assertEquals(3, dtos.get(0).getId());
        assertEquals(1, dtos.get(1).getId());
        assertTrue(dtos.get(2).getCreatedAt() == null || dtos.get(3).getCreatedAt() == null);
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle null timestamps with ASC order")
    void sortByTimestamp_ShouldHandleNullTimestampsAscOrder() {
        List<RecipeSummaryDto> dtos = new ArrayList<>();

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

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);
        dtos.add(dto4);

        sortingService.sortByTimestamp(dtos, RecipeSummaryDto::getCreatedAt, Sort.Direction.ASC);

        assertEquals(3, dtos.get(0).getId());
        assertEquals(1, dtos.get(1).getId());
        assertTrue(dtos.get(2).getCreatedAt() == null || dtos.get(3).getCreatedAt() == null);
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle empty list")
    void sortByTimestamp_ShouldHandleEmptyList() {
        List<PlanSummaryDto> dtos = new ArrayList<>();

        assertDoesNotThrow(() ->
            sortingService.sortByTimestamp(dtos, PlanSummaryDto::getCreatedAt, Sort.Direction.DESC)
        );
        assertTrue(dtos.isEmpty());
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle list with single element")
    void sortByTimestamp_ShouldHandleSingleElement() {
        List<PlanSummaryDto> dtos = new ArrayList<>();

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        dto1.setCreatedAt(LocalDateTime.of(2024, 1, 1, 10, 0));

        dtos.add(dto1);

        sortingService.sortByTimestamp(dtos, PlanSummaryDto::getCreatedAt, Sort.Direction.DESC);

        assertEquals(1, dtos.size());
        assertEquals(1, dtos.get(0).getId());
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle list with all null timestamps")
    void sortByTimestamp_ShouldHandleAllNullTimestamps() {
        List<RecipeSummaryDto> dtos = new ArrayList<>();

        RecipeSummaryDto dto1 = new RecipeSummaryDto();
        dto1.setId(1);
        dto1.setCreatedAt(null);

        RecipeSummaryDto dto2 = new RecipeSummaryDto();
        dto2.setId(2);
        dto2.setCreatedAt(null);

        RecipeSummaryDto dto3 = new RecipeSummaryDto();
        dto3.setId(3);
        dto3.setCreatedAt(null);

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);

        assertDoesNotThrow(() ->
            sortingService.sortByTimestamp(dtos, RecipeSummaryDto::getCreatedAt, Sort.Direction.DESC)
        );
        assertEquals(3, dtos.size());
    }

    @Test
    @DisplayName("sortByTimestamp - Should handle identical timestamps")
    void sortByTimestamp_ShouldHandleIdenticalTimestamps() {
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        List<PlanSummaryDto> dtos = new ArrayList<>();

        PlanSummaryDto dto1 = new PlanSummaryDto();
        dto1.setId(1);
        dto1.setCreatedAt(sameTime);

        PlanSummaryDto dto2 = new PlanSummaryDto();
        dto2.setId(2);
        dto2.setCreatedAt(sameTime);

        PlanSummaryDto dto3 = new PlanSummaryDto();
        dto3.setId(3);
        dto3.setCreatedAt(sameTime);

        dtos.add(dto1);
        dtos.add(dto2);
        dtos.add(dto3);

        assertDoesNotThrow(() ->
            sortingService.sortByTimestamp(dtos, PlanSummaryDto::getCreatedAt, Sort.Direction.DESC)
        );
        assertEquals(3, dtos.size());
    }
}
