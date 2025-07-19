package source.code.model.daily;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.activity.Activity;

@Entity
@Table(name = "daily_cart_activity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyCartActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Positive
    @Column(nullable = false)
    private int time;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "daily_cart_id", nullable = false)
    private DailyCart dailyCart;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    public static DailyCartActivity of(
            Activity activity,
            DailyCart dailyCart,
            int time
    ) {
        DailyCartActivity dailyCartActivity = new DailyCartActivity();
        dailyCartActivity.setActivity(activity);
        dailyCartActivity.setDailyCart(dailyCart);
        dailyCartActivity.setTime(time);
        return dailyCartActivity;
    }
}
