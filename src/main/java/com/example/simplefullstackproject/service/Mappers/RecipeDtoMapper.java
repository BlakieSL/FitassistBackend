package com.example.simplefullstackproject.service.Mappers;

import com.example.simplefullstackproject.dto.RecipeDto;
import com.example.simplefullstackproject.model.Recipe;
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
