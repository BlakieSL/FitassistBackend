package com.example.simplefullstackproject.Services.Mappers;

import com.example.simplefullstackproject.Dtos.RecipeDto;
import com.example.simplefullstackproject.Models.Recipe;
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
