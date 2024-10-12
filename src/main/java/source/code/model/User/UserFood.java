package source.code.model.User;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.Food.Food;

@Entity
@Table(name = "user_food")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFood {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "food_id", nullable = false)
  private Food food;

  @NotNull
  @Column(nullable = false)
  private short type;

  public static UserFood createWithUserFoodType(
          User user, Food food, short type) {

    UserFood userFood = new UserFood();
    userFood.setUser(user);
    userFood.setFood(food);
    userFood.setType(type);

    return userFood;
  }
}
