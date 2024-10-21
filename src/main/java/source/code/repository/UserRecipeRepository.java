package source.code.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import source.code.model.User.UserRecipe;

import java.util.List;
import java.util.Optional;

public interface UserRecipeRepository extends JpaRepository<UserRecipe, Integer> {
  List<UserRecipe> findByUserId(int userId);

  List<UserRecipe> findByUserIdAndType(int userId, short type);

  Optional<UserRecipe> findByUserIdAndRecipeIdAndType(int userId, int recipeId, short type);

  boolean existsByUserIdAndRecipeIdAndType(int userId, int recipeId, short type);

  long countByRecipeIdAndType(int recipeId, short type);
}