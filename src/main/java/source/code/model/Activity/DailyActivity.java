package source.code.model.Activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.User.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_activity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyActivity {
  @OneToMany(mappedBy = "dailyActivity",
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
          orphanRemoval = true)
  private final List<DailyActivityItem> dailyActivityItems = new ArrayList<>();
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  @Column(nullable = false)
  private LocalDate date;
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static DailyActivity createForToday(User user) {
    DailyActivity dailyActivity = new DailyActivity();
    dailyActivity.setDate(LocalDate.now());
    dailyActivity.setUser(user);

    return dailyActivity;
  }

  public static DailyActivity createWithIdUser(int id, User user) {
    DailyActivity dailyActivity = new DailyActivity();
    dailyActivity.setId(id);
    dailyActivity.setUser(user);
    return dailyActivity;
  }
}
