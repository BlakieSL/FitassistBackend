package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.FoodAdditionDto;
import com.example.simplefullstackproject.dto.FoodCalculatedDto;
import com.example.simplefullstackproject.dto.FoodCategoryDto;
import com.example.simplefullstackproject.dto.FoodDto;
import com.example.simplefullstackproject.model.ActivityCategory;
import com.example.simplefullstackproject.model.Food;
import com.example.simplefullstackproject.model.FoodCategory;
import com.example.simplefullstackproject.repository.FoodCategoryRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class FoodMapper {
    @Autowired
    private FoodCategoryRepository foodCategoryRepository;

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    public abstract FoodDto toDto(Food food);

    @Mapping(target = "categoryName", source = "foodCategory.name")
    @Mapping(target = "categoryId", source = "foodCategory.id")
    @Mapping(target = "amount", expression = "java((int) (factor * 100))")
    public abstract FoodCalculatedDto toDtoWithFactor(Food food, @Context double factor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "foodCategory", source = "categoryId", qualifiedByName = "categoryIdToFoodCategory")
    public abstract Food toEntity(FoodAdditionDto dto);

    public abstract FoodCategoryDto toCategoryDto(FoodCategory foodCategory);

    @AfterMapping
    protected void calculateMacros(@MappingTarget FoodCalculatedDto dto, @Context double factor) {
        dto.setCalories(dto.getCalories() * factor);
        dto.setProtein(dto.getProtein() * factor);
        dto.setFat(dto.getFat() * factor);
        dto.setCarbohydrates(dto.getCarbohydrates() * factor);
    }

    @Named("categoryIdToFoodCategory")
    protected FoodCategory categoryIdToFoodCategory(int categoryId) {
        return foodCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Food category with id: " + categoryId + " not found"));
    }
}