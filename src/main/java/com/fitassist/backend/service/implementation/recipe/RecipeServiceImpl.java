package com.fitassist.backend.service.implementation.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fitassist.backend.auth.AuthorizationUtil;
import com.fitassist.backend.config.cache.CacheNames;
import com.fitassist.backend.dto.request.filter.FilterDto;
import com.fitassist.backend.dto.request.recipe.RecipeCreateDto;
import com.fitassist.backend.dto.request.recipe.RecipeUpdateDto;
import com.fitassist.backend.dto.response.recipe.RecipeResponseDto;
import com.fitassist.backend.dto.response.recipe.RecipeSummaryDto;
import com.fitassist.backend.event.event.Recipe.RecipeCreateEvent;
import com.fitassist.backend.event.event.Recipe.RecipeDeleteEvent;
import com.fitassist.backend.event.event.Recipe.RecipeUpdateEvent;
import com.fitassist.backend.exception.RecordNotFoundException;
import com.fitassist.backend.mapper.recipe.RecipeMapper;
import com.fitassist.backend.model.recipe.Recipe;
import com.fitassist.backend.repository.RecipeRepository;
import com.fitassist.backend.service.declaration.helpers.JsonPatchService;
import com.fitassist.backend.service.declaration.helpers.RepositoryHelper;
import com.fitassist.backend.service.declaration.helpers.ValidationService;
import com.fitassist.backend.service.declaration.recipe.RecipePopulationService;
import com.fitassist.backend.service.declaration.recipe.RecipeService;
import com.fitassist.backend.service.implementation.specification.SpecificationDependencies;
import com.fitassist.backend.specification.SpecificationBuilder;
import com.fitassist.backend.specification.SpecificationFactory;
import com.fitassist.backend.specification.specification.RecipeSpecification;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

	private final RecipeMapper recipeMapper;

	private final JsonPatchService jsonPatchService;

	private final ValidationService validationService;

	private final ApplicationEventPublisher applicationEventPublisher;

	private final RepositoryHelper repositoryHelper;

	private final RecipeRepository recipeRepository;

	private final RecipePopulationService recipePopulationService;

	private final SpecificationDependencies dependencies;

	public RecipeServiceImpl(RecipeMapper recipeMapper, JsonPatchService jsonPatchService,
			ValidationService validationService, ApplicationEventPublisher applicationEventPublisher,
			RepositoryHelper repositoryHelper, RecipeRepository recipeRepository,
			RecipePopulationService recipePopulationService, SpecificationDependencies dependencies) {
		this.recipeMapper = recipeMapper;
		this.jsonPatchService = jsonPatchService;
		this.validationService = validationService;
		this.applicationEventPublisher = applicationEventPublisher;
		this.repositoryHelper = repositoryHelper;
		this.recipeRepository = recipeRepository;
		this.recipePopulationService = recipePopulationService;
		this.dependencies = dependencies;
	}

	@Override
	@Transactional
	public RecipeResponseDto createRecipe(RecipeCreateDto request) {
		int userId = AuthorizationUtil.getUserId();
		Recipe mapped = recipeMapper.toEntity(request, userId);
		Recipe saved = recipeRepository.save(mapped);
		applicationEventPublisher.publishEvent(RecipeCreateEvent.of(this, saved));

		recipeRepository.flush();

		return findAndMap(saved.getId());
	}

	@Override
	@Transactional
	public void updateRecipe(int recipeId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		Recipe recipe = find(recipeId);
		RecipeUpdateDto patchedRecipeUpdateDto = applyPatchToRecipe(patch);

		validationService.validate(patchedRecipeUpdateDto);
		recipeMapper.updateRecipe(recipe, patchedRecipeUpdateDto);
		Recipe savedRecipe = recipeRepository.save(recipe);

		applicationEventPublisher.publishEvent(RecipeUpdateEvent.of(this, savedRecipe));
	}

	@Override
	@Transactional
	public void deleteRecipe(int recipeId) {
		Recipe recipe = find(recipeId);
		recipeRepository.delete(recipe);

		applicationEventPublisher.publishEvent(RecipeDeleteEvent.of(this, recipe));
	}

	@Override
	@Cacheable(value = CacheNames.RECIPES, key = "#id")
	public RecipeResponseDto getRecipe(int id) {
		return findAndMap(id);
	}

	@Override
	public Page<RecipeSummaryDto> getFilteredRecipes(FilterDto filter, Pageable pageable) {
		SpecificationFactory<Recipe> recipeFactory = RecipeSpecification::new;
		SpecificationBuilder<Recipe> specificationBuilder = SpecificationBuilder.of(filter, recipeFactory,
				dependencies);
		Specification<Recipe> specification = specificationBuilder.build();

		Page<Recipe> recipePage = recipeRepository.findAll(specification, pageable);
		List<RecipeSummaryDto> summaries = recipePage.getContent().stream().map(recipeMapper::toSummaryDto).toList();
		recipePopulationService.populate(summaries);

		return new PageImpl<>(summaries, pageable, recipePage.getTotalElements());
	}

	@Override
	public List<Recipe> getAllRecipeEntities() {
		return recipeRepository.findAllWithoutAssociations();
	}

	@Override
	@Transactional
	public Long incrementViews(int recipeId) {
		recipeRepository.incrementViews(recipeId);
		return recipeRepository.getViews(recipeId);
	}

	private Recipe find(int recipeId) {
		return repositoryHelper.find(recipeRepository, Recipe.class, recipeId);
	}

	private RecipeResponseDto findAndMap(int recipeId) {
		Recipe recipe = recipeRepository.findByIdWithDetails(recipeId)
			.orElseThrow(() -> RecordNotFoundException.of(Recipe.class, recipeId));

		RecipeResponseDto dto = recipeMapper.toResponseDto(recipe);
		recipePopulationService.populate(dto);
		return dto;
	}

	private RecipeUpdateDto applyPatchToRecipe(JsonMergePatch patch)
			throws JsonPatchException, JsonProcessingException {
		return jsonPatchService.createFromPatch(patch, RecipeUpdateDto.class);
	}

}
