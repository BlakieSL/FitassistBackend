package com.example.simplefullstackproject.mapper;

import com.example.simplefullstackproject.dto.RecipeAdditionDto;
import com.example.simplefullstackproject.dto.RecipeCategoryDto;
import com.example.simplefullstackproject.dto.RecipeCategoryShortDto;
import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.model.Recipe;
import com.example.simplefullstackproject.model.RecipeCategory;
import com.example.simplefullstackproject.model.RecipeCategoryAssociation;
import com.example.simplefullstackproject.repository.RecipeCategoryRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Mapper(componentModel = "spring")
public abstract class RecipeMapper {
    @Autowired
    private RecipeCategoryRepository recipeCategoryRepository;

    @Mapping(target = "categories", source = "recipeCategoryAssociations", qualifiedByName = "mapAssociationsToCategoryShortDto")
    public abstract RecipeDto toDto(Recipe recipe);

    @Mapping(target = "recipeCategoryAssociations", source = "categoryIds", qualifiedByName = "mapCategoryIdsToAssociations")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRecipes", ignore = true)
    @Mapping(target = "recipeFoods", ignore = true)
    public abstract Recipe toEntity(RecipeAdditionDto dto);

    public abstract RecipeCategoryDto toCategoryDto(RecipeCategory recipeCategory);

    @Named("mapCategoryIdsToAssociations")
    protected Set<RecipeCategoryAssociation> mapCategoryIdsToAssociations(List<Integer> categoryIds) {
        if (categoryIds == null) {
            return new HashSet<>();
        }

        Set<RecipeCategoryAssociation> associations = new HashSet<>();

        for (Integer categoryId : categoryIds) {
            RecipeCategory category = recipeCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NoSuchElementException(
                            "Category not found for id: " + categoryId));

            RecipeCategoryAssociation association = new RecipeCategoryAssociation();
            association.setRecipeCategory(category);
            associations.add(association);
        }

        return associations;
    }

    @Named("mapAssociationsToCategoryShortDto")
    protected List<RecipeCategoryShortDto> mapAssociationsToCategoryShortDto(Set<RecipeCategoryAssociation> associations) {
        return associations.stream()
                .map(association -> new RecipeCategoryShortDto(
                        association.getRecipeCategory().getId(),
                        association.getRecipeCategory().getName()
                ))
                .toList();
    }
}
