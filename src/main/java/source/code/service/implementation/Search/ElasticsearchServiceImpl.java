package source.code.service.implementation.Search;

import org.springframework.stereotype.Service;
import source.code.dto.response.Other.SearchResultDto;
import source.code.helper.enumerators.EntityType;
import source.code.mapper.Search.SearchMapper;
import source.code.repository.Elasticsearch.*;
import source.code.service.declaration.Search.ElasticsearchService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
  private final SearchMapper mapper;
  private final FoodElasticsearchRepository foodRepository;
  private final ActivityElasticsearchRepository activityRepository;
  private final ExerciseElasticsearchRepository exerciseRepository;
  private final RecipeElasticsearchRepository recipeRepository;
  private final PlanElasticsearchRepository planRepository;

  public ElasticsearchServiceImpl(SearchMapper mapper,
                                  FoodElasticsearchRepository foodRepository,
                                  ActivityElasticsearchRepository activityRepository,
                                  ExerciseElasticsearchRepository exerciseRepository,
                                  RecipeElasticsearchRepository recipeRepository,
                                  PlanElasticsearchRepository planRepository) {
    this.mapper = mapper;
    this.foodRepository = foodRepository;
    this.activityRepository = activityRepository;
    this.exerciseRepository = exerciseRepository;
    this.recipeRepository = recipeRepository;
    this.planRepository = planRepository;
  }

  @Override
  public List<SearchResultDto<?>> searchAll(String query) {
    return Stream.of(
                    foodRepository.findByNameContainingIgnoreCase(query).stream()
                            .map(mapper::toFoodSearchResult),
                    activityRepository.findByNameContainingIgnoreCase(query).stream()
                            .map(mapper::toActivitySearchResult),
                    exerciseRepository.findByNameContainingIgnoreCase(query).stream()
                            .map(mapper::toExerciseSearchResult),
                    recipeRepository.findByNameContainingIgnoreCase(query).stream()
                            .map(mapper::toRecipeSearchResult),
                    planRepository.findByNameContainingIgnoreCase(query).stream()
                            .map(mapper::toPlanSearchResult)
            )
            .flatMap(stream -> stream)
            .collect(Collectors.toList());
  }

}
