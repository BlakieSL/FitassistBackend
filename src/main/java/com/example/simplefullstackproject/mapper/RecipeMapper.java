package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.RecipeCategoryDto;
import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.model.Recipe;
import com.example.simplefullstackproject.model.RecipeCategory;
import com.example.simplefullstackproject.repository.RecipeCategoryRepository;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {
    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;

    @Mapping(target = "categoryName", source = "recipeCategory.name")
    public abstract RecipeDto toDto(Recipe recipe);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recipeCategory", source = "categoryName", qualifiedByName = "categoryNameToRecipeCategory")
    public abstract Recipe toEntity(RecipeDto dto);

    public abstract RecipeCategoryDto toCategoryDto(RecipeCategory recipeCategory);

    @Named("categoryNameToRecipeCategory")
    protected RecipeCategory categoryNameToRecipeCategory(String categoryName) {
        return recipeCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new NoSuchElementException("Category not found"));
    }
}
