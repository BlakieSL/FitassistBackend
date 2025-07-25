package source.code.unit.food;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import source.code.dto.pojo.FilterCriteria;
import source.code.dto.request.filter.FilterDto;
import source.code.dto.request.food.CalculateFoodMacrosRequestDto;
import source.code.dto.request.food.FoodCreateDto;
import source.code.dto.request.food.FoodUpdateDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.dto.response.food.FoodResponseDto;
import source.code.event.events.Food.FoodCreateEvent;
import source.code.event.events.Food.FoodDeleteEvent;
import source.code.event.events.Food.FoodUpdateEvent;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.food.FoodMapper;
import source.code.model.food.Food;
import source.code.repository.FoodRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.food.FoodServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private FoodMapper foodMapper;
    @Mock
    private ValidationService validationService;
    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private FoodRepository foodRepository;
    @InjectMocks
    private FoodServiceImpl foodService;

    private Food food;
    private FoodCreateDto createDto;
    private FoodResponseDto responseDto;
    private JsonMergePatch patch;
    private FoodUpdateDto patchedDto;
    private int foodId;
    private FilterDto filter;
    private CalculateFoodMacrosRequestDto calculateRequestDto;
    private FoodCalculatedMacrosResponseDto calculatedResponseDto;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        food = new Food();
        createDto = new FoodCreateDto();
        responseDto = new FoodResponseDto();
        patchedDto = new FoodUpdateDto();
        foodId = 1;
        filter = new FilterDto();
        patch = mock(JsonMergePatch.class);
        calculateRequestDto = new CalculateFoodMacrosRequestDto();
        calculatedResponseDto = new FoodCalculatedMacrosResponseDto();
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void createFood_shouldCreateFood() {
        when(foodMapper.toEntity(createDto)).thenReturn(food);
        when(foodRepository.save(food)).thenReturn(food);
        when(foodMapper.toResponseDto(food)).thenReturn(responseDto);

        FoodResponseDto result = foodService.createFood(createDto);

        assertEquals(responseDto, result);
    }

    @Test
    void createFood_shouldPublishEvent() {
        ArgumentCaptor<FoodCreateEvent> eventCaptor = ArgumentCaptor.forClass(FoodCreateEvent.class);

        when(foodMapper.toEntity(createDto)).thenReturn(food);
        when(foodRepository.save(food)).thenReturn(food);
        when(foodMapper.toResponseDto(food)).thenReturn(responseDto);

        foodService.createFood(createDto);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(food, eventCaptor.getValue().getFood());
    }

    @Test
    void updateFood_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(jsonPatchService.createFromPatch(patch, FoodUpdateDto.class))
                .thenReturn(patchedDto);
        when(foodRepository.save(food)).thenReturn(food);

        foodService.updateFood(foodId, patch);

        verify(validationService).validate(patchedDto);
        verify(foodMapper).updateFood(food, patchedDto);
        verify(foodRepository).save(food);
    }

    @Test
    void updateFood_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
        ArgumentCaptor<FoodUpdateEvent> eventCaptor = ArgumentCaptor.forClass(FoodUpdateEvent.class);

        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(jsonPatchService.createFromPatch(patch, FoodUpdateDto.class))
                .thenReturn(patchedDto);
        when(foodRepository.save(food)).thenReturn(food);

        foodService.updateFood(foodId, patch);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(food, eventCaptor.getValue().getFood());
    }

    @Test
    void updateFood_shouldThrowExceptionWhenFoodNotFound() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId))
                .thenThrow(RecordNotFoundException.of(Food.class, foodId));

        assertThrows(RecordNotFoundException.class, () -> foodService.updateFood(foodId, patch));

        verifyNoInteractions(foodMapper, jsonPatchService, validationService, eventPublisher);
        verify(foodRepository, never()).save(food);
    }

    @Test
    void updateFood_shouldThrowExceptionWhenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(jsonPatchService.createFromPatch(patch, FoodUpdateDto.class))
                .thenThrow(JsonPatchException.class);

        assertThrows(JsonPatchException.class, () -> foodService.updateFood(foodId, patch));

        verifyNoInteractions(validationService, eventPublisher);
        verify(foodRepository, never()).save(food);
    }

    @Test
    void updateFood_shouldThrowExceptionWhenValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(jsonPatchService.createFromPatch(patch, FoodUpdateDto.class))
                .thenReturn(patchedDto);

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () -> foodService.updateFood(foodId, patch));

        verify(validationService).validate(patchedDto);
        verifyNoInteractions(eventPublisher);
        verify(foodRepository, never()).save(food);
    }

    @Test
    void deleteFood_shouldDelete() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);

        foodService.deleteFood(foodId);

        verify(foodRepository).delete(food);
    }

    @Test
    void deleteFood_shouldPublishEvent() {
        ArgumentCaptor<FoodDeleteEvent> eventCaptor = ArgumentCaptor
                .forClass(FoodDeleteEvent.class);

        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);

        foodService.deleteFood(foodId);

        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(food, eventCaptor.getValue().getFood());
    }

    @Test
    void deleteFood_shouldThrowExceptionWhenFoodNotFound() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId))
                .thenThrow(RecordNotFoundException.of(Food.class, foodId));

        assertThrows(RecordNotFoundException.class, () -> foodService.deleteFood(foodId));

        verify(foodRepository, never()).delete(food);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void calculateFoodMacros_shouldCalculateMacrosForFood() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(foodMapper.toDtoWithFactor(
                eq(food),
                argThat(x -> x.compareTo(new BigDecimal("1.2")) == 0)
        )).thenReturn(calculatedResponseDto);

        CalculateFoodMacrosRequestDto request = new CalculateFoodMacrosRequestDto();
        request.setQuantity(BigDecimal.valueOf(120));

        FoodCalculatedMacrosResponseDto result = foodService.calculateFoodMacros(foodId, request);

        assertEquals(calculatedResponseDto, result);
        verify(foodMapper).toDtoWithFactor(
                eq(food),
                argThat(x -> x.compareTo(new BigDecimal("1.2")) == 0)
        );
    }

    @Test
    void calculateFoodMacros_shouldThrowExceptionWhenFoodNotFound() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId))
                .thenThrow(RecordNotFoundException.of(Food.class, foodId));

        assertThrows(RecordNotFoundException.class,
                () -> foodService.calculateFoodMacros(foodId, calculateRequestDto)
        );

        verifyNoInteractions(foodMapper);
    }

    @Test
    void getFood_shouldReturnFoodWhenFound() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId)).thenReturn(food);
        when(foodMapper.toResponseDto(food)).thenReturn(responseDto);

        FoodResponseDto result = foodService.getFood(foodId);

        assertEquals(responseDto, result);
    }

    @Test
    void getFood_shouldThrowExceptionWhenFoodNotFound() {
        when(repositoryHelper.find(foodRepository, Food.class, foodId))
                .thenThrow(RecordNotFoundException.of(Food.class, foodId));

        assertThrows(RecordNotFoundException.class, () -> foodService.getFood(foodId));

        verifyNoInteractions(foodMapper);
    }

    @Test
    void getAllFoods_shouldReturnAllFoods() {
        List<FoodResponseDto> responseDtos = List.of(responseDto);

        when(repositoryHelper.findAll(eq(foodRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<FoodResponseDto> result = foodService.getAllFoods();

        assertEquals(responseDtos, result);
        verify(repositoryHelper).findAll(eq(foodRepository), any(Function.class));
    }

    @Test
    void getAllFoods_shouldReturnEmptyListWhenNoFoods() {
        List<FoodResponseDto> responseDtos = List.of();
        when(repositoryHelper.findAll(eq(foodRepository), any(Function.class)))
                .thenReturn(responseDtos);

        List<FoodResponseDto> result = foodService.getAllFoods();

        assertTrue(result.isEmpty());
        verify(repositoryHelper).findAll(eq(foodRepository), any(Function.class));
    }

    @Test
    void getFilteredFoods_shouldReturnFilteredFoods() {
        when(foodRepository.findAll(any(Specification.class))).thenReturn(List.of(food));
        when(foodMapper.toResponseDto(food)).thenReturn(responseDto);

        List<FoodResponseDto> result = foodService.getFilteredFoods(filter);

        assertEquals(1, result.size());
        assertSame(responseDto, result.get(0));
        verify(foodRepository).findAll(any(Specification.class));
        verify(foodMapper).toResponseDto(food);
    }

    @Test
    void getFilteredFoods_shouldReturnEmptyListWhenFilterHasNoCriteria() {
        filter.setFilterCriteria(new ArrayList<>());

        when(foodRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<FoodResponseDto> result = foodService.getFilteredFoods(filter);

        assertTrue(result.isEmpty());
        verify(foodRepository).findAll(any(Specification.class));
        verifyNoInteractions(foodMapper);
    }

    @Test
    void getFilteredFoods_shouldReturnEmptyListWhenNoFoodsMatchFilter() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.setFilterKey("nonexistentKey");
        filter.setFilterCriteria(List.of(criteria));

        when(foodRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        List<FoodResponseDto> result = foodService.getFilteredFoods(filter);

        assertTrue(result.isEmpty());
        verify(foodRepository).findAll(any(Specification.class));
        verifyNoInteractions(foodMapper);
    }

    @Test
    void getAllFoodEntities_shouldReturnAllFoodEntities() {
        List<Food> foods = List.of(food);
        when(foodRepository.findAllWithoutAssociations()).thenReturn(foods);

        List<Food> result = foodService.getAllFoodEntities();

        assertEquals(foods, result);
        verify(foodRepository).findAllWithoutAssociations();
    }

    @Test
    void getAllFoodEntities_shouldReturnEmptyListWhenNoFoods() {
        List<Food> foods = List.of();
        when(foodRepository.findAllWithoutAssociations()).thenReturn(foods);

        List<Food> result = foodService.getAllFoodEntities();

        assertTrue(result.isEmpty());
        verify(foodRepository).findAllWithoutAssociations();
    }
}