package source.code.service.implementation.user.interaction.withType;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.helper.Enum.cache.CacheNames;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.helpers.RecipePopulationService;
import source.code.service.declaration.helpers.SortingService;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userRecipeService")
public class UserRecipeServiceImpl
        extends GenericSavedService<Recipe, UserRecipe, RecipeResponseDto>
        implements SavedService {

    private final RecipeMapper recipeMapper;
    private final RecipePopulationService recipePopulationService;
    private final SortingService sortingService;

    public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository,
                                 RecipeRepository recipeRepository,
                                 UserRepository userRepository,
                                 RecipeMapper recipeMapper,
                                 RecipePopulationService recipePopulationService,
                                 SortingService sortingService) {
        super(userRepository,
                recipeRepository,
                userRecipeRepository,
                recipeMapper::toResponseDto,
                Recipe.class);
        this.recipeMapper = recipeMapper;
        this.recipePopulationService = recipePopulationService;
        this.sortingService = sortingService;
    }

    @Override
    @CacheEvict(value = CacheNames.RECIPES, key = "#entityId")
    public void saveToUser(int entityId, TypeOfInteraction type) {
        super.saveToUser(entityId, type);
    }

    @Override
    @CacheEvict(value = CacheNames.RECIPES, key = "#entityId")
    public void deleteFromUser(int entityId, TypeOfInteraction type) {
        super.deleteFromUser(entityId, type);
    }

    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type, Sort.Direction sortDirection) {
        var summaries = ((RecipeRepository) entityRepository)
                .findInteractedByUserWithDetails(userId, type)
                .stream()
                .map(recipeMapper::toSummaryDto)
                .toList();

        recipePopulationService.populate(summaries);
        recipePopulationService.populateInteractionDates(summaries, userId, type);

        return summaries.stream()
                .sorted(sortingService.comparator(RecipeSummaryDto::getUserRecipeInteractionCreatedAt, sortDirection))
                .map(dto -> (BaseUserEntity) dto)
                .toList();
    }

    @Override
    protected boolean isAlreadySaved(int userId, int entityId, TypeOfInteraction type) {
        return ((UserRecipeRepository) userEntityRepository)
                .existsByUserIdAndRecipeIdAndType(userId, entityId, type);
    }

    @Override
    protected UserRecipe createUserEntity(User user, Recipe entity, TypeOfInteraction type) {
        if (!entity.getIsPublic()) {
            throw new NotSupportedInteractionTypeException("Cannot save private recipe");
        }
        return UserRecipe.createWithUserRecipeType(user, entity, type);
    }

    @Override
    protected UserRecipe findUserEntity(int userId, int recipeId, TypeOfInteraction type) {
        return ((UserRecipeRepository) userEntityRepository)
                .findByUserIdAndRecipeIdAndType(userId, recipeId, type)
                .orElseThrow(() -> RecordNotFoundException.of(
                        UserRecipe.class,
                        userId,
                        recipeId,
                        type
                ));
    }

    @Override
    protected List<UserRecipe> findAllByUserAndType(int userId, TypeOfInteraction type) {
        return ((UserRecipeRepository) userEntityRepository).findByUserIdAndType(userId, type);
    }

    @Override
    protected Recipe extractEntity(UserRecipe userRecipe) {
        return userRecipe.getRecipe();
    }

    @Override
    protected long countSaves(int recipeId) {
        return ((UserRecipeRepository) userEntityRepository)
                .countByRecipeIdAndType(recipeId, TypeOfInteraction.SAVE);
    }

    @Override
    protected long countLikes(int recipeId) {
        return ((UserRecipeRepository) userEntityRepository)
                .countByRecipeIdAndType(recipeId, TypeOfInteraction.LIKE);
    }
}