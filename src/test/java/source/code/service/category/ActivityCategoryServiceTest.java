package source.code.service.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.event.events.Activity.ActivityCreateEvent;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.category.ActivityCategoryMapper;
import source.code.model.activity.ActivityCategory;
import source.code.repository.ActivityCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.category.ActivityCategoryServiceImpl;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ActivityCategoryServiceTest {
    @Mock
    private ValidationService validationService;

    @Mock
    private JsonPatchService jsonPatchService;

    @Mock
    private CategoryCacheKeyGenerator<ActivityCategory> cacheKeyGenerator;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private ActivityCategoryRepository repository;

    @Mock
    private ActivityCategoryMapper mapper;

    @InjectMocks
    private ActivityCategoryServiceImpl activityCategoryService;

    private ActivityCategory category;
    private CategoryResponseDto responseDto;
    String cacheKey;

    @BeforeEach
    void setup() {
        category = new ActivityCategory();
        responseDto = new CategoryResponseDto();
        cacheKey = "testCacheKey";
    }

    @Test
    void createCategory_shouldCreate() {
        CategoryCreateDto request = new CategoryCreateDto();

        when(mapper.toEntity(request)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);
        when(mapper.toResponseDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = activityCategoryService.createCategory(request);

        assertEquals(result, responseDto);
    }

    @Test
    void createCategory_shouldPublishClearCacheEvent() {
        CategoryCreateDto request = new CategoryCreateDto();
        ArgumentCaptor<CategoryClearCacheEvent> eventCaptor = ArgumentCaptor
                .forClass(CategoryClearCacheEvent.class);

        when(mapper.toEntity(request)).thenReturn(category);
        when(repository.save(category)).thenReturn(category);
        when(mapper.toResponseDto(category)).thenReturn(responseDto);
        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);

        activityCategoryService.createCategory(request);

        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
    }





    @Test
    void getAllCategories_shouldNotInteractWithRepositoryWhenCacheHit() {
        Cache cache = mock(Cache.class);
        Cache.ValueWrapper cachedValue = mock(Cache.ValueWrapper.class);
        List<CategoryResponseDto> cachedCategoriesList = List.of(responseDto);

        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
        when(cacheManager.getCache("allCategories")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(cachedValue);
        when(cachedValue.get()).thenReturn(cachedCategoriesList);

        List<CategoryResponseDto> result = activityCategoryService.getAllCategories();

        assertEquals(cachedCategoriesList, result);
        verify(cache).get(cacheKey);
    }

    @Test
    void getAllCategories_shouldInteractWithRepositoryWhenCacheMiss() {
        Cache cache = mock(Cache.class);
        List<CategoryResponseDto> categoryResponseDtos = List.of(responseDto);
        ActivityCategory mockEntity = new ActivityCategory();

        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
        when(cacheManager.getCache("allCategories")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.findAll()).thenReturn(List.of(mockEntity));
        when(mapper.toResponseDto(mockEntity)).thenReturn(responseDto);

        List<CategoryResponseDto> result = activityCategoryService.getAllCategories();

        verify(repository).findAll();
        verify(mapper).toResponseDto(mockEntity);
        verify(applicationEventPublisher).publishEvent(any(CategoryCreateCacheEvent.class));
        assertEquals(categoryResponseDtos.size(), result.size());
    }

    @Test
    void getCategory_shouldReturnCategoryWhenFound() {
        int categoryId = 1;
        when(repository.findById(categoryId)).thenReturn(Optional.of(category));
        when(mapper.toResponseDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = activityCategoryService.getCategory(categoryId);

        verify(repository).findById(categoryId);
        verify(mapper).toResponseDto(category);
        assertEquals(responseDto, result);
    }

    @Test
    void getCategory_shouldThrowExceptionWhenNotFound() {
        int nonExistentCategoryId = 999;

        when(repository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class, () ->
            activityCategoryService.getCategory(nonExistentCategoryId)
        );

        verify(repository).findById(nonExistentCategoryId);
        verifyNoInteractions(mapper);
    }

}
