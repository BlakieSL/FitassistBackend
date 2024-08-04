package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.FoodCategoryDto;
import com.example.simplefullstackproject.dto.FoodDto;
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
    public abstract FoodDto toDto(Food food);

    @Mapping(target = "categoryName", source = "foodCategory.name")
    public abstract FoodDto toDtoWithFactor(Food food, @Context double factor);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "foodCategory", source = "categoryName", qualifiedByName = "categoryNameToFoodCategory")
    public abstract Food toEntity(FoodDto dto);

    public abstract FoodCategoryDto toCategoryDto(FoodCategory foodCategory);

    @AfterMapping
    protected void calculateMacros(@MappingTarget FoodDto dto, @Context double factor) {
        dto.setCalories(dto.getCalories() * factor);
        dto.setProtein(dto.getProtein() * factor);
        dto.setFat(dto.getFat() * factor);
        dto.setCarbohydrates(dto.getCarbohydrates() * factor);
    }

    @Named("categoryNameToFoodCategory")
    protected FoodCategory categoryNameToFoodCategory(String categoryName) {
        return foodCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
    }
}