package source.code.model.Activity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import source.code.model.User.UserActivity;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "activity")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
  private static final int NAME_MAX_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  @Column(nullable = false, length = NAME_MAX_LENGTH)
  private String name;

  @NotNull
  @Positive
  @Column(nullable = false)
  private double met;

  @NotNull
  @ManyToOne
  @JoinColumn(name = "activity_category_id", nullable = false)
  private ActivityCategory activityCategory;

  @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
  private final Set<DailyActivityItem> dailyActivityItems = new HashSet<>();

  @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
  private final Set<UserActivity> userActivities = new HashSet<>();

  public static Activity createWithIdNameMet(int id, String name, double met) {
    Activity activity = new Activity();
    activity.setId(id);
    activity.setName(name);
    activity.setMet(met);
    return activity;
  }

  public static Activity createWithId(int id) {
    Activity activity = new Activity();
    activity.setId(id);
    return activity;
  }
}