package source.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @ManyToOne
  @JoinColumn(name = "daily_activity_id", nullable = false)
  private DailyActivity dailyActivity;

  @ManyToOne
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  public static DailyActivityItem createWithIdActivityDailyActivity(
          int id,
          Activity activity,
          DailyActivity dailyActivity) {

    DailyActivityItem dailyActivityItem = new DailyActivityItem();
    dailyActivityItem.setId(id);
    dailyActivityItem.setActivity(activity);
    dailyActivityItem.setDailyActivity(dailyActivity);

    return dailyActivityItem;
  }

  public static DailyActivityItem createWithActivityDailyActivityTime(
          Activity activity,
          DailyActivity dailyActivity,
          int time) {

    DailyActivityItem dailyActivityItem = new DailyActivityItem();
    dailyActivityItem.setActivity(activity);
    dailyActivityItem.setDailyActivity(dailyActivity);
    dailyActivityItem.setTime(time);

    return dailyActivityItem;
  }
}
