package source.code.service.implementation.user.interaction.withType;

import org.springframework.stereotype.Service;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.dto.response.recipe.RecipeSummaryDto;
import source.code.exception.NotSupportedInteractionTypeException;
import source.code.exception.RecordNotFoundException;
import source.code.helper.BaseUserEntity;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.TypeOfInteraction;
import source.code.model.user.User;
import source.code.model.user.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.aws.AwsS3Service;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userRecipeService")
public class UserRecipeServiceImpl
        extends GenericSavedService<Recipe, UserRecipe, RecipeResponseDto>
        implements SavedService {

    private final AwsS3Service awsS3Service;

    public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository,
                                 RecipeRepository recipeRepository,
                                 UserRepository userRepository,
                                 RecipeMapper recipeMapper,
                                 AwsS3Service awsS3Service) {
        super(userRepository,
                recipeRepository,
                userRecipeRepository,
                recipeMapper::toResponseDto,
                Recipe.class);
        this.awsS3Service = awsS3Service;
    }


    @Override
    public List<BaseUserEntity> getAllFromUser(int userId, TypeOfInteraction type) {
        List<RecipeSummaryDto> dtos = ((UserRecipeRepository) userEntityRepository)
                .findRecipeSummaryByUserIdAndType(userId, type);

        dtos.forEach(dto -> {
            if (dto.getAuthorImageUrl() != null) {
                dto.setAuthorImageUrl(awsS3Service.getImage(dto.getAuthorImageUrl()));
            }
            if (dto.getImageName() != null) {
                dto.setFirstImageUrl(awsS3Service.getImage(dto.getImageName()));
            }
        });

        return dtos.stream()
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