package source.code.dto.response.food;

import lombok.*;
import source.code.helper.BaseUserEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class FoodResponseDto implements BaseUserEntity {
    private Integer id;
    @ToString.Include
    private String name;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal fat;
    private BigDecimal carbohydrates;
    private int categoryId;
    private String categoryName;
    private String imageName;
    private String firstImageUrl;
    private LocalDateTime userFoodInteractionCreatedAt;

    public FoodResponseDto(Integer id, String name, BigDecimal calories, BigDecimal protein,
                           BigDecimal fat, BigDecimal carbohydrates, int categoryId,
                           String categoryName, String imageName, String firstImageUrl) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageName = imageName;
        this.firstImageUrl = firstImageUrl;
    }

    public FoodResponseDto(Integer id, String name, BigDecimal calories, BigDecimal protein,
                           BigDecimal fat, BigDecimal carbohydrates, int categoryId,
                           String categoryName, String imageName, String firstImageUrl,
                           LocalDateTime userFoodInteractionCreatedAt) {
        this.id = id;
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageName = imageName;
        this.firstImageUrl = firstImageUrl;
        this.userFoodInteractionCreatedAt = userFoodInteractionCreatedAt;
    }
}