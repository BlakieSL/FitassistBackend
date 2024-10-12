package source.code.model.Activity;

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
@Table(name = "activity_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityCategory {
  private static final int NAME_MAX_LENGTH = 50;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank
  @Size(max = NAME_MAX_LENGTH)
  private String name;

  @NotBlank
  private String iconUrl;

  @NotBlank
  private String gradient;

  @OneToMany(mappedBy = "activityCategory", cascade = CascadeType.REMOVE)
  private final Set<Activity> activities = new HashSet<>();

  public static ActivityCategory createWithIdName(int id, String name) {
    ActivityCategory activityCategory = new ActivityCategory();
    activityCategory.setId(id);
    activityCategory.setName(name);
    return activityCategory;
  }
}
