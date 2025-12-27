package source.code.unit.category;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;

import java.util.List;
import java.util.Optional;

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
import source.code.dto.request.category.CategoryUpdateDto;
import source.code.dto.response.category.CategoryResponseDto;
import source.code.event.events.Category.CategoryClearCacheEvent;
import source.code.event.events.Category.CategoryCreateCacheEvent;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.category.ThreadCategoryMapper;
import source.code.model.thread.ThreadCategory;
import source.code.repository.ThreadCategoryRepository;
import source.code.service.declaration.category.CategoryCacheKeyGenerator;
import source.code.service.declaration.helpers.JsonPatchService;
import source.code.service.declaration.helpers.ValidationService;
import source.code.service.implementation.category.ThreadCategoryServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ThreadCategoryServiceTest {

	@Mock
	private ValidationService validationService;

	@Mock
	private JsonPatchService jsonPatchService;

	@Mock
	private CategoryCacheKeyGenerator<ThreadCategory> cacheKeyGenerator;

	@Mock
	private ApplicationEventPublisher applicationEventPublisher;

	@Mock
	private CacheManager cacheManager;

	@Mock
	private ThreadCategoryRepository repository;

	@Mock
	private ThreadCategoryMapper mapper;

	@InjectMocks
	private ThreadCategoryServiceImpl threadCategoryService;

	private CategoryCreateDto createDto;

	private ThreadCategory category;

	private CategoryResponseDto responseDto;

	private String cacheKey;

	private JsonMergePatch patch;

	private CategoryUpdateDto patchedDto;

	private Cache cache;

	private Cache.ValueWrapper cachedValueWrapper;

	@BeforeEach
	void setup() {
		createDto = new CategoryCreateDto();
		category = new ThreadCategory();
		responseDto = new CategoryResponseDto();
		cacheKey = "testCacheKey";
		patch = mock(JsonMergePatch.class);
		patchedDto = new CategoryUpdateDto();
		cache = mock(Cache.class);
		cachedValueWrapper = mock(Cache.ValueWrapper.class);
	}

	@Test
	void createCategory_shouldCreate() {
		when(mapper.toEntity(createDto)).thenReturn(category);
		when(repository.save(category)).thenReturn(category);
		when(mapper.toResponseDto(category)).thenReturn(responseDto);

		CategoryResponseDto result = threadCategoryService.createCategory(createDto);

		assertEquals(result, responseDto);
	}

	@Test
	void createCategory_shouldPublishEvent() {
		ArgumentCaptor<CategoryClearCacheEvent> eventCaptor = ArgumentCaptor.forClass(CategoryClearCacheEvent.class);

		when(mapper.toEntity(createDto)).thenReturn(category);
		when(repository.save(category)).thenReturn(category);
		when(mapper.toResponseDto(category)).thenReturn(responseDto);
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);

		threadCategoryService.createCategory(createDto);

		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
	}

	@Test
	void updateCategory_shouldUpdate() throws JsonPatchException, JsonProcessingException {
		int categoryId = 1;
		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(jsonPatchService.createFromPatch(patch, CategoryUpdateDto.class)).thenReturn(patchedDto);

		threadCategoryService.updateCategory(categoryId, patch);

		verify(validationService).validate(patchedDto);
		verify(mapper).updateEntityFromDto(category, patchedDto);
		verify(repository).save(category);
	}

	@Test
	void updateCategory_shouldPublishEvent() throws JsonPatchException, JsonProcessingException {
		int categoryId = 1;
		ArgumentCaptor<CategoryClearCacheEvent> eventCaptor = ArgumentCaptor.forClass(CategoryClearCacheEvent.class);

		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(jsonPatchService.createFromPatch(patch, CategoryUpdateDto.class)).thenReturn(patchedDto);
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);

		threadCategoryService.updateCategory(categoryId, patch);

		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
	}

	@Test
	void updateCategory_shouldThrowExceptionWhenCategoryNotFound() {
		int nonExistentCategoryId = 999;
		when(repository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class,
			() -> threadCategoryService.updateCategory(nonExistentCategoryId, patch));

		verifyNoInteractions(validationService, mapper, applicationEventPublisher, cacheKeyGenerator);
		verify(repository, never()).save(category);
	}

	@Test
	void updateCategory_shouldThrowExceptionWhenPatchFails() throws JsonPatchException, JsonProcessingException {
		int categoryId = 1;
		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(jsonPatchService.createFromPatch(patch, CategoryUpdateDto.class)).thenThrow(JsonPatchException.class);

		assertThrows(JsonPatchException.class, () -> threadCategoryService.updateCategory(categoryId, patch));

		verifyNoInteractions(validationService, applicationEventPublisher, cacheKeyGenerator);
		verify(repository, never()).save(category);
	}

	@Test
	void updateCategory_shouldThrowExceptionWhenValidationFails() throws JsonPatchException, JsonProcessingException {
		int categoryId = 1;
		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(jsonPatchService.createFromPatch(patch, CategoryUpdateDto.class)).thenReturn(patchedDto);

		doThrow(new IllegalArgumentException("Validation failed")).when(validationService).validate(patchedDto);

		assertThrows(RuntimeException.class, () -> threadCategoryService.updateCategory(categoryId, patch));

		verify(validationService).validate(patchedDto);
		verifyNoInteractions(applicationEventPublisher, cacheKeyGenerator);
		verify(repository, never()).save(category);
	}

	@Test
	void deleteCategory_shouldDelete() {
		int categoryId = 1;

		when(repository.findById(categoryId)).thenReturn(Optional.of(category));

		threadCategoryService.deleteCategory(categoryId);

		verify(repository).delete(category);
	}

	@Test
	void deleteCategory_shouldPublishEvent() {
		int categoryId = 1;
		ArgumentCaptor<CategoryClearCacheEvent> eventCaptor = ArgumentCaptor.forClass(CategoryClearCacheEvent.class);

		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);

		threadCategoryService.deleteCategory(categoryId);

		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
	}

	@Test
	void deleteCategory_shouldThrowExceptionWhenCategoryNotFound() {
		int nonExistentCategoryId = 999;
		when(repository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> threadCategoryService.deleteCategory(nonExistentCategoryId));

		verifyNoInteractions(applicationEventPublisher, cacheKeyGenerator);
		verify(repository, never()).delete(category);
	}

	@Test
	void getAllCategories_shouldReturnCachedCategories() {
		List<CategoryResponseDto> cachedCategories = List.of(responseDto);

		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
		when(cacheManager.getCache("allCategories")).thenReturn(cache);
		when(cache.get(cacheKey)).thenReturn(cachedValueWrapper);
		when(cachedValueWrapper.get()).thenReturn(cachedCategories);

		List<CategoryResponseDto> result = threadCategoryService.getAllCategories();

		assertEquals(cachedCategories, result);
	}

	@Test
	void getAllCategories_shouldFetchFromRepositoryAndPublishEventWhenCacheIsMissed() {
		List<CategoryResponseDto> fetchedCategories = List.of(responseDto);
		ArgumentCaptor<CategoryCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(CategoryCreateCacheEvent.class);

		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
		when(cacheManager.getCache("allCategories")).thenReturn(cache);
		when(cache.get(cacheKey)).thenReturn(null);
		when(repository.findAll()).thenReturn(List.of(category));
		when(mapper.toResponseDto(category)).thenReturn(responseDto);

		List<CategoryResponseDto> result = threadCategoryService.getAllCategories();

		verify(repository).findAll();
		verify(mapper).toResponseDto(category);
		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
		assertEquals(fetchedCategories, result);
	}

	@Test
	void getAllCategories_shouldThrowExceptionWhenCacheIsNull() {
		when(cacheManager.getCache("allCategories")).thenReturn(null);

		assertThrows(NullPointerException.class, () -> threadCategoryService.getAllCategories());
		verifyNoInteractions(repository, applicationEventPublisher);
	}

	@Test
	void getAllCategories_shouldFetchFromRepositoryWhenCacheValueIsNull() {
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
		when(cacheManager.getCache("allCategories")).thenReturn(cache);
		when(cache.get(cacheKey)).thenReturn(cachedValueWrapper);
		when(cachedValueWrapper.get()).thenReturn(null);

		List<CategoryResponseDto> result = threadCategoryService.getAllCategories();

		verify(repository).findAll();
		assertTrue(result.isEmpty());
	}

	@Test
	void getAllCategories_shouldHandleUnexpectedCacheValueGracefully() {
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
		when(cacheManager.getCache("allCategories")).thenReturn(cache);
		when(cache.get(cacheKey)).thenReturn(cachedValueWrapper);
		when(cachedValueWrapper.get()).thenReturn("UnexpectedStringValue");

		List<CategoryResponseDto> result = threadCategoryService.getAllCategories();

		verify(repository).findAll();
		assertTrue(result.isEmpty());
	}

	@Test
	void getAllCategories_shouldHandleEmptyRepositoryResult() {
		ArgumentCaptor<CategoryCreateCacheEvent> eventCaptor = ArgumentCaptor.forClass(CategoryCreateCacheEvent.class);
		when(cacheKeyGenerator.generateCacheKey()).thenReturn(cacheKey);
		when(cacheManager.getCache("allCategories")).thenReturn(cache);
		when(cache.get(cacheKey)).thenReturn(null);

		when(repository.findAll()).thenReturn(List.of());

		List<CategoryResponseDto> result = threadCategoryService.getAllCategories();

		verify(applicationEventPublisher).publishEvent(eventCaptor.capture());
		assertEquals(cacheKey, eventCaptor.getValue().getCacheKey());
		assertEquals(List.of(), result);
	}

	@Test
	void getCategory_shouldReturnCategoryWhenFound() {
		int categoryId = 1;
		when(repository.findById(categoryId)).thenReturn(Optional.of(category));
		when(mapper.toResponseDto(category)).thenReturn(responseDto);

		CategoryResponseDto result = threadCategoryService.getCategory(categoryId);

		assertEquals(responseDto, result);
	}

	@Test
	void getCategory_shouldThrowRecordNotFoundExceptionWhenCategoryNotFound() {
		int nonExistentCategoryId = 999;
		when(repository.findById(nonExistentCategoryId)).thenReturn(Optional.empty());

		assertThrows(RecordNotFoundException.class, () -> threadCategoryService.getCategory(nonExistentCategoryId));
		verifyNoInteractions(mapper);
	}

}
