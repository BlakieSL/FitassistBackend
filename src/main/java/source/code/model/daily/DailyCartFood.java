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

import java.math.BigDecimal;

@Entity
@Table(name = "daily_cart_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartFood {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private BigDecimal quantity;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "daily_cart_id", nullable = false)
    @JsonBackReference
    private DailyCart dailyCart;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    public static DailyCartFood of(Food food, DailyCart dailyCart, BigDecimal quantity) {
        DailyCartFood dailyCartFood = new DailyCartFood();
        dailyCartFood.setQuantity(quantity);
        dailyCartFood.setFood(food);
        dailyCartFood.setDailyCart(dailyCart);
        return dailyCartFood;
    }
}
