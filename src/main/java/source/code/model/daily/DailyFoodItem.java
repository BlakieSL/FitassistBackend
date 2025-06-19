package source.code.model.daily;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.food.Food;

@Entity
@Table(name = "daily_cart_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyFoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private int amount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "daily_cart_id", nullable = false)
    @JsonBackReference
    private DailyCart dailyCart;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    public static DailyFoodItem of(Food food, DailyCart dailyCart, int amount) {
        DailyFoodItem dailyFoodItem = new DailyFoodItem();
        dailyFoodItem.setAmount(amount);
        dailyFoodItem.setFood(food);
        dailyFoodItem.setDailyCart(dailyCart);
        return dailyFoodItem;
    }
}
