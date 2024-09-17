package com.example.simplefullstackproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "recipe_category_association")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCategoryAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "recipe_category_id", nullable = false)
    private RecipeCategory recipeCategory;
}
