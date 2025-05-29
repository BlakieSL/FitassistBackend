package source.code.service.implementation.user.interaction.withType;

import org.springframework.stereotype.Service;
import source.code.dto.response.recipe.RecipeResponseDto;
import source.code.exception.RecordNotFoundException;
import source.code.mapper.recipe.RecipeMapper;
import source.code.model.recipe.Recipe;
import source.code.model.user.profile.User;
import source.code.model.user.UserRecipe;
import source.code.repository.RecipeRepository;
import source.code.repository.UserRecipeRepository;
import source.code.repository.UserRepository;
import source.code.service.declaration.user.SavedService;

import java.util.List;

@Service("userRecipeService")
public class UserRecipeServiceImpl
        extends GenericSavedService<Recipe, UserRecipe, RecipeResponseDto>
        implements SavedService {


    public UserRecipeServiceImpl(UserRecipeRepository userRecipeRepository,
                                 RecipeRepository recipeRepository,
                                 UserRepository userRepository,
                                 RecipeMapper recipeMapper) {
        super(userRepository,
                recipeRepository,
                userRecipeRepository,
                recipeMapper::toResponseDto,
                Recipe.class);
    }


    @Override
    protected boolean isAlreadySaved(int userId, int entityId, short type) {
        return ((UserRecipeRepository) userEntityRepository)
                .existsByUserIdAndRecipeIdAndType(userId, entityId, type);
    }

    @Override
    protected UserRecipe createUserEntity(User user, Recipe entity, short type) {
        return UserRecipe.createWithUserRecipeType(user, entity, type);
    }

    @Override
    protected UserRecipe findUserEntity(int userId, int recipeId, short type) {
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
    protected List<UserRecipe> findAllByUserAndType(int userId, short type) {
        return ((UserRecipeRepository) userEntityRepository).findByUserIdAndType(userId, type);
    }

    @Override
    protected Recipe extractEntity(UserRecipe userRecipe) {
        return userRecipe.getRecipe();
    }

    @Override
    protected long countSaves(int recipeId) {
        return ((UserRecipeRepository) userEntityRepository)
                .countByRecipeIdAndType(recipeId, (short) 1);
    }

    @Override
    protected long countLikes(int recipeId) {
        return ((UserRecipeRepository) userEntityRepository)
                .countByRecipeIdAndType(recipeId, (short) 2);
    }
}