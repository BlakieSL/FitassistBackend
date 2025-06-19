package source.code.model.activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.daily.DailyCart;

@Entity
@Table(name = "daily_cart_activity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivityItem {
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

    public static DailyActivityItem of(
            Activity activity,
            DailyCart dailyCart,
            int time
    ) {
        DailyActivityItem dailyActivityItem = new DailyActivityItem();
        dailyActivityItem.setActivity(activity);
        dailyActivityItem.setDailyCart(dailyCart);
        dailyActivityItem.setTime(time);
        return dailyActivityItem;
    }
}
