package source.code.model.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.food.Food;

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

    public static UserFood of(User user, Food food) {
        UserFood userFood = new UserFood();
        userFood.setUser(user);
        userFood.setFood(food);

        return userFood;
    }
}
