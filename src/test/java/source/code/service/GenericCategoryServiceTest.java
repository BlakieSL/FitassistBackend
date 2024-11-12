package source.code.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cache.Cache;
import org.springframework.data.jpa.repository.JpaRepository;
import source.code.dto.request.category.CategoryCreateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.category.BaseMapper;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.implementation.category.GenericCategoryService;

import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenericCategoryServiceTest {
    @Mock
    private ValidationService validationService;

    @Mock
    private JsonPatchService jsonPatchService;

    @Mock
    private CategoryCacheKeyGenerator<Object> cacheKeyGenerator;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private JpaRepository<Object, Integer> repository;

    @Mock
    private BaseMapper<Object> mapper;

    private GenericCategoryService<Object> genericCategoryService;

    @BeforeEach
    void setUp() {
        genericCategoryService = new GenericCategoryService<>(
                validationService,
                jsonPatchService,
                cacheKeyGenerator,
                applicationEventPublisher,
                cacheManager,
                repository,
                mapper
        ) {};
    }

    @Test
    void createCategory_shouldCreate() {
        CategoryCreateDto request = new CategoryCreateDto();
        Object mockEntity = new Object();
        CategoryResponseDto mockResponseDto = new CategoryResponseDto();

        when(mapper.toEntity(request)).thenReturn(mockEntity);
        when(repository.save(mockEntity)).thenReturn(mockEntity);
        when(mapper.toResponseDto(mockEntity)).thenReturn(mockResponseDto);
        String cacheKey = "testCacheKey";
        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);

        CategoryResponseDto response = genericCategoryService.createCategory(request);

        verify(applicationEventPublisher).publishEvent(any(CategoryClearCacheEvent.class));
        assertNotNull(response);
    }

    @Test
    void getAllCategories_shouldNotInteractWithRepositoryWhenCacheHit() {
        String cacheKey = "testCacheKey";
        Cache cache = mock(Cache.class);
        Cache.ValueWrapper cachedValue = mock(Cache.ValueWrapper.class);
        List<CategoryResponseDto> cachedCategoriesList = List.of(new CategoryResponseDto());

        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
        when(cacheManager.getCache("allCategories")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(cachedValue);
        when(cachedValue.get()).thenReturn(cachedCategoriesList);

        List<CategoryResponseDto> result = genericCategoryService.getAllCategories();

        assertEquals(cachedCategoriesList, result);
        verify(cache).get(cacheKey);
    }

    @Test
    void getAllCategories_shouldInteractWithRepositoryWhenCacheMiss() {
        String cacheKey = "testCacheKey";
        Cache cache = mock(Cache.class);
        List<CategoryResponseDto> categoryResponseDtos = List.of(new CategoryResponseDto());
        Object mockEntity = new Object();

        when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
        when(cacheManager.getCache("allCategories")).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(null);
        when(repository.findAll()).thenReturn(List.of(mockEntity));
        when(mapper.toResponseDto(mockEntity)).thenReturn(new CategoryResponseDto());

        List<CategoryResponseDto> result = genericCategoryService.getAllCategories();

        verify(repository).findAll();
        verify(mapper).toResponseDto(mockEntity);
        verify(applicationEventPublisher).publishEvent(any(CategoryCreateCacheEvent.class));
        assertEquals(categoryResponseDtos.size(), result.size());
    }

    @Test
    void getCategory_shouldReturnCategoryWhenFound() {
        int categoryId = 1;
        Object category = new Object();
        CategoryResponseDto responseDto = new CategoryResponseDto();

        when(repository.findById(categoryId)).thenReturn(Optional.of(category));
        when(mapper.toResponseDto(category)).thenReturn(responseDto);

        CategoryResponseDto result = genericCategoryService.getCategory(categoryId);

        verify(repository).findById(categoryId);
        verify(mapper).toResponseDto(category);
        assertEquals(responseDto, result);
    }

    @Test
    void getCategory_shouldThrowExceptionWhenNotFound() {
        int nonExistentCategoryId = 999;

        when(repository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());
        assertThrows(RecordNotFoundException.class, () -> {
            genericCategoryService.getCategory(nonExistentCategoryId);
        });

        verify(repository).findById(nonExistentCategoryId);
        verifyNoInteractions(mapper);
    }

}
