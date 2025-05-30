package unit.daily;

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
import source.code.dto.request.food.DailyFoodItemCreateDto;
import source.code.dto.response.DailyFoodsResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.user.AuthorizationUtil;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.food.DailyFood;
import source.code.model.food.DailyFoodItem;
import source.code.model.food.Food;
import source.code.model.user.profile.User;
import source.code.repository.DailyFoodItemRepository;
import source.code.repository.DailyFoodRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.daily.DailyFoodServiceImpl;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DailyFoodServiceTest {
    private static final int USER_ID = 1;
    private static final int FOOD_ID = 1;
    private static final int INVALID_FOOD_ID = 999;

    @Mock
    private JsonPatchService jsonPatchService;
    @Mock
    private ValidationService validationService;
    @Mock
    private DailyFoodMapper dailyFoodMapper;
    @Mock
    private RepositoryHelper repositoryHelper;
    @Mock
    private DailyFoodRepository dailyFoodRepository;
    @Mock
    private DailyFoodItemRepository dailyFoodItemRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyFoodServiceImpl dailyFoodService;

    private Food food;
    private DailyFood dailyFood;
    private DailyFoodItem dailyFoodItem;
    private DailyFoodItemCreateDto createDto;
    private JsonMergePatch patch;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        food = new Food();
        dailyFood = new DailyFood();
        dailyFoodItem = new DailyFoodItem();
        createDto = new DailyFoodItemCreateDto();
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

        food.setId(1);
        dailyFood.setId(1);
        dailyFoodItem.setId(1);
        createDto.setAmount(100);
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void addFoodToDailyFoodItem_shouldUpdateExistingDailyFoodItemAmount() {
        dailyFood.getDailyFoodItems().add(dailyFoodItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyFoodItem));

        dailyFoodService.addFoodToDailyFoodItem(FOOD_ID, createDto);

        verify(dailyFoodRepository).save(dailyFood);
        assertEquals(dailyFood.getDailyFoodItems().get(0).getAmount(), createDto.getAmount());
    }

    @Test
    void addFoodToDailyFoodItem_shouldAddNewDailyFoodItem_whenNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.empty());

        dailyFoodService.addFoodToDailyFoodItem(FOOD_ID, createDto);

        verify(dailyFoodRepository).save(dailyFood);
        assertEquals(dailyFood.getDailyFoodItems().get(0).getAmount(), createDto.getAmount());
    }

    @Test
    void addFoodToDailyFoodItem_shouldCreateNewDailyFood_whenNotFound() {
        User user = new User();
        user.setId(USER_ID);
        DailyFood newDailyFood = DailyFood.createForToday(user);
        newDailyFood.setId(1);
        newDailyFood.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
        when(dailyFoodRepository.save(any(DailyFood.class))).thenReturn(newDailyFood);
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(anyInt(), eq(FOOD_ID)))
                .thenReturn(Optional.empty());

        dailyFoodService.addFoodToDailyFoodItem(FOOD_ID, createDto);

        ArgumentCaptor<DailyFood> dailyFoodCaptor = ArgumentCaptor.forClass(DailyFood.class);
        verify(dailyFoodRepository, times(2)).save(dailyFoodCaptor.capture());
        DailyFood savedDailyFood = dailyFoodCaptor.getValue();
        assertEquals(USER_ID, savedDailyFood.getUser().getId());
        assertEquals(createDto.getAmount(), savedDailyFood.getDailyFoodItems().get(0).getAmount());
    }

    @Test
    void removeFoodFromDailyFoodItem_shouldRemoveExistingDailyFoodItem() {
        dailyFood.getDailyFoodItems().add(dailyFoodItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyFoodItem));

        dailyFoodService.removeFoodFromDailyFoodItem(FOOD_ID);

        verify(dailyFoodRepository).save(dailyFood);
        assertTrue(dailyFood.getDailyFoodItems().isEmpty());
    }

    @Test
    void removeFoodFromDailyFoodItem_shouldThrowException_whenDailyFoodItemNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.removeFoodFromDailyFoodItem(FOOD_ID)
        );
    }

    @Test
    void removeFoodFromDailyFoodItem_shouldThrowException_whenDailyFoodNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.removeFoodFromDailyFoodItem(FOOD_ID)
        );
    }

    @Test
    void updateDailyFoodItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        DailyFoodItemCreateDto patchedDto = new DailyFoodItemCreateDto();
        patchedDto.setAmount(200);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyFoodItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(any(JsonMergePatch.class),
                any(DailyFoodItemCreateDto.class), eq(DailyFoodItemCreateDto.class));

        dailyFoodService.updateDailyFoodItem(FOOD_ID, patch);

        verify(validationService).validate(patchedDto);
        assertEquals(patchedDto.getAmount(), dailyFoodItem.getAmount());
        verify(dailyFoodRepository).save(dailyFood);
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenDailyFoodItemNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenDailyFoodNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenPatchFails()
            throws JsonPatchException, JsonProcessingException
    {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyFoodItem));
        doThrow(JsonPatchException.class).when(jsonPatchService).applyPatch(any(JsonMergePatch.class),
                any(DailyFoodItemCreateDto.class), eq(DailyFoodItemCreateDto.class));

        assertThrows(JsonPatchException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch)
        );

        verifyNoInteractions(validationService);
        verify(dailyFoodRepository, never()).save(dailyFood);
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenPatchValidationFails()
            throws JsonPatchException, JsonProcessingException
    {
        DailyFoodItemCreateDto patchedDto = new DailyFoodItemCreateDto();
        patchedDto.setAmount(200);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodItemRepository.findByDailyFoodIdAndFoodId(dailyFood.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyFoodItem));
        doReturn(patchedDto).when(jsonPatchService).applyPatch(any(JsonMergePatch.class),
                any(DailyFoodItemCreateDto.class), eq(DailyFoodItemCreateDto.class));

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch)
        );

        verify(validationService).validate(patchedDto);
        verify(dailyFoodRepository, never()).save(dailyFood);
    }

    @Test
    void getFoodsFromDailyFoodItem_shouldReturnFoods() {
        dailyFood.getDailyFoodItems().add(dailyFoodItem);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyFood));
        when(dailyFoodMapper.toDailyFoodsResponseDto(anyList()))
                .thenReturn(new DailyFoodsResponseDto());

        DailyFoodsResponseDto result = dailyFoodService.getFoodsFromDailyFoodItem();

        assertNotNull(result);
        verify(dailyFoodRepository).findByUserIdAndDate(USER_ID, LocalDate.now());
        verify(dailyFoodMapper).toDailyFoodsResponseDto(anyList());
    }

    @Test
    void getFoodsFromDailyFoodItem_shouldReturnEmptyFoods_whenDailyFoodNotFound() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(70);
        DailyFood newDailyFood = DailyFood.createForToday(user);
        newDailyFood.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyFoodRepository.findByUserIdAndDate(USER_ID, LocalDate.now())).thenReturn(Optional.empty());
        when(dailyFoodRepository.save(any(DailyFood.class))).thenReturn(newDailyFood);

        DailyFoodsResponseDto result = dailyFoodService.getFoodsFromDailyFoodItem();

        assertTrue(result.getFoods().isEmpty());
        assertEquals(0, result.getTotalCalories());
    }
}