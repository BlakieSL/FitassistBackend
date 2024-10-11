package source.code.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_food")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DailyFood {
  @OneToMany(mappedBy = "dailyFoodFood",
          cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
          orphanRemoval = true)
  @JsonManagedReference
  private final List<DailyFoodItem> dailyFoodItems = new ArrayList<>();
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @NotNull
  @Column(nullable = false)
  private LocalDate date;
  @OneToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public static DailyFood createForToday(User user) {
    DailyFood dailyFood = new DailyFood();
    dailyFood.setDate(LocalDate.now());
    dailyFood.setUser(user);
    return dailyFood;
  }
}
