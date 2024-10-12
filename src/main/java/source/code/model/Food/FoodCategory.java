package source.code.model.Food;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodCategory {
  private static final int MAX_NAME_LENGTH = 255;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = MAX_NAME_LENGTH)
  private String name;

  @NotBlank
  private String iconUrl;

  @NotBlank
  private String gradient;

  @OneToMany(mappedBy = "foodCategory", cascade = CascadeType.REMOVE)
  private final Set<Food> foods = new HashSet<>();

  public static FoodCategory createWithIdName(int id, String name) {
    FoodCategory foodCategory = new FoodCategory();
    foodCategory.setId(id);
    foodCategory.setName(name);
    return foodCategory;
  }
}
