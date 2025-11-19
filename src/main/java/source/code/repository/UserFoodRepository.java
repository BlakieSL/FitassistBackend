package source.code.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import source.code.model.user.UserFood;

import java.util.List;
import java.util.Optional;

public interface UserFoodRepository extends JpaRepository<UserFood, Integer> {
    boolean existsByUserIdAndFoodId(int userId, int foodId);

    Optional<UserFood> findByUserIdAndFoodId(int userId, int foodId);

    List<UserFood> findByUserId(int userId);

    @Query("""
           SELECT uf FROM UserFood uf
           JOIN FETCH uf.food f
           JOIN FETCH f.foodCategory
           LEFT JOIN FETCH f.mediaList
           WHERE uf.user.id = :userId
           """)
    List<UserFood> findAllByUserIdWithMedia(@Param("userId") int userId, Sort sort);

    long countByFoodId(int foodId);
}