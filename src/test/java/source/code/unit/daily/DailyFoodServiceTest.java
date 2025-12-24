package source.code.unit.daily;

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
import source.code.dto.pojo.FoodMacros;
import source.code.dto.request.food.DailyCartFoodCreateDto;
import source.code.dto.request.food.DailyCartFoodGetDto;
import source.code.dto.request.food.DailyCartFoodUpdateDto;
import source.code.dto.response.daily.DailyFoodsResponseDto;
import source.code.dto.response.food.FoodCalculatedMacrosResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.helper.utils.AuthorizationUtil;
import source.code.mapper.daily.DailyFoodMapper;
import source.code.model.daily.DailyCart;
import source.code.model.daily.DailyCartFood;
import source.code.model.food.Food;
import source.code.model.user.User;
import source.code.repository.DailyCartFoodRepository;
import source.code.repository.DailyCartRepository;
import source.code.repository.FoodRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.RepositoryHelper;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.daily.DailyFoodServiceImpl;

import java.math.BigDecimal;
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
    private DailyCartRepository dailyCartRepository;
    @Mock
    private DailyCartFoodRepository dailyCartFoodRepository;
    @Mock
    private FoodRepository foodRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private DailyFoodServiceImpl dailyFoodService;

    private Food food;
    private DailyCart dailyCart;
    private DailyCartFood dailyCartFood;
    private DailyCartFoodCreateDto createDto;
    private JsonMergePatch patch;
    private MockedStatic<AuthorizationUtil> mockedAuthorizationUtil;

    @BeforeEach
    void setUp() {
        food = new Food();
        dailyCart = new DailyCart();
        dailyCartFood = new DailyCartFood();
        dailyCartFood.setQuantity(BigDecimal.valueOf(0));
        createDto = new DailyCartFoodCreateDto();
        createDto.setQuantity(BigDecimal.valueOf(10));
        patch = mock(JsonMergePatch.class);
        mockedAuthorizationUtil = mockStatic(AuthorizationUtil.class);

        food.setId(1);
        dailyCart.setId(1);
        dailyCartFood.setId(1);
        createDto.setQuantity(BigDecimal.valueOf(100));
    }

    @AfterEach
    void tearDown() {
        if (mockedAuthorizationUtil != null) {
            mockedAuthorizationUtil.close();
        }
    }

    @Test
    void addFoodToDailyCart_shouldUpdateExistingDailyFoodToDailyCartAmount() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(anyInt(), any()))
                .thenReturn(Optional.of(dailyCart));
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
        when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));

        dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

        verify(dailyCartRepository, times(1)).save(any(DailyCart.class));
    }

    @Test
    void addFoodToDailyCart_shouldAddNewDailyFoodToDailyCart_whenNotFound() {
        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(anyInt(), any()))
                .thenReturn(Optional.empty());
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID)).thenReturn(food);
        when(repositoryHelper.find(userRepository, User.class, USER_ID))
                .thenReturn(new User());
        when(dailyCartRepository.save(any()))
                .thenReturn(dailyCart);
        when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(dailyCart.getId(), FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));

        dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

        verify(dailyCartRepository, times(2)).save(any(DailyCart.class));
    }

    @Test
    void addFoodToDailyCart_shouldCreateNewDailyCart_whenNotFound() {
        User user = new User();
        user.setId(USER_ID);
        DailyCart newDailyCart = DailyCart.createDate(user);
        newDailyCart.setId(1);
        newDailyCart.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDate(eq(USER_ID), any())).thenReturn(Optional.empty());
        when(repositoryHelper.find(userRepository, User.class, USER_ID)).thenReturn(user);
        when(dailyCartRepository.save(any(DailyCart.class)))
                .thenReturn(newDailyCart);
        when(repositoryHelper.find(foodRepository, Food.class, FOOD_ID))
                .thenReturn(food);
        when(dailyCartFoodRepository.findByDailyCartIdAndFoodId(
                anyInt(),
                eq(FOOD_ID))
        ).thenReturn(Optional.empty());

        dailyFoodService.addFoodToDailyCart(FOOD_ID, createDto);

        ArgumentCaptor<DailyCart> dailyFoodCaptor = ArgumentCaptor
                .forClass(DailyCart.class);
        verify(dailyCartRepository, times(2))
                .save(dailyFoodCaptor.capture());
        DailyCart savedDailyFood = dailyFoodCaptor.getValue();
        assertEquals(USER_ID, savedDailyFood.getUser().getId());
        assertEquals(
                createDto.getQuantity(),
                savedDailyFood.getDailyCartFoods().get(0).getQuantity()
        );
    }

    @Test
    void removeFoodFromDailyCart_shouldRemoveExistingDailyFoodFromDailyCart() {
        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));
        doNothing().when(dailyCartFoodRepository).delete(dailyCartFood);


        dailyFoodService.removeFoodFromDailyCart(FOOD_ID);

        verify(dailyCartFoodRepository).delete(dailyCartFood);
    }

    @Test
    void removeFoodFromDailyCart_shouldThrowException_whenDailyFoodFromDailyCartNotFound() {
        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.removeFoodFromDailyCart(FOOD_ID)
        );
    }

    @Test
    void updateDailyFoodItem_shouldUpdate() throws JsonPatchException, JsonProcessingException {
        DailyCartFoodUpdateDto patchedDto = new DailyCartFoodUpdateDto();
        patchedDto.setQuantity(BigDecimal.valueOf(120));

        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));

        doReturn(patchedDto).when(jsonPatchService).createFromPatch(
                any(JsonMergePatch.class),
                eq(DailyCartFoodUpdateDto.class)
        );

        dailyFoodService.updateDailyFoodItem(FOOD_ID, patch);

        verify(validationService).validate(patchedDto);
        assertEquals(patchedDto.getQuantity(), dailyCartFood.getQuantity());
        verify(dailyCartFoodRepository).save(dailyCartFood);
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenDailyFoodItemNotFound() {
        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch));

        verifyNoInteractions(validationService);
        verify(dailyCartFoodRepository, never()).save(any());
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenPatchFails()
            throws JsonPatchException, JsonProcessingException {
        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));
        doThrow(JsonPatchException.class).when(jsonPatchService).createFromPatch(
                any(JsonMergePatch.class),
                eq(DailyCartFoodUpdateDto.class)
        );

        assertThrows(JsonPatchException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch)
        );

        verifyNoInteractions(validationService);
        verify(dailyCartFoodRepository, never()).save(any());
    }

    @Test
    void updateDailyFoodItem_shouldThrowException_whenPatchValidationFails()
            throws JsonPatchException, JsonProcessingException {
        DailyCartFoodUpdateDto patchedDto = new DailyCartFoodUpdateDto();
        patchedDto.setQuantity(BigDecimal.valueOf(120));

        when(dailyCartFoodRepository.findByIdWithoutAssociations(FOOD_ID))
                .thenReturn(Optional.of(dailyCartFood));
        doReturn(patchedDto).when(jsonPatchService).createFromPatch(
                any(JsonMergePatch.class),
                eq(DailyCartFoodUpdateDto.class)
        );

        doThrow(new IllegalArgumentException("Validation failed")).when(validationService)
                .validate(patchedDto);

        assertThrows(RuntimeException.class, () ->
                dailyFoodService.updateDailyFoodItem(FOOD_ID, patch)
        );

        verify(validationService).validate(patchedDto);
        verify(dailyCartFoodRepository, never()).save(any());
    }

    @Test
    void getFoodsFromDailyCart_shouldReturnFoods() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(BigDecimal.valueOf(70));
        dailyCart.setUser(user);
        dailyCart.getDailyCartFoods().add(dailyCartFood);

        FoodCalculatedMacrosResponseDto calculatedResponseDto = new FoodCalculatedMacrosResponseDto();
        FoodMacros foodMacros = FoodMacros.of(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(5),
                BigDecimal.valueOf(20)
        );
        calculatedResponseDto.setFoodMacros(foodMacros);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDateWithFoodAssociations(USER_ID, LocalDate.now())).thenReturn(Optional.of(dailyCart));
        when(dailyFoodMapper.toFoodCalculatedMacrosResponseDto(dailyCartFood)).thenReturn(calculatedResponseDto);

        DailyFoodsResponseDto result = dailyFoodService
                .getFoodFromDailyCart(new DailyCartFoodGetDto(LocalDate.now()));

        assertEquals(1, result.getFoods().size());
        assertEquals(BigDecimal.valueOf(100.0), result.getTotalCalories());
        assertEquals(BigDecimal.valueOf(20.0), result.getTotalCarbohydrates());
        assertEquals(BigDecimal.valueOf(10.0), result.getTotalProtein());
        assertEquals(BigDecimal.valueOf(5.0), result.getTotalFat());
    }

    @Test
    void getFoodsFromDailyCart_shouldReturnEmptyFoods_whenDailyFoodNotFound() {
        User user = new User();
        user.setId(USER_ID);
        user.setWeight(BigDecimal.valueOf(70));
        DailyCart newDailyCart = DailyCart.createDate(user);
        newDailyCart.setUser(user);

        mockedAuthorizationUtil.when(AuthorizationUtil::getUserId).thenReturn(USER_ID);
        when(dailyCartRepository.findByUserIdAndDateWithFoodAssociations(USER_ID, LocalDate.now())).thenReturn(Optional.empty());

        DailyFoodsResponseDto result = dailyFoodService
                .getFoodFromDailyCart(new DailyCartFoodGetDto(LocalDate.now()));

        assertTrue(result.getFoods().isEmpty());
    }
}
