package com.example.simplefullstackproject.services.Mappers;

import com.example.simplefullstackproject.dtos.RecipeDto;
import com.example.simplefullstackproject.models.Recipe;
import org.springframework.stereotype.Service;

@Service
public class RecipeDtoMapper {
    public RecipeDto map(Recipe recipe) {
        return new RecipeDto(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getText()
        );
    }

    public Recipe map(RecipeDto request) {
        Recipe recipe = new Recipe();
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setText(request.getText());
        return recipe;
    }
}
