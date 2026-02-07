package com.fitassist.backend.model.food;

import com.fitassist.backend.model.CategoryBase;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food_category")
@Getter
@Setter
public class FoodCategory extends CategoryBase {

	@OneToMany(mappedBy = "foodCategory")
	private final Set<Food> foods = new HashSet<>();

}
